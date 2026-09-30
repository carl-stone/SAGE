# Applied ontology changes

All seven approved decisions below were applied on 2026-09-30 to the specification, ontology, SHACL profile, examples, and verification files. Python preparation, pySHACL, OWL-DL profile, HermiT consistency/coherence, and Java entailment checks passed. The decision text is retained as the record of the approved rewrite.

## 1. SWAN Claim is a subclass of SEE Assertion

Add this alignment:

```turtle
swande:Claim rdfs:subClassOf rdo:assertion .
```

A claim remains a SWAN Claim and is also a SEE Assertion: one individual, both classifications. It inherits SEE's assertion relationships to its proposition, asserting agent, and arguments. No separate assertion individual or bridge relationship is needed solely to connect these classifications.

SWAN's established-status qualification specializes SEE's more general truth claim. The person making the assertion and the author or curator judging it established may differ; their attribution remains distinct.

## 2. Remove the dedicated assumption relationship

Remove `sage:assumes` and use SEE's existing `rdo:has_premise` relationship (currently aliased as `sage:hasPremise`). Revise the specification, ontology, shapes, example, and verification references together in the consolidated rewrite.

An assertion can serve as a premise without being established within that argument or classified as a SWAN Claim. The dedicated assumption relationship adds no sufficiently defined distinction to retain. Purely hypothetical reasoning remains a separate modeling question; this decision does not equate entertaining a proposition with asserting it.

## 3. Remove the dedicated discourse-assessment class

Remove `sage:DiscourseAssessment` and its dedicated requirements. Retain direct SWAN discourse relationships between contributions, including positive, negative, and neutral responses. Where an agent's interpretation of a relationship needs to be expressed, use an ordinary SEE assertion about that relationship; no dedicated assessment class is needed.

Revise the specification, ontology, shapes, example, and verification references together in the consolidated rewrite.

## 4. Remove the decision layer

Remove `sage:Decision` and its dedicated `sage:decidedBy` and `sage:selects` relationships. The choice-recording layer adds process and planning concepts beyond the intended unification of SEE and SWAN. Revise the specification, ontology, shapes, example, and verification references together in the consolidated rewrite.

## 5. Remove the stance layer

Remove `sage:Stance`, `sage:heldBy`, `sage:StanceScheme`, and the four values `sage:Accepts`, `sage:TentativelyAccepts`, `sage:Rejects`, and `sage:WithholdsJudgment`, along with their dedicated requirements. Use existing SEE assertions and SWAN discourse concepts where applicable, without retaining a separate four-category position scheme. Revise the specification, ontology, shapes, example, and verification references together in the consolidated rewrite.

## 6. Remove the added rationale and context relationships

Remove `sage:hasRationale` and `sage:inContext`, including their dedicated requirements and references throughout the specification, ontology, shapes, example, and verification files. Retain SEE's existing argument and conclusion relationships and the scientific qualifications expressed within propositions. Do not introduce replacement SAGE-specific relationships for these removals.

## 7. SWAN Hypothesis is a subclass of SEE Assertion

Add this alignment:

```turtle
swande:Hypothesis rdfs:subClassOf rdo:assertion .
```

A hypothesis remains a SWAN Hypothesis and ResearchStatement and is also a SEE Assertion: one individual, both classifications. Together with decision 1, both Claim and Hypothesis specialize Assertion. Hypothesis retains its provisional character; Claim retains its author/curator-regarded established character.

SEE's assertion relationships naturally apply to both: propositional content, asserting agent, expression and source, evidential and inference connections, premise and conclusion roles, and conjunctive parts. These relationships do not require an assertion to be established. No separate provisional-assertion class or bridge relationship is needed. Apply the alignment consistently in the consolidated specification, ontology, shapes, examples, and verification rewrite.

## Review refinements applied

The follow-up review changes are applied: Claims and Hypotheses obtain their formulation from the linked proposition, with optional descriptions; the selected schema and preparation include SWAN's discourse subproperties and four symmetric relationships; and the specification gives identity conventions and an example of later assertion versus correction of an existing record. A published DNA replication example exercises competing explanations. Verification covers these cases.
