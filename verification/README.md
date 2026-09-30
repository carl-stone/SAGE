# SAGE verification

Run from the SAGE directory with Python 3.10+, a Java 11+ **JDK**, `make`, and `curl`:

```sh
make setup
make verify
```

Setup creates `.venv` and downloads ROBOT 1.9.10 into `.tools`. Python dependencies are pinned in `requirements-verify.txt`. The JDK is needed for Java's single-file source launcher. On Windows, the same commands work in WSL.

Verification reads the current example directly from the specification. It runs:

- **ROBOT/HermiT:** OWL-DL profile, consistency, and unsatisfiable-class checks.
- **pySHACL:** meta-validation of `sage.shacl.ttl`, validation of the example and an opposing-stance variant, and two small negative controls.
- **HermiT entailment checks:** the fourteen aliases, assumptions as premises, coexistence of opposing positions, attribution independent of recording, the Assertion–Stance distinction, and separation of an assessment from the assessed relationship.

The negative controls check the specific expected SHACL violation. Engine errors stop the check; they never count as successful detection. Non-entailments are queried through HermiT's OWLAPI interface, including whether the recorder is required to hold *any* stance. They are not inferred from missing triples in a serialized output.

## Verification scope

The specification and the two original Turtle files are unchanged. The checks cover the selected axioms in `sage.ttl`; upstream ontologies are not imported. SHACL uses exactly the alias and subclass/subproperty preparation in specification §5.

SAGE uses RDF reification, whose `rdf:Statement` class IRI is outside OWL 2 DL's reserved-vocabulary rules. For HermiT, the runner generates a temporary OWL view: `rdf:Statement`, `rdf:subject`, `rdf:predicate`, and `rdf:object` receive local `urn:sage:verification:` names. `owl-declarations.ttl` supplies external class/property declarations; documentary DCMI fields are treated as OWL annotations. All input triples are retained under this explicit mapping. OWL profile checks apply to this generated view, while SHACL checks the original RDF representation. This is a check of the OWL encoding, rather than a proof of the complete RDF-based semantics.

Inputs, logs, and the latest `summary.json` are written to `build/verification/`. The spec remains the authority for meanings and expected results. To inspect inputs without running either engine:

```sh
python3 verification/verify.py --prepare-only
```

That command requires RDFLib and reports the engine checks as **NOT RUN**. A normal `make verify` succeeds only when every stage completes.

Sources: [ROBOT setup](https://robot.obolibrary.org/), [profile checking](https://robot.obolibrary.org/validate-profile), [HermiT reasoning](https://robot.obolibrary.org/reason), [pySHACL](https://github.com/RDFLib/pySHACL), [OWLAPI entailment API](https://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html), and [OWL 2 reserved vocabulary](https://www.w3.org/TR/owl2-syntax/#Classes).
