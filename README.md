# SAGE

SAGE unifies SWAN's scientific discourse vocabulary with SEE's Reasoning and Discourse Ontology. A SWAN Claim or Hypothesis is also a SEE Assertion on the same individual, with its content, asserting agent, and argument connections. Direct SWAN relationships connect scientific contributions; their hierarchy and symmetry support queries at broader levels. Claims and Hypotheses use their proposition text without requiring a duplicate description.

Read [SAGE-Ontology-Specification.md](SAGE-Ontology-Specification.md) for the normative definition and worked example.

| File | Purpose |
|---|---|
| `SAGE-Ontology-Specification.md` | Scientific model, vocabulary, alignments, and structural requirements. |
| `sage.ttl` | Exact aliases and selected OWL/RDFS axioms, including the two SAGE alignments. |
| `sage.shacl.ttl` | Structural validation, including inherited Assertion requirements for Claims and Hypotheses. |
| `REWRITE-NOTES.md` | Applied design decisions for this rewrite. |

The specification and Turtle files can be used without the verification tools. Before applying SHACL, normalize aliases and expand the selected subclass/subproperty and symmetry axioms as described in specification §5.

The specification also explains identity across publications and corrections. [A published example](examples/README.md) models observations and competing explanations from Meselson and Stahl’s DNA replication experiment.

## Verification

With Python 3.10+, a Java 11+ JDK, make, and curl:

```sh
make setup
make verify
```

Setup downloads Python dependencies and ROBOT. Verification checks the specification's example with pySHACL and the selected ontology with ROBOT/HermiT, including Java entailment checks. Results go into `build/verification/`. See [verification/README.md](verification/README.md) for the checks and their scope.
