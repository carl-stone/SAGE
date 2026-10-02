# SAGE Ontology Specification

**Version 0.1 — frozen 2026-10-02.**

**Scientific Argument Graph & Epistemology**

SAGE unifies SWAN's scientific discourse vocabulary and SEE's Reasoning and Discourse Ontology (RDO). It describes scientific contributions, their propositional content, the agents asserting them, and the arguments and discourse relationships connecting them. [SWAN] [SEE] [SAGE]

<a id="authority-and-scope"></a>
## 1. Authority and scope

This document is the normative authority for SAGE. Sections 1–5 define the vocabulary, alignments, and structural requirements implemented by `sage.ttl` and `sage.shacl.ttl`. Section 6 illustrates their use. External terms retain their source meanings; SAGE's added subclass alignments and validation requirements are identified explicitly. [SAGE]

SAGE combines existing scientific concepts through class overlap and exact aliases. A SWAN Claim or Hypothesis is also a SEE Assertion, represented by the same individual. Domain ontologies describe organisms, assays, samples, and other research entities. PAV, PROV, and Web Annotation supply authorship, provenance, and source selections. [SAGE] [SWAN-DE] [RDO] [PAV] [PROV] [OA]

The provisional namespaces are `https://example.org/sage/ontology#` for `sage:` and `https://example.org/sage/spec#` for `spec:`. Source vocabularies use `http://purl.org/see/rdo#` (`rdo:`), `http://purl.org/swan/2.0/discourse-elements/` (`swande:`), and `http://purl.org/swan/2.0/discourse-relationships/` (`swandr:`). Other prefixes follow their cited standards. [SAGE]

## 2. Scientific model

### Propositions, assertions, claims, and hypotheses

A **proposition** is truth-evaluable content; a **text** expresses it. An **assertion** puts a proposition forward as true on a particular occasion. Assertions can share content while differing in asserting agent, source, or justification. Assertion does not entail proof, certainty, or established status. RDO's `act_of_assertion` denotes the activity of asserting; `assertion` denotes the resulting claim. [RDO] [SEE]

A SWAN **Claim** is a research statement its author or curator regards as established. A SWAN **Hypothesis** provisionally puts an idea forward. SAGE makes both subclasses of SEE Assertion while retaining their SWAN ResearchStatement classification. Thus one individual has its SWAN classification, proposition, asserting agent, and argument connections. Both can have source and expression links, inference connections, and conjunctive parts through existing RDO relationships. [SWAN-DE] [RDO] [SAGE]

The alignment is specifically for Claim and Hypothesis. It does not classify every ResearchStatement as an Assertion or equate assertions with their propositions. SWAN Question and ResearchStatement remain sibling subclasses of DiscourseElement. [SWAN-DE] [SAGE]

### Attribution and scientific qualifications

The asserting agent is identified by `sage:assertedBy` (`rdo:is_assertion_made_by`). PAV separately identifies an intellectual author, curator, or creator of the digital representation. A Claim's author or curator may regard it as established while a different agent made the assertion. Recording or curating a contribution does not itself make the recorder or curator its asserting agent. [RDO] [PAV] [SAGE]

Proposition identity includes scientific qualifications such as organism, experimental system, comparison, measurement, and conditions. Equivalent formulations can express the same proposition. A conditional statement retains its conditions in its content. Omitted assertions leave information unspecified; they do not express suspension of judgment. [RDO] [SEE] [SAGE]

### Arguments and evidence

An **argument** offers premise assertions jointly as grounds for a conclusion assertion. `rdo:has_premise` (alias `sage:hasPremise`) and `rdo:has_conclusion` (alias `sage:hasConclusion`) connect those roles. Separate arguments can offer alternative justifications. A premise need not be established within the argument or classified as a SWAN Claim: an ordinary assertion or a Hypothesis can serve as a premise. Merely entertaining a proposition does not thereby assert it. [RDO] [SEE] [SAGE]

Assertions describing observations or computed results can supply premises. `sage:reportedIn` identifies a report in which an assertion was made. `prov:wasGeneratedBy` connects a report to its producing activity, and `prov:used` connects that activity to its inputs. Existing RDO inference and argument relationships express reasoning; scientific qualifications remain in propositions. [RDO] [PROV]

### Questions and discourse relationships

A **question** expresses an inquiry. Direct SWAN relationships connect contributions, including statements, questions, reports, and arguments. Positive and negative responses express agreement and disagreement; a neutral response is a contribution responding without either. These are discourse relationships, not biological responses to treatment or direct person-to-proposition belief relationships. Neutral response does not mean withholding judgment. [SWAN-DE] [SWAN-DR] [SAGE]

SWAN deliberately leaves the domain and range of discourse properties unspecified for reuse. SAGE adds no endpoint class restrictions. When an interpretation of a relationship needs its own attribution, an ordinary SEE assertion can express that interpretation in its proposition. Explanatory notes can use `rdfs:comment`. [SWAN-DR] [RDO] [RDFS] [SAGE]

<a id="aliases"></a>
## 3. Vocabulary

### 3.1 Exact aliases

Aliases retain their source definitions. Class aliases use `owl:equivalentClass`; property aliases use `owl:equivalentProperty` and retain the original direction. Arrows show subject-to-object direction. [OWL] [SAGE]

| SAGE alias | Original identifier | Meaning | Source |
|---|---|---|---|
| `sage:Proposition` | `rdo:proposition` | The content of a statement that can be true or false. | [RDO] |
| `sage:Assertion` | `rdo:assertion` | An agent's claim that a proposition is true on a particular occasion. | [RDO] |
| `sage:Argument` | `rdo:argument` | Premise assertions offered as grounds for a conclusion assertion. | [RDO] |
| `sage:Agent` | `rdo:agent` | A person, organization, or information-processing device contributing information to an inquiry. | [RDO] |
| `sage:Text` | `rdo:text` | A lexical expression in a natural or formal language. | [RDO] |
| `sage:Report` | `rdo:report` | An account of an event, situation, observation, or inquiry. | [RDO] |
| `sage:Question` | `swande:Question` | A research question. | [SWAN-DE] |
| `sage:asserts` | `rdo:is_assertion_asserting` | Assertion → proposition. | [RDO] |
| `sage:assertedBy` | `rdo:is_assertion_made_by` | Assertion → agent making the claim. | [RDO] |
| `sage:reportedIn` | `rdo:is_assertion_made_in` | Assertion → report or report part in which the claim was made. | [RDO] |
| `sage:hasPremise` | `rdo:has_premise` | Argument → premise assertion. | [RDO] |
| `sage:hasConclusion` | `rdo:has_conclusion` | Argument → conclusion assertion. | [RDO] |
| `sage:expressedAs` | `rdo:is_proposition_expressed_in` | Proposition → text expressing it. | [RDO] |
| `sage:text` | `rdo:has_lexical_structure` | Text → lexical literal. | [RDO] |

<a id="core"></a>
### 3.2 SAGE alignments

SAGE adds these two subclass axioms. They are SAGE alignments of the source concepts, not axioms published by SWAN or SEE. [SAGE]

| Subclass | Superclass | Effect |
|---|---|---|
| `swande:Claim` | `rdo:assertion` | The same contribution is a SWAN Claim, ResearchStatement, and SEE Assertion. |
| `swande:Hypothesis` | `rdo:assertion` | The same contribution is a SWAN Hypothesis, ResearchStatement, and SEE Assertion. |

The classes retain their respective established and provisional meanings. Neither alignment requires a second statement/assertion individual, a connecting property, or an additional status vocabulary. The fourteen aliases in §3.1 remain exact aliases. Section 5 lists the other selected structural mappings. [SAGE]

### 3.3 Reused vocabulary

The following terms retain their source definitions. Each citation applies to every term in its row. Properties describe their subjects in the roles specified by the source vocabulary. [SAGE]

| Existing terms | Meaning and use | Source |
|---|---|---|
| `rdo:report_part` | A portion of a report's narrative, including a figure, table, or discontinuous selection. RDO also classifies whole reports as report parts. | [RDO] |
| `rdo:act_of_assertion`, `rdo:act_of_inference` | The activities of asserting and drawing an inference. | [RDO] |
| `rdo:is_inferred_from` | Assertion → assertion from which it is inferred. | [RDO] |
| `rdo:has_conjunctive_part` | Assertion → assertion that forms a conjunctive part of it. | [RDO] |
| `rdo:is_assertion_based_on_report`, `rdo:is_assertion_based_on_text` | Assertion → report or text on which it is based. | [RDO] |
| `rdo:is_assertion_expressed_in` | Assertion → text expressing it in a report or report part. | [RDO] |
| `swande:DiscourseElement`, `swande:ResearchStatement` | Scientific discourse elements and research statements. | [SWAN-DE] |
| `swande:Hypothesis` | A provisional research statement. | [SWAN-DE] |
| `swande:Claim` | A research statement its author or curator regards as established. | [SWAN-DE] |
| `swandr:relatesTo`, `swandr:refersTo` | A generic connection and a directed reference, respectively. | [SWAN-DR] |
| `swandr:respondsTo`, `respondsNeutrallyTo`, `respondsPositivelyTo`, `respondsNegativelyTo` | A response, a neutral response, agreement, and disagreement, respectively. All names use `swandr:`. | [SWAN-DR] |
| `swandr:consistentWith`, `inconsistentWith`, `relevantTo`, `alternativeTo` | Symmetric relations expressing consistency, inconsistency, relevance, and alternative interpretations of the same results. All names use `swandr:`. | [SWAN-DR] |
| `swandr:motivates`, `swandr:arisesFrom` | Motivating item → item motivated; item → what prompted it, respectively. | [SWAN-DR] |
| `swandr:referencesAsSupportiveEvidence`, `referencesAsRelevantEvidence`, `referencesAsInconsistentEvidence` | References characterized as supportive, relevant, or inconsistent evidence. All names use `swandr:`. These glosses summarize the names of properties whose upstream comments are empty. | [SWAN-DR] |
| `pav:authoredBy`, `pav:curatedBy`, `pav:createdBy`, `pav:createdOn` | Intellectual author, curator, creator of the digital representation, and its creation time. SAGE uses the modern PAV identifiers. | [PAV] |
| `pav:createdWith` | A tool used to create a representation. | [PAV] |
| `prov:Entity`, `prov:Activity`, `prov:Agent` | A thing described in a provenance account, an occurrence that acts on entities, and an agent assigned responsibility. | [PROV] |
| `prov:Person`, `prov:Organization`, `prov:SoftwareAgent` | Categories of provenance agents. A resource can also have RDO agent typing when it participates in an inquiry. | [PROV] |
| `prov:Plan`, `prov:Role`, `prov:Association` | Intended steps, an activity role, and a qualified association with an agent. | [PROV] |
| `prov:used`, `prov:wasGeneratedBy`, `prov:wasDerivedFrom` | Activity → input entity; entity → generating activity; entity → entity from which it was derived. | [PROV] |
| `prov:wasAssociatedWith`, `prov:wasAttributedTo` | Activity → responsible agent; entity → attributed agent. | [PROV] |
| `prov:qualifiedAssociation`, `prov:agent`, `prov:hadPlan`, `prov:hadRole` | Activity → association; association → agent, plan, or role. The profile checks these qualified fields in their association use. | [PROV] |
| `prov:startedAtTime`, `prov:endedAtTime` | The start and end of an activity. | [PROV] |
| `oa:SpecificResource`, `oa:hasSource`, `oa:hasSelector` | A constrained resource, its source, and an optional selection mechanism. A report selection can also have `rdo:report_part` typing. | [OA] |
| `oa:TextQuoteSelector`, `oa:exact`, `oa:prefix`, `oa:suffix` | A text selection identified by an exact normalized quote and optional surrounding text. | [OA] |
| `oa:FragmentSelector`, `rdf:value`, `dcterms:conformsTo` | A fragment selection, its fragment string, and the identifier of its syntax. | [OA] [RDFS] [DC] |
| `dcterms:source`, `dcterms:title`, `dcterms:description` | A resource from which content is derived, a name, and a descriptive account. Sources can be any referenced resource. | [DC] |
| `dcterms:identifier`, `dcterms:issued` | A resource identifier and issuance date. | [DC] |
| `rdfs:label`, `rdfs:comment` | A readable label and comment. SAGE also uses comments for informal explanations. | [RDFS] |
| `skos:changeNote` | Notes identifying how a vocabulary term or axiom is adapted. | [SKOS] |

The files use RDF and Turtle for representation, RDFS and OWL for vocabulary declarations, SHACL for constraints, and XSD for datatypes. Component annotations identify sources with `dcterms:source`, normative definitions with `rdfs:isDefinedBy`, and adaptations with `skos:changeNote`. Each of these standards is used with its original semantics. [RDF] [RDFS] [TURTLE] [OWL] [SHACL] [XSD] [DC] [SKOS]

<a id="constraints"></a>
## 4. Structural requirements

The tables define the supplied SHACL profile. A conforming description supplies the required fields and satisfies the stated value constraints. Partial descriptions can be retained and expanded; validation reports identify unmet requirements. Attribution fields name agents in their stated roles. [RDO] [PAV] [SHACL] [SAGE]

All cardinalities and validation targets are SAGE profile choices. `1` means exactly one value, `1+` one or more, and `0+` any number, including zero. Referenced resources can be IRIs or blank nodes, except where a field specifically requires an IRI. A readable value is an `xsd:string` or `rdf:langString` containing at least one non-whitespace character. The profile permits additional properties. [RDF] [XSD] [SHACL] [SAGE]

In the tables, **Record** selects the shared `RecordShape` constraints: `prov:Entity` typing, the optional metadata fields, and explanations. Entity and Activity are disjoint in the profile. Class names use SAGE aliases where available; Entity, Activity, Plan, Role, and Association denote PROV classes. [PROV] [SAGE]

### Scientific records

| Type | Required structure and permitted fields | Source vocabulary |
|---|---|---|
| Agent | RDO agent type; `rdfs:label` (0+ readable values). | [RDO] [RDFS] |
| Proposition | Record; proposition type; `expressedAs` (1+ Texts). | [RDO] [SEE] |
| Text | Record; text type; `text` (1 readable literal). | [RDO] |
| Report or report part | Record; RDO report-part type after §5 preparation; `dcterms:title` (0+ readable values). | [RDO] [DC] |
| Assertion | Record; assertion type; `asserts` (1 Proposition), `assertedBy` (1+ Agents), `reportedIn` (0+ report parts). | [RDO] [SEE] |
| Argument | Record; argument type; `hasPremise` (1+ Assertions), `hasConclusion` (1 Assertion), `pav:authoredBy` (1+ Agents). | [RDO] [SEE] [PAV] |
| SWAN ResearchStatement | Record; ResearchStatement type; `pav:authoredBy` (1+ Agents). If also an Assertion, its proposition supplies its formulation; otherwise `dcterms:description` (1+ readable formulations) is required. Any supplied description must be readable. | [SWAN-DE] [PAV] |
| SWAN Claim or Hypothesis | All ResearchStatement **and** Assertion requirements above, inherited after §5 preparation. The linked proposition supplies the formulation; `dcterms:description` is optional descriptive metadata, not a second required copy of the content. | [SWAN-DE] [RDO] [SAGE] |
| Question | Record; question type; `dcterms:description` (1+ readable formulations), `pav:authoredBy` (1+ Agents). | [SWAN-DE] [PAV] |
| Shared optional fields | On each Record: `pav:createdBy` and `pav:curatedBy` (0+ RDO Agents), `pav:createdWith` (0+ tool resources), `pav:createdOn` (0–1 `xsd:dateTime`), `dcterms:source` (0+ resources), `rdfs:comment` (0+ readable notes). | [PAV] [DC] [PROV] [XSD] [SAGE] |

`reportedIn` identifies where a claim was made; `dcterms:source` identifies a resource from which the description was drawn. Both are optional. Several Texts linked to a Proposition express the same identified content. A description may summarize a contribution; its asserted content is determined by its linked Proposition. SHACL checks structure and readable values, not semantic agreement between prose fields. [RDO] [DC] [SAGE]

### Provenance and source selections

These checks apply when the corresponding structures are present. Date and time fields use `xsd:dateTime`, which supports values with or without a timezone. Auxiliary resources can use either IRIs or blank nodes. [PROV] [OA] [RDF] [XSD] [SAGE]

| Type | Structural requirements | Source vocabulary |
|---|---|---|
| Entity | Entity type. Optional `wasGeneratedBy` → Activity, `wasDerivedFrom` → Entity, `wasAttributedTo` → PROV Agent. | [PROV] |
| Activity | Activity type. Optional `used` → Entity, `wasAssociatedWith` → PROV Agent, `qualifiedAssociation` → Association. Start and end times are optional, at most one each. Plans are attached through an Association. | [PROV] |
| PROV Agent | PROV Agent type, including its normalized Person, Organization, and SoftwareAgent subclasses. | [PROV] |
| Association | Association type; `prov:agent` (1 PROV Agent), `prov:hadPlan` (0–1 Plan), `prov:hadRole` (0+ Roles). | [PROV] |
| SpecificResource | SpecificResource type; `oa:hasSource` (1 resource), `oa:hasSelector` (0+ selector resources). | [OA] |
| TextQuoteSelector | Selector type; `oa:exact` (1 non-whitespace `xsd:string`), `oa:prefix` and `oa:suffix` (0–1 string each). | [OA] |
| FragmentSelector | Selector type; `rdf:value` (1 non-whitespace string), `dcterms:conformsTo` (0–1 syntax IRI). | [OA] [RDFS] [DC] |

### Validation targets

The file uses SHACL Core, with violation severity for all checks. Shapes apply to their target classes and the property uses below after §5 preparation. Record and ReadableText constraints apply through their parent shapes. Source and adaptation annotations accompany the named shapes and field constraints. [SHACL] [SAGE]

| Shape | Additional targets, using canonical predicates |
|---|---|
| Agent | Objects of `rdo:is_assertion_made_by`. PAV field types are checked within the records that use them. |
| Proposition | Subjects of `rdo:is_proposition_expressed_in` and objects of `rdo:is_assertion_asserting`. |
| Text | Subjects of `rdo:has_lexical_structure` and objects of `rdo:is_proposition_expressed_in`. |
| Assertion | Subjects of `rdo:is_assertion_asserting`, `rdo:is_assertion_made_by`, and `rdo:is_assertion_made_in`; objects of `rdo:has_premise` and `rdo:has_conclusion`. |
| Argument | Subjects of `rdo:has_premise` and `rdo:has_conclusion`. |
| Report | Objects of `rdo:is_assertion_made_in`. |
| Entity | Subjects of `prov:wasGeneratedBy`, `prov:wasDerivedFrom`, and `prov:wasAttributedTo`; objects of `prov:used` and `prov:wasDerivedFrom`. |
| Activity | Subjects of `prov:used`, `prov:wasAssociatedWith`, `prov:qualifiedAssociation`, `prov:startedAtTime`, and `prov:endedAtTime`; objects of `prov:wasGeneratedBy`. |
| PROV Agent | Objects of `prov:wasAssociatedWith`, `prov:wasAttributedTo`, and `prov:agent`. |
| Association | Subjects of `prov:hadPlan` and objects of `prov:qualifiedAssociation`. |
| SpecificResource | Subjects of `oa:hasSource` and `oa:hasSelector`. |
| TextQuoteSelector | Subjects of `oa:exact`. |

Question, ResearchStatement, and FragmentSelector use class targets only. Claim and Hypothesis inherit both ResearchStatement and Assertion targets; they need no separate shapes. Report targets `rdo:report_part`, including whole reports after preparation. The TextQuoteSelector check is named `QuoteSelectorShape` in the file. [SHACL] [SAGE]

<a id="normalization"></a>
## 5. Using the Turtle files

`sage.ttl` supplies the vocabulary, aliases, and selected structural mappings. The mappings allow a compact representation: superclass types can be derived from the supplied schema. [RDFS] [OWL] [SAGE]

| Mapping | Source and exact treatment |
|---|---|
| SAGE aliases ↔ original terms | Exact OWL equivalences listed in §3.1. |
| `rdo:report` → `rdo:report_part` | [RDO]. Reused subclass axiom. |
| `swande:Hypothesis`, `swande:Claim` → `swande:ResearchStatement`; `ResearchStatement`, `Question` → `DiscourseElement` | [SWAN-DE]. Four reused subclass axioms. |
| `prov:Person`, `prov:Organization`, `prov:SoftwareAgent` → `prov:Agent`; `prov:Plan` → `prov:Entity` | [PROV]. Four reused subclass axioms. |
| `rdo:proposition`, `rdo:assertion`, `rdo:argument`, `rdo:text`, `rdo:report_part` → `prov:Entity` | [RDO] [PROV] [SAGE]. Five SAGE-added subclass mappings classifying information-bearing resources as provenance entities. |
| `swande:ResearchStatement`, `swande:Question` → `prov:Entity` | [SWAN-DE] [PROV] [SAGE]. Two SAGE-added subclass mappings classifying discourse records as provenance entities. |
| `swande:Claim`, `swande:Hypothesis` → `rdo:assertion` | [SAGE]. Two added subclass alignments defined in §3.2. |
| Positive, negative, and neutral responses → `respondsTo` → `refersTo` → `relatesTo` | [SWAN-DR]. Reused subproperty hierarchy. |
| Evidence references, `motivates`, and `arisesFrom` → `refersTo` | [SWAN-DR]. Reused subproperty axioms. |
| `consistentWith`, `inconsistentWith`, `relevantTo`, `alternativeTo` → `relatesTo`; each is symmetric | [SWAN-DR]. Reused subproperty and symmetry axioms. |

For the supplied SHACL file, prepare a metadata view in two steps. First, replace the §3.1 alias classes in `rdf:type` positions and alias properties in predicate positions with their original identifiers. Then apply the supplied `rdfs:subClassOf`, `rdfs:subPropertyOf`, and `owl:SymmetricProperty` axioms repeatedly: add the superclass type for each instance, the superproperty triple for each property use, and the reversed triple for each symmetric property use. Stop when no further triples are added. These two operations define the preparation step. [SHACL] [RDFS] [OWL] [SAGE]

The metadata view contains the descriptions of assertions, arguments, and related resources. Preparation uses only the schema supplied in `sage.ttl`; stored identifiers and source graphs can remain unchanged. Ontology declarations, shapes, and named graphs encoding proposition content are kept separate from this view. The supplied vocabulary is a selected set of axioms. The preparation step does not infer types from domains or ranges, perform property chains, or apply other upstream axioms. The SWAN discourse hierarchy and four symmetric relationships listed above are included. A positive, negative, or neutral response therefore appears in queries for `respondsTo`, `refersTo`, and `relatesTo`. Responses remain directed; no transitivity or inverse pairing of `motivates` and `arisesFrom` is introduced. Full upstream ontologies can be loaded for additional reasoning in a separate view. [RDO] [SEE] [RDF] [OWL] [SHACL] [SAGE]

## 6. Examples

### 6.1 An argument and its responses

In this fictional example, an analysis reports marker signals of 8 and 4 units. A researcher provisionally puts forward the hypothesis that the assay uses a common ratio scale. The observation and hypothesis serve as premises for the Claim that A has twice B's signal. The Claim's proposition explicitly retains that condition. Other contributions agree, disagree, or respond neutrally to the hypothesis.

```turtle
@prefix sage: <https://example.org/sage/ontology#> .
@prefix ex: <https://example.org/project/> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix pav: <http://purl.org/pav/> .
@prefix dcterms: <http://purl.org/dc/terms/> .
@prefix swande: <http://purl.org/swan/2.0/discourse-elements/> .
@prefix swandr: <http://purl.org/swan/2.0/discourse-relationships/> .

ex:researcher a sage:Agent, prov:Person .
ex:curator a sage:Agent, prov:Person .
ex:peer a sage:Agent, prov:Person .
ex:assistant a sage:Agent, prov:SoftwareAgent .
ex:data a prov:Entity .
ex:run a prov:Activity ; prov:used ex:data ;
    prov:wasAssociatedWith ex:assistant .
ex:report a sage:Report ; prov:wasGeneratedBy ex:run .

ex:p1 a sage:Proposition ; sage:expressedAs [ a sage:Text ;
    sage:text "In assay M, mean marker signal is 8 units for sample A and 4 units for sample B." ] .
ex:p2 a sage:Proposition ; sage:expressedAs [ a sage:Text ;
    sage:text "The signals for samples A and B in assay M use a common ratio scale with a meaningful zero." ] .
ex:p3 a sage:Proposition ; sage:expressedAs [ a sage:Text ;
    sage:text "Given the common ratio scale with a meaningful zero, sample A has twice the mean marker signal of sample B in assay M." ] .

ex:observation a sage:Assertion ; sage:asserts ex:p1 ;
    sage:assertedBy ex:assistant ; sage:reportedIn ex:report .
ex:hypothesis a swande:Hypothesis ;
    pav:authoredBy ex:researcher ; sage:asserts ex:p2 ;
    sage:assertedBy ex:researcher ; sage:reportedIn ex:report .
ex:conclusion a swande:Claim ;
    pav:authoredBy ex:researcher ; pav:curatedBy ex:curator ;
    sage:asserts ex:p3 ; sage:assertedBy ex:researcher ;
    pav:createdBy ex:assistant ; swandr:respondsTo ex:question .
ex:argument a sage:Argument ; pav:authoredBy ex:researcher ;
    sage:hasPremise ex:observation, ex:hypothesis ;
    sage:hasConclusion ex:conclusion .

ex:question a sage:Question ; pav:authoredBy ex:researcher ;
    dcterms:description "What is the ratio of mean marker signal in sample A to sample B in assay M?" ;
    swandr:refersTo ex:report .
ex:agreement a swande:ResearchStatement ; pav:authoredBy ex:curator ;
    dcterms:description "I agree with the proposed ratio-scale interpretation." ;
    swandr:respondsPositivelyTo ex:hypothesis .
ex:disagreement a swande:ResearchStatement ; pav:authoredBy ex:peer ;
    dcterms:description "I disagree with the proposed ratio-scale interpretation because the baseline may be offset." ;
    swandr:respondsNegativelyTo ex:hypothesis ;
    swandr:inconsistentWith ex:agreement .
ex:neutralResponse a swande:ResearchStatement ; pav:authoredBy ex:peer ;
    dcterms:description "The calibration protocol relevant to this interpretation is in the report." ;
    swandr:respondsNeutrallyTo ex:hypothesis ; swandr:refersTo ex:report .

# A later publication puts forward the same content on a new occasion.
ex:laterReport a sage:Report ; dcterms:title "Subsequent calibration study" .
ex:laterClaim a swande:Claim ; pav:authoredBy ex:peer ;
    sage:asserts ex:p2 ; sage:assertedBy ex:peer ;
    sage:reportedIn ex:laterReport ; swandr:respondsPositivelyTo ex:hypothesis .
```

`ex:hypothesis` and `ex:conclusion` acquire SEE Assertion typing from their SWAN types. Their proposition and agent fields describe those same individuals. The observation is an ordinary Assertion, and neither premise is a Claim. The conclusion's curator and digital recorder are distinct from its asserting agent. The three response contributions use direct SWAN relationships without requiring another record around the relationship. The neutral contribution says nothing about whether its author withholds judgment. [SAGE]

### 6.2 Identity across publications and corrections

In §6.1, `ex:hypothesis` puts forward `ex:p2` provisionally in `ex:report`. A later publication, `ex:laterReport`, puts the same proposition forward as established through `ex:laterClaim`. The two assertions have their own identifiers, asserting agents, and source reports; they share `ex:p2` because the scientific content is unchanged. The earlier Hypothesis remains a record of the earlier contribution. A new assertion does not require a new Proposition when its content is the same. [SAGE]

A citation or quotation of the original assertion alone does not create a new asserting occasion. Record the reference to the original contribution or report with existing source and discourse relationships. Create a new assertion when the later contribution itself puts the proposition forward. [SAGE]

Correcting a transcription, attribution, or classification error in the graph retains the identifier of the contribution being described. For example, if the original paper actually presented `ex:hypothesis` as established and the graph mistakenly classified it as provisional, correct its type to Claim on that same individual. This corrects the record of the original occasion; it does not represent a later change in scientific judgment. By contrast, a later reassertion supported by new evidence is a new contribution, as with `ex:laterClaim`. [SAGE]

If a later contribution instead limits the ratio-scale statement to a different assay, population, or set of conditions, it expresses a different proposition and receives a new Proposition identifier. A translation or paraphrase preserving those conditions can share the original proposition. Identity follows the contribution and scientific meaning, rather than wording alone. These conventions require no additional status classes or relationships. [SAGE]

### 6.3 A published scientific argument

[The Meselson–Stahl example](examples/README.md) applies the same model to a published experiment, including observations, an argument, and competing interpretations. Its [Turtle graph](examples/meselson-stahl.ttl) is included in verification. Claims use proposition text; generic model descriptions retain their own formulations. The example also exercises broader response queries and symmetric alternatives.

## Sources

**[SAGE]** identifies definitions and design choices introduced by this specification. Citations in grouped rows apply individually to each named term.

**[SWAN]** Ciccarese et al., *Semantic Web Applications in Neuromedicine (SWAN) Ontology*, W3C Interest Group Note, 2009. Describes SWAN 1.2 and its discourse and modularity principles.

**[SWAN-DE] / [SWAN-DR]** SWAN `discourse-elements.owl` and `discourse-relationships.owl`, repository tag `2.0.1`. These files use `/swan/2.0/` identifiers and declare `owl:versionInfo` as `3.0`. Their inspected Git blobs are `aa4488d9d9452e60adebaee9b3fd8ed9890f6668` and `c6e05a9406a42ba25690ec2a42c280600ae8d2b7`, respectively.

**[SEE]** Bölling, Weidlich, and Holzhütter, *SEE: structured representation of scientific evidence in the biomedical domain using Semantic Web techniques*, Journal of Biomedical Semantics, 2014. **[RDO]** identifies its machine vocabulary, `ontology/rdo.owl`, inspected Git blob `7fd43b79af2875c6dbe3e00f7be175cc583bfd98`.

**[PROV]**, **[OA]**, **[SKOS]**, **[RDF]**, **[RDFS]**, **[OWL]**, **[SHACL]**, and **[TURTLE]** identify the linked W3C recommendations. **[PAV]** identifies the modern PAV vocabulary, **[DC]** the DCMI terms, and **[XSD]** the datatype recommendation.

[SAGE]: #authority-and-scope
[RDO]: https://github.com/semantic-evidence/semantic-evidence/blob/master/ontology/rdo.owl
[SEE]: https://doi.org/10.1186/2041-1480-5-S1-S1
[SWAN]: https://www.w3.org/TR/2009/NOTE-hcls-swan-20091020/
[SWAN-DE]: https://github.com/pav-ontology/swan-ontology/blob/2.0.1/discourse-elements.owl
[SWAN-DR]: https://github.com/pav-ontology/swan-ontology/blob/2.0.1/discourse-relationships.owl
[PROV]: https://www.w3.org/TR/2013/REC-prov-o-20130430/
[PAV]: https://pav-ontology.github.io/pav/
[OA]: https://www.w3.org/TR/2017/REC-annotation-model-20170223/
[SKOS]: https://www.w3.org/TR/2009/REC-skos-reference-20090818/
[RDF]: https://www.w3.org/TR/2014/REC-rdf11-mt-20140225/
[RDFS]: https://www.w3.org/TR/2014/REC-rdf-schema-20140225/
[OWL]: https://www.w3.org/TR/2012/REC-owl2-syntax-20121211/
[SHACL]: https://www.w3.org/TR/2017/REC-shacl-20170720/
[DC]: https://www.dublincore.org/specifications/dublin-core/dcmi-terms/
[TURTLE]: https://www.w3.org/TR/2014/REC-turtle-20140225/
[XSD]: https://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/
