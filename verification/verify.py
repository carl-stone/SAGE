#!/usr/bin/env python3
"""Run standard OWL/SHACL tools against SAGE. See README.md for scope."""
from __future__ import annotations

import argparse
import hashlib
import importlib.metadata
import json
import re
import shutil
import subprocess
import sys
from pathlib import Path

from rdflib import Graph, Literal, Namespace, RDF, RDFS, OWL, URIRef

ROOT = Path(__file__).resolve().parents[1]
SAGE = Namespace('https://example.org/sage/ontology#')
RDO = Namespace('http://purl.org/see/rdo#')
SWAN = Namespace('http://purl.org/swan/2.0/discourse-elements/')
SWANDR = Namespace('http://purl.org/swan/2.0/discourse-relationships/')
PAV = Namespace('http://purl.org/pav/')
DC = Namespace('http://purl.org/dc/terms/')
SH = Namespace('http://www.w3.org/ns/shacl#')
EX = Namespace('https://example.org/project/')

def copy_graph(*graphs: Graph) -> Graph:
    result = Graph()
    for graph in graphs:
        for prefix, ns in graph.namespaces():
            result.bind(prefix, ns)
        for triple in graph:
            result.add(triple)
    return result


def prepare(data: Graph, schema: Graph) -> Graph:
    """Exactly spec §5: alias replacement, then subclass/subproperty/symmetry closure."""
    classes = dict(schema.subject_objects(OWL.equivalentClass))
    properties = dict(schema.subject_objects(OWL.equivalentProperty))
    result = Graph()
    for prefix, ns in data.namespaces():
        result.bind(prefix, ns)
    for s, p, o in data:
        result.add((s, properties.get(p, p), classes.get(o, o) if p == RDF.type else o))
    subclasses = list(schema.subject_objects(RDFS.subClassOf))
    subproperties = list(schema.subject_objects(RDFS.subPropertyOf))
    symmetric = list(schema.subjects(RDF.type, OWL.SymmetricProperty))
    while True:
        additions = set()
        for child, parent in subclasses:
            additions.update((s, RDF.type, parent) for s in result.subjects(RDF.type, child))
        for child, parent in subproperties:
            additions.update((s, parent, o) for s, o in result.subject_objects(child))
        for prop in symmetric:
            additions.update((o, prop, s) for s, o in result.subject_objects(prop))
        additions.difference_update(result)
        if not additions:
            return result
        for triple in additions:
            result.add(triple)


def owl_view(schema: Graph, declarations: Graph, data: Graph | None = None) -> Graph:
    """Add OWL declarations without renaming or changing the scientific triples."""
    result = copy_graph(schema, declarations, *([data] if data is not None else []))
    # Explicit individual declarations make the OWL parser's signature unambiguous.
    classes = set(result.subjects(RDF.type, OWL.Class))
    individuals = {s for s, c in result.subject_objects(RDF.type)
                   if isinstance(s, URIRef) and c in classes}
    for p in list(result.subjects(RDF.type, OWL.ObjectProperty)):
        for s, o in result.subject_objects(p):
            individuals.update(n for n in (s, o) if isinstance(n, URIRef))
    for individual in individuals:
        result.add((individual, RDF.type, OWL.NamedIndividual))
    return result


def shacl_checks(example: Graph, schema: Graph, shapes: Graph, out: Path, published: dict[str, Graph]) -> None:
    # This is orchestration of pySHACL, not a second SHACL implementation.
    from pyshacl import validate
    from pyshacl.errors import ValidationFailure

    def check(label: str, data: Graph, expected: bool, *, meta=False, failures=()):
        conforms, report, text = validate(
            data, shacl_graph=shapes, inference='none', meta_shacl=meta,
            do_owl_imports=False, advanced=False, js=False, inplace=False,
            allow_infos=False, allow_warnings=False)
        if isinstance(report, ValidationFailure) or not isinstance(report, Graph):
            raise RuntimeError(f'{label}: validator could not complete: {report}')
        (out / f'{label}.txt').write_text(text, encoding='utf-8')
        report.serialize(out / f'{label}.ttl', format='turtle')
        if bool(conforms) != expected:
            raise RuntimeError(f'{label}: unexpected conformance; see {label}.txt')
        for focus, path, component in failures:
            matched = any(
                (r, SH.focusNode, focus) in report and
                (path is None or (r, SH.resultPath, path) in report) and
                (r, SH.sourceConstraintComponent, component) in report
                for r in report.subjects(RDF.type, SH.ValidationResult))
            if not matched:
                raise RuntimeError(f'{label}: expected violation for {focus} / {path} was not reported')
        print(f'PASS {label}', flush=True)

    good = prepare(example, schema)
    check('shacl-example-and-meta', good, True, meta=True)
    for name, data in published.items():
        check(f'shacl-{name}', prepare(data, schema), True)
    # Isolated SWAN-only records exercise inherited targets without property-use
    # targets or argument links accidentally supplying Assertion validation.
    for kind in ('Claim', 'Hypothesis'):
        node = EX['standalone' + kind]
        complete = copy_graph(example)
        for triple in [(node, RDF.type, SWAN[kind]),
                       (node, PAV.authoredBy, EX.researcher),
                       (node, SAGE.asserts, EX.p2),
                       (node, SAGE.assertedBy, EX.researcher)]:
            complete.add(triple)
        check(f'shacl-{kind.lower()}-inherited', prepare(complete, schema), True)
        for alias, canonical in ((SAGE.asserts, RDO.is_assertion_asserting),
                                 (SAGE.assertedBy, RDO.is_assertion_made_by)):
            missing = copy_graph(complete)
            missing.remove((node, alias, None))
            check(f'shacl-{kind.lower()}-missing-{str(alias).split("#")[-1]}',
                  prepare(missing, schema), False,
                  failures=[(node, canonical, SH.MinCountConstraintComponent)])
        bare = copy_graph(complete)
        bare.remove((node, SAGE.asserts, None))
        bare.remove((node, SAGE.assertedBy, None))
        check(f'shacl-{kind.lower()}-type-only-control', prepare(bare, schema), False,
              failures=[(node, RDO.is_assertion_asserting, SH.MinCountConstraintComponent),
                        (node, RDO.is_assertion_made_by, SH.MinCountConstraintComponent)])
    missing_description = copy_graph(example)
    missing_description.remove((EX.neutralResponse, DC.description, None))
    check('shacl-generic-statement-needs-formulation', prepare(missing_description, schema), False,
          failures=[(EX.neutralResponse, None, SH.OrConstraintComponent)])
    missing_expression = copy_graph(example)
    missing_expression.remove((EX.p2, SAGE.expressedAs, None))
    check('shacl-proposition-needs-expression', prepare(missing_expression, schema), False,
          failures=[(EX.p2, RDO.is_proposition_expressed_in, SH.MinCountConstraintComponent)])
    bad_description = copy_graph(example)
    bad_description.add((EX.hypothesis, DC.description, Literal('   ')))
    check('shacl-optional-description-must-be-readable', prepare(bad_description, schema), False,
          failures=[(EX.hypothesis, DC.description, SH.NodeConstraintComponent)])
    bad = copy_graph(good)
    bad.set((EX.argument, RDO.has_premise, Literal('raw data instead of an assertion')))
    check('shacl-premise-type-control', bad, False,
          failures=[(EX.argument, RDO.has_premise, SH.ClassConstraintComponent)])


def run(command: list[str], label: str, out: Path) -> None:
    """A runtime failure is always an error, never a successful negative check."""
    proc = subprocess.run(command, capture_output=True, text=True, timeout=180)
    log = (proc.stdout or '') + (proc.stderr or '')
    (out / f'{label}.log').write_text(log, encoding='utf-8')
    if proc.returncode:
        raise RuntimeError(f'{label} exited {proc.returncode}; see {out / (label + ".log")}\n{log[-1600:]}')
    print(f'PASS {label}', flush=True)
    if label == 'owl-semantic-checks':
        print(proc.stdout.rstrip(), flush=True)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--robot-jar', type=Path, default=ROOT / '.tools/robot-1.9.10.jar')
    parser.add_argument('--out', type=Path, default=ROOT / 'build/verification')
    parser.add_argument('--prepare-only', action='store_true',
                        help='write inputs only; OWL and SHACL checks remain NOT RUN')
    args = parser.parse_args()
    out = args.out.resolve()
    out.mkdir(parents=True, exist_ok=True)
    example_paths = sorted((ROOT / 'examples').glob('*.ttl'))
    status = {'preparation': 'NOT RUN', 'pyshacl': 'NOT RUN', 'robot_hermit': 'NOT RUN'}
    summary = {'checks': status, 'scope': 'selected SAGE axioms with external OWL declarations',
               'source_sha256': {name: hashlib.sha256((ROOT / name).read_bytes()).hexdigest()
                                 for name in ['SAGE-Ontology-Specification.md', 'sage.ttl', 'sage.shacl.ttl']
                                 + [str(path.relative_to(ROOT)) for path in example_paths]}}
    (out / 'summary.json').write_text(json.dumps(summary, indent=2) + '\n')
    try:
        schema = Graph().parse(ROOT / 'sage.ttl', format='turtle')
        shapes = Graph().parse(ROOT / 'sage.shacl.ttl', format='turtle')
        declarations = Graph().parse(ROOT / 'verification/owl-declarations.ttl', format='turtle')
        if any(p not in (RDF.type, DC.source) for _, p, _ in declarations):
            raise RuntimeError('OWL support file must contain declarations and source annotations only')
        if any(schema.triples((None, OWL.imports, None))):
            raise RuntimeError('Ontology imports require an explicitly chosen verification scope')
        text = (ROOT / 'SAGE-Ontology-Specification.md').read_text(encoding='utf-8')
        blocks = re.findall(r'^```turtle\s*\n(.*?)^```\s*$', text, re.M | re.S)
        if len(blocks) != 1:
            raise RuntimeError('Expected one Turtle example in the specification; select the intended example explicitly')
        example = Graph().parse(data=blocks[0], format='turtle')
        required = [(EX.hypothesis, RDF.type, SWAN.Hypothesis),
                    (EX.conclusion, RDF.type, SWAN.Claim),
                    (EX.argument, SAGE.hasPremise, EX.hypothesis),
                    (EX.conclusion, PAV.createdBy, EX.assistant),
                    (EX.conclusion, PAV.curatedBy, EX.curator),
                    (EX.agreement, SWANDR.respondsPositivelyTo, EX.hypothesis),
                    (EX.disagreement, SWANDR.respondsNegativelyTo, EX.hypothesis),
                    (EX.neutralResponse, SWANDR.respondsNeutrallyTo, EX.hypothesis)]
        if not all(t in example for t in required):
            raise RuntimeError('Spec example changed; update the semantic checks to match')
        published = {path.stem: Graph().parse(path, format='turtle') for path in example_paths}
        for name, data in published.items():
            prepare(data, schema).serialize(out / f'{name}-prepared.ttl', format='turtle')
        prepared = prepare(example, schema)
        for node in (EX.hypothesis, EX.conclusion):
            if (node, RDF.type, RDO.assertion) not in prepared or (node, RDF.type, SWAN.ResearchStatement) not in prepared:
                raise RuntimeError(f'{node} did not inherit Assertion and ResearchStatement')
        if (EX.neutralResponse, RDF.type, RDO.assertion) in prepared:
            raise RuntimeError('A generic ResearchStatement must not automatically become an Assertion')
        for node in (EX.agreement, EX.disagreement, EX.neutralResponse):
            for relation in (SWANDR.respondsTo, SWANDR.refersTo, SWANDR.relatesTo):
                if (node, relation, EX.hypothesis) not in prepared:
                    raise RuntimeError('Discourse hierarchy did not expand')
            if (EX.hypothesis, SWANDR.respondsTo, node) in prepared:
                raise RuntimeError('A directed response was incorrectly reversed')
        if (EX.agreement, SWANDR.inconsistentWith, EX.disagreement) not in prepared:
            raise RuntimeError('Symmetric discourse relationship did not expand')
        if set(prepared) != set(prepare(prepared, schema)):
            raise RuntimeError('Preparation is not idempotent')
        example.serialize(out / 'example.ttl', format='turtle')
        prepared.serialize(out / 'example-prepared.ttl', format='turtle')
        owl_view(schema, declarations).serialize(out / 'schema-owl-view.ttl', format='turtle')
        owl_view(schema, declarations, copy_graph(example, *published.values())).serialize(out / 'scenario-owl-view.ttl', format='turtle')
        status['preparation'] = 'PASS'
        print('PASS parse and prepare current specification example', flush=True)
        if args.prepare_only:
            print('Prepared inputs only. OWL and SHACL engine checks: NOT RUN.', flush=True)
            return 0
        errors = []
        try:
            summary['pyshacl_version'] = importlib.metadata.version('pyshacl')
            shacl_checks(example, schema, shapes, out, published)
            status['pyshacl'] = 'PASS'
        except (ImportError, importlib.metadata.PackageNotFoundError) as exc:
            status['pyshacl'] = 'BLOCKED: install requirements-verify.txt'
            errors.append(str(exc))
        except Exception as exc:
            status['pyshacl'] = 'FAIL'
            errors.append(str(exc))
        java = shutil.which('java')
        jar = args.robot_jar.resolve()
        if not java or not jar.is_file():
            status['robot_hermit'] = 'BLOCKED: requires Java 11+ JDK and the ROBOT JAR; run make setup'
        else:
            try:
                cmd = [java, '-Xmx1g', '-jar', str(jar)]
                run(cmd + ['--version'], 'robot-version', out)
                run(cmd + ['validate-profile', '--profile', 'DL', '--input', str(out / 'schema-owl-view.ttl'),
                           '--output', str(out / 'owl-profile.txt')], 'owl-profile', out)
                run(cmd + ['reason', '--reasoner', 'hermit', '--equivalent-classes-allowed', 'all',
                           '--input', str(out / 'schema-owl-view.ttl'),
                           '--output', str(out / 'schema-reasoned.ttl')], 'owl-consistency-and-coherence', out)
                run([java, '-Xmx1g', '--class-path', str(jar),
                     str(ROOT / 'verification/SemanticChecks.java'), str(out / 'scenario-owl-view.ttl')],
                    'owl-semantic-checks', out)
                status['robot_hermit'] = 'PASS'
            except Exception as exc:
                status['robot_hermit'] = 'FAIL'
                errors.append(str(exc))
        summary['errors'] = errors
        for label, result in status.items():
            print(f'{label}: {result}', flush=True)
        for error in errors:
            print(error, file=sys.stderr)
        return 0 if all(value == 'PASS' for value in status.values()) else 2
    except Exception as exc:
        status['preparation'] = 'FAIL'
        summary['errors'] = [str(exc)]
        print(f'ERROR: {exc}', file=sys.stderr)
        return 2
    finally:
        (out / 'summary.json').write_text(json.dumps(summary, indent=2) + '\n', encoding='utf-8')


if __name__ == '__main__':
    raise SystemExit(main())
