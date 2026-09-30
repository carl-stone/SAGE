# SAGE verification

Run from the SAGE directory with Python 3.10+, a Java 11+ **JDK**, `make`, and `curl`:

```sh
make setup
make verify
```

Setup creates `.venv` and downloads ROBOT 1.9.10 into `.tools`. Python dependencies are pinned in `requirements-verify.txt`. The JDK supplies Java's single-file source launcher. The commands also work in WSL.

Verification reads the Turtle example directly from the normative specification and runs:

- **Python preparation:** parses all inputs, normalizes aliases, and expands the selected subclass/subproperty and symmetry axioms from specification §5. Checks that Claim and Hypothesis inherit Assertion and ResearchStatement, while a generic ResearchStatement does not inherit Assertion. Checks response generalization, directedness, symmetry, and preparation idempotence.
- **pySHACL:** validates the shapes themselves and the example. Standalone Claim and Hypothesis records conform with the required assertion fields and no duplicate descriptions. They fail with the expected focus node, path, and constraint when either or both required fields are missing. Generic statements still require a description, proposition expressions remain required, and optional descriptions must be readable. A malformed premise must fail its class constraint. The published example is also validated.
- **ROBOT/HermiT:** checks the OWL-DL profile, consistency, and unsatisfiable classes.
- **Java/HermiT entailments:** checks all fourteen aliases; both new subclass alignments and their individual-level effects; the preserved Question hierarchy; premises without Claim typing; independent asserting, curating, and recording roles; direct discourse relationships without added endpoint restrictions; inherited responses and symmetric relationships; and separate assertions sharing a proposition without merging or changing the earlier classification.

Engine errors fail verification. Negative entailment checks use HermiT's OWLAPI interface rather than absence from serialized output. A non-entailment means the ontology does not force that conclusion; it is not an assertion of its negation.

## Scope and outputs

The supplied ontology contains selected axioms, not full upstream imports. SHACL uses only the preparation defined in specification §5, with no extra inference. `owl-declarations.ttl` provides external class/property declarations for OWL parsing; documentary DCMI fields are treated as annotation properties. The OWL view preserves the input IRIs and scientific triples. There is no reification adapter.

Inputs, validation reports, engine logs, and `summary.json` are written to `build/verification/`. The summary identifies the current source file hashes and engine outcomes. To inspect inputs without running engines:

```sh
.venv/bin/python verification/verify.py --prepare-only
```

This requires RDFLib and reports engine checks as **NOT RUN**. `make verify` succeeds only when every stage passes.

Sources: [ROBOT](https://robot.obolibrary.org/), [OWL profile checking](https://robot.obolibrary.org/validate-profile), [HermiT reasoning](https://robot.obolibrary.org/reason), [pySHACL](https://github.com/RDFLib/pySHACL), and [OWLAPI entailments](https://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html).
