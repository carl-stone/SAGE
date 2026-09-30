# SAGE

SAGE represents scientific claims, evidence, arguments, and researchers’ positions in a shared knowledge graph.

## Start here

Read **SAGE-Ontology-Specification.md**. It defines the system and is the ultimate authority. The two Turtle files implement it for software.

| File | Purpose |
|---|---|
| `SAGE-Ontology-Specification.md` | The human-readable specification: concepts, relationships, sources, and structural requirements. |
| `sage.ttl` | The machine-readable ontology: vocabulary, aliases, and formal relationships. |
| `sage.shacl.ttl` | Rules for checking whether graph data has the structure specified in the document. |

These three files are the SAGE definition. They can be read and used without installing the verification tools.

## Optional verification tools

`verification/` contains the checking scripts, a small set of semantic checks, and the declarations needed by the OWL reasoner. Its `README.md` explains their scope. `Makefile` provides the commands; `requirements-verify.txt` lists the Python dependencies.

To run them, open a terminal in this `SAGE` folder. With Python 3.10+, a Java 11+ JDK, make, and curl installed:

```sh
make setup
make verify
```

Setup requires internet access to download Python dependencies and ROBOT. Those external tools are not bundled. Results go into `build/verification/`.

The verification setup has been prepared, but the ROBOT/HermiT and pySHACL engine checks have not yet run in the authoring environment because dependency downloads were blocked.

This package contains the latest specification and its matching files, plus the verification setup. It replaces the earlier downloads; old drafts, audit reports, and superseded test suites are omitted.
