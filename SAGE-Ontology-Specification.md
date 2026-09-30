# SAGE Ontology Specification

**Scientific Argument Graph & Epistemology**

SAGE represents scientific claims, evidence, arguments, and positions held by researchers and software agents. It combines SWAN's scientific discourse vocabulary with SEE's Reasoning and Discourse Ontology (RDO). Together, these describe what researchers claim, how they justify those claims, and how they interpret one another's work. [SWAN] [SEE]

<a id="authority-and-scope"></a>
## 1. Authority and scope

This document is the ultimate normative authority for SAGE. Sections 1–5 define the vocabulary, mappings, and structural requirements implemented by `sage.ttl` and `sage.shacl.ttl`. Conflicts are resolved by bringing those files into agreement with this specification. External terms retain their source meanings. Section 6 is illustrative. [SAGE]

Three SAGE-specific concepts extend the reused vocabularies: `Stance`, `DiscourseAssessment`, and `Decision`. Other scientific concepts are reused directly or through exact aliases. Domain ontologies describe genes, assays, samples, models, and other research entities. Applications determine how the graph is stored, edited, and used. [SWAN] [SAGE]

The provisional namespaces are `https://example.org/sage/ontology#` for `sage:` and `https://example.org/sage/spec#` for `spec:`. The source vocabularies use `http://purl.org/see/rdo#` (`rdo:`), `http://purl.org/swan/2.0/discourse-elements/` (`swande:`), and `http://purl.org/swan/2.0/discourse-relationships/` (`swandr:`). Other prefixes follow their cited standards. [SAGE] [RDO] [SWAN-DE] [SWAN-DR]

## 2. Scientific model

### Claims and positions

A **proposition** is the content of a statement that can be true or false. A **text** is an expression in a natural or formal language. An **assertion** is an agent's claim, made on a particular occasion, that a proposition is true. Several assertions can share a proposition while having different authors, sources, or justifications. RDO represents the act of asserting with `act_of_assertion`. [RDO] [SEE]

A **stance** records an agent's position toward a proposition: acceptance, tentative acceptance, rejection as an adequate conclusion, or suspension of judgment. Rejection concerns the proposed conclusion's adequacy; a claim that it is false uses a proposition expressing that negation. `heldBy` identifies whose position is represented. An interpretation of someone else's position can itself be recorded as an assertion attributed to the interpreter. [RDO] [SEE] [SAGE]

Attribution identifies each contribution: making a claim, holding a stance, authoring content, curating it, or creating its digital representation. One agent can fill several roles, recorded through their respective properties. [RDO] [PAV]

### Arguments and evidence

An **argument** connects premise assertions to a conclusion assertion. Its premises are offered jointly as grounds for the conclusion. Alternative justifications are represented by separate arguments. `sage:assumes` identifies a premise taken as given within that argument. Conditional claims retain their conditions in the proposition. Evaluations of an argument are recorded as further assertions or discourse assessments. [RDO] [SEE] [SAGE]

Observations and computed results contribute evidence when assertions describing them serve as premises. `reportedIn` identifies a report in which an assertion was made. `prov:wasGeneratedBy` links a report to the activity that produced it; `prov:used` links that activity to its data, code, or other inputs. Together, these relations trace an argument to its scientific and computational origins. [RDO] [SEE] [PROV]

### Interpretation, questions, and choices

A **discourse assessment** records an author's judgment that a SWAN relationship holds between two resources. Its `rdf:subject`, `rdf:predicate`, and `rdf:object` identify the assessed relationship, such as a response or an alternative interpretation. This reification gives the assessment its own identity and attribution. A direct SWAN triple states the relationship in its containing graph; both forms are supported. [SWAN-DR] [RDF] [SAGE]

A **question** expresses an inquiry. Existing SWAN relationships connect it to relevant reports, statements, and arguments. A **decision** records who chose an option and what they chose. Explanatory notes use `rdfs:comment`; structured reasons use `hasRationale`. Both forms of explanation are optional. [SWAN-DE] [SWAN-DR] [RDO] [RDFS] [SAGE]

### Identity and context

Proposition identity follows scientific meaning, including qualifications such as organism, experimental system, comparison, and measurement. Equivalent formulations express the same proposition. Each assertion identifies a particular claim-making occasion, even when several assertions express the same proposition. [RDO] [SEE]

`inContext` identifies where a recorded position or other resource applies, such as an inquiry or analytical setting. Each value names a context of applicability; a combination of conditions can be represented as one context. The proposition retains its stated meaning across contexts, and distinct positions have separate stance records. [SAGE]

Omitted fields leave information unspecified. Sources, contexts, and reasons can be added as they become available. [SAGE]

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
### 3.2 SAGE additions

The table defines each SAGE term and its formal relationships. Arrows give a property's domain and range; “Resource” indicates an unrestricted subject type. Section 4 defines the structural profile's field cardinalities. [SAGE]

| Term | Definition and formal relationships | Source and exact adaptation |
|---|---|---|
| `sage:Stance` | An agent's position toward a proposition. Subclass of `oa:Annotation` and `prov:Entity`; disjoint with `rdo:assertion`. | [SEE] [OA] [PROV]. Specializes an annotation with a proposition target, stance-value body, and holder. Context, provenance, and reasons are optional. |
| `sage:DiscourseAssessment` | An attributed assessment of a SWAN relationship. Subclass of `rdf:Statement` and `prov:Entity`. | [SWAN-DR] [RDF] [PAV] [PROV]. Specializes RDF reification with author attribution and optional source, context, and reasons. It retains RDF's separation between describing and asserting a triple. |
| `sage:Decision` | A record of a choice. Subclass of `swande:DiscourseElement` and `prov:Entity`. | [SWAN-DE] [PROV]. Adds a discourse record with decision makers and selected options. Reasons are optional. |
| `sage:heldBy` | Stance → `rdo:agent` whose position is recorded. | [RDO] [PAV]. Adds a property identifying the stance-holder role. |
| `sage:inContext` | Resource → `prov:Entity` identifying a context of applicability. | [SWAN] [PROV]. Adds an optional scope relationship with an unrestricted domain and any number of contexts. |
| `sage:assumes` | `rdo:argument` → `rdo:assertion`; subproperty of `rdo:has_premise`. | [RDO] [SEE]. Specializes the premise relation to identify assumption use within an argument. |
| `sage:hasRationale` | Resource → `rdo:argument` offered as a reason. | [RDO]. Adds an optional link to RDO's argument concept with an unrestricted domain. Informal explanations use `rdfs:comment`. |
| `sage:decidedBy` | Decision → `rdo:agent` who made the choice. | [PAV]. Adds a property identifying the decision-maker role. |
| `sage:selects` | Decision → selected `prov:Entity`, often a `prov:Plan`. | [PROV]. Adds a choice relationship supporting one or more selected options. |

`sage:StanceScheme` is a SAGE-defined `skos:ConceptScheme` containing the following four categorical positions. Each value is a `skos:Concept`, linked to the scheme with `skos:inScheme` and labeled with `skos:prefLabel`. [SKOS] [SAGE]

| Value | Meaning | Source and adaptation |
|---|---|---|
| `sage:Accepts` | Treats the proposition as warranted within its applicable scope. | [SAGE] [SKOS]. New controlled value. |
| `sage:TentativelyAccepts` | Accepts the proposition provisionally. | [SAGE] [SKOS]. New controlled value. |
| `sage:Rejects` | Judges the proposition inadequate as a conclusion within its applicable scope. | [SAGE] [SKOS]. New controlled value. |
| `sage:WithholdsJudgment` | Explicitly suspends judgment about the proposition. | [SAGE] [SKOS]. New controlled value. |

### 3.3 Reused vocabulary

The following terms retain their source definitions. Each citation applies to every term in its row. Properties describe their subjects in the roles specified by the source vocabulary. [SAGE]

| Existing terms | Meaning and use | Source |
|---|---|---|
| `rdo:report_part` | A portion of a report's narrative, including a figure, table, or discontinuous selection. RDO also classifies whole reports as report parts. | [RDO] |
| `rdo:act_of_assertion`, `rdo:act_of_inference` | The activities of asserting and drawing an inference. | [RDO] |
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
| `oa:Annotation`, `oa:hasTarget`, `oa:hasBody` | An annotation, the resource it concerns, and its content. | [OA] |
| `oa:SpecificResource`, `oa:hasSource`, `oa:hasSelector` | A constrained resource, its source, and an optional selection mechanism. A report selection can also have `rdo:report_part` typing. | [OA] |
| `oa:TextQuoteSelector`, `oa:exact`, `oa:prefix`, `oa:suffix` | A text selection identified by an exact normalized quote and optional surrounding text. | [OA] |
| `oa:FragmentSelector`, `rdf:value`, `dcterms:conformsTo` | A fragment selection, its fragment string, and the identifier of its syntax. | [OA] [RDFS] [DC] |
| `dcterms:source`, `dcterms:title`, `dcterms:description` | A resource from which content is derived, a name, and a descriptive account. Sources can be any referenced resource. | [DC] |
| `dcterms:identifier`, `dcterms:issued` | A resource identifier and issuance date. | [DC] |
| `rdfs:label`, `rdfs:comment` | A readable label and comment. SAGE also uses comments for informal explanations. | [RDFS] |
| `rdf:Statement`, `rdf:subject`, `rdf:predicate`, `rdf:object` | A reified triple and its three components. | [RDF] [RDFS] |
| `skos:Concept`, `skos:ConceptScheme`, `skos:inScheme`, `skos:prefLabel`, `skos:changeNote` | Controlled values, schemes, scheme membership, preferred labels, and adaptation notes. | [SKOS] |

The files use RDF and Turtle for representation, RDFS and OWL for vocabulary declarations, SHACL for constraints, and XSD for datatypes. Component annotations identify sources with `dcterms:source`, normative definitions with `rdfs:isDefinedBy`, and adaptations with `skos:changeNote`. Each of these standards is used with its original semantics. [RDF] [RDFS] [TURTLE] [OWL] [SHACL] [XSD] [DC] [SKOS]

<a id="constraints"></a>
## 4. Structural requirements

The tables define the supplied SHACL profile. A conforming description supplies the required fields and satisfies the stated value constraints. Partial descriptions can be retained and expanded; validation reports identify unmet requirements. Attribution fields name agents in their stated roles. [RDO] [PAV] [SHACL] [SAGE]

All cardinalities and validation targets are SAGE profile choices. `1` means exactly one value, `1+` one or more, and `0+` any number, including zero. Referenced resources can be IRIs or blank nodes, except where a field specifically requires an IRI. A readable value is an `xsd:string` or `rdf:langString` containing at least one non-whitespace character. The profile permits additional properties. [RDF] [XSD] [SHACL] [SAGE]

In the tables, **Record** selects the shared `RecordShape` constraints: `prov:Entity` typing, the optional metadata fields, and explanations. Entity and Activity are disjoint in the profile; Stance and Assertion are disjoint in the ontology and profile. Class names use SAGE aliases where available; Entity, Activity, Plan, Role, and Association denote PROV classes. [PROV] [SAGE]

### Scientific records

| Type | Required structure and permitted fields | Source vocabulary |
|---|---|---|
| Agent | RDO agent type; `rdfs:label` (0+ readable values). | [RDO] [RDFS] |
| Proposition | Record; proposition type; `expressedAs` (1+ Texts). | [RDO] [SEE] |
| Text | Record; text type; `text` (1 readable literal). | [RDO] |
| Report or report part | Record; RDO report-part type after §5 preparation; `dcterms:title` (0+ readable values). | [RDO] [DC] |
| Assertion | Record; assertion type; `asserts` (1 Proposition), `assertedBy` (1+ Agents), `reportedIn` (0+ report parts). | [RDO] [SEE] |
| Argument | Record; argument type; `hasPremise` (1+ Assertions), `hasConclusion` (1 Assertion), `pav:authoredBy` (1+ Agents). `assumes` specializes the premise relation. | [RDO] [SEE] [PAV] |
| SWAN ResearchStatement, Hypothesis, Claim | Record; appropriate SWAN type; `dcterms:description` (1+ readable formulations), `pav:authoredBy` (1+ Agents). | [SWAN-DE] [PAV] |
| Question | Record; question type; `dcterms:description` (1+ readable formulations), `pav:authoredBy` (1+ Agents). | [SWAN-DE] [PAV] |
| Stance | Record; Stance type; `heldBy` (1 Agent), `oa:hasTarget` (1 Proposition), `oa:hasBody` (1 of the four stance values). | [OA] [SEE] [SAGE] |
| DiscourseAssessment | Record; DiscourseAssessment type; `rdf:subject` (1 resource), `rdf:predicate` (1 SWAN relationship IRI from §3.3), `rdf:object` (1 resource), `pav:authoredBy` (1+ Agents). | [SWAN-DR] [RDF] [PAV] |
| Decision | Record; Decision type; `dcterms:description` (1+ readable formulations), `decidedBy` (1+ Agents), `selects` (1+ Entities). | [PROV] [PAV] [SAGE] |
| Shared optional fields | On each Record: `pav:createdBy` and `pav:curatedBy` (0+ RDO Agents), `pav:createdWith` (0+ tool resources), `pav:createdOn` (0–1 `xsd:dateTime`), `dcterms:source` (0+ resources), `inContext` (0+ Entities). | [PAV] [DC] [PROV] [XSD] [SAGE] |
| Explanation | `rdfs:comment` (0+ readable notes), `hasRationale` (0+ Arguments). Applies to Records and any resource using `hasRationale`. Comments can contain reasons or other explanatory material. | [RDFS] [RDO] [SAGE] |

`reportedIn` identifies where a claim was made; `dcterms:source` identifies a resource from which the description was drawn. Both are optional. Several formulations of one record express the same identified content. [RDO] [DC] [SAGE]

### Provenance and source selections

These checks apply when the corresponding structures are present. Date and time fields use `xsd:dateTime`, which supports values with or without a timezone. Auxiliary resources can use either IRIs or blank nodes. [PROV] [OA] [RDF] [XSD] [SAGE]

| Type | Structural requirements | Source vocabulary |
|---|---|---|
| Entity | Entity type. Optional `wasGeneratedBy` → Activity, `wasDerivedFrom` → Entity, `wasAttributedTo` → PROV Agent. | [PROV] |
| Activity | Activity type. Optional `used` → Entity, `wasAssociatedWith` → PROV Agent, `qualifiedAssociation` → Association. Start and end times are optional, at most one each. Plans are attached through an Association. | [PROV] |
| PROV Agent | PROV Agent type, including its normalized Person, Organization, and SoftwareAgent subclasses. | [PROV] |
| Association | Association type; `prov:agent` (1 PROV Agent), `prov:hadPlan` (0–1 Plan), `prov:hadRole` (0+ Roles). | [PROV] |
| Context | Entity type when linked by `inContext`. | [PROV] [SAGE] |
| SpecificResource | SpecificResource type; `oa:hasSource` (1 resource), `oa:hasSelector` (0+ selector resources). | [OA] |
| TextQuoteSelector | Selector type; `oa:exact` (1 non-whitespace `xsd:string`), `oa:prefix` and `oa:suffix` (0–1 string each). | [OA] |
| FragmentSelector | Selector type; `rdf:value` (1 non-whitespace string), `dcterms:conformsTo` (0–1 syntax IRI). | [OA] [RDFS] [DC] |

### Validation targets

The file uses SHACL Core, with violation severity for all checks. Shapes apply to their target classes and the property uses below after §5 preparation. Record and ReadableText constraints apply through their parent shapes. Source and adaptation annotations accompany the named shapes and field constraints. [SHACL] [SAGE]

| Shape | Additional targets, using canonical predicates |
|---|---|
| Agent | Objects of `rdo:is_assertion_made_by`, `sage:heldBy`, and `sage:decidedBy`. PAV field types are checked within the records that use them. |
| Proposition | Subjects of `rdo:is_proposition_expressed_in` and objects of `rdo:is_assertion_asserting`. |
| Text | Subjects of `rdo:has_lexical_structure` and objects of `rdo:is_proposition_expressed_in`. |
| Assertion | Subjects of `rdo:is_assertion_asserting`, `rdo:is_assertion_made_by`, and `rdo:is_assertion_made_in`; objects of `rdo:has_premise` and `rdo:has_conclusion`. |
| Argument | Subjects of `rdo:has_premise`, `rdo:has_conclusion`, and `sage:assumes`; objects of `sage:hasRationale`. |
| Report | Objects of `rdo:is_assertion_made_in`. |
| Stance | Subjects of `sage:heldBy`. |
| Decision | Subjects of `sage:decidedBy` and `sage:selects`. |
| Explanation | Subjects of `sage:hasRationale`. It also targets Stance, DiscourseAssessment, Decision, and Question classes. |
| Context | Objects of `sage:inContext`. |
| Entity | Subjects of `prov:wasGeneratedBy`, `prov:wasDerivedFrom`, and `prov:wasAttributedTo`; objects of `prov:used`, `prov:wasDerivedFrom`, and `sage:selects`. |
| Activity | Subjects of `prov:used`, `prov:wasAssociatedWith`, `prov:qualifiedAssociation`, `prov:startedAtTime`, and `prov:endedAtTime`; objects of `prov:wasGeneratedBy`. |
| PROV Agent | Objects of `prov:wasAssociatedWith`, `prov:wasAttributedTo`, and `prov:agent`. |
| Association | Subjects of `prov:hadPlan` and objects of `prov:qualifiedAssociation`. |
| SpecificResource | Subjects of `oa:hasSource` and `oa:hasSelector`. |
| TextQuoteSelector | Subjects of `oa:exact`. |

Question, ResearchStatement, DiscourseAssessment, and FragmentSelector use class targets only. Report targets `rdo:report_part`, including whole reports after preparation. The TextQuoteSelector check is named `QuoteSelectorShape` in the file. [SHACL] [SAGE]

<a id="normalization"></a>
## 5. Using the Turtle files

`sage.ttl` supplies the vocabulary, aliases, and selected structural mappings. The mappings allow a compact representation: superclass types and assumption-to-premise links can be derived from the supplied schema. [RDFS] [OWL] [SAGE]

| Mapping | Source and exact treatment |
|---|---|
| SAGE aliases ↔ original terms | Exact OWL equivalences listed in §3.1. |
| `rdo:report` → `rdo:report_part` | [RDO]. Reused subclass axiom. |
| `swande:Hypothesis`, `swande:Claim` → `swande:ResearchStatement`; `ResearchStatement`, `Question` → `DiscourseElement` | [SWAN-DE]. Four reused subclass axioms. |
| `prov:Person`, `prov:Organization`, `prov:SoftwareAgent` → `prov:Agent`; `prov:Plan` → `prov:Entity` | [PROV]. Four reused subclass axioms. |
| `rdo:proposition`, `rdo:assertion`, `rdo:argument`, `rdo:text`, `rdo:report_part` → `prov:Entity` | [RDO] [PROV] [SAGE]. Five SAGE-added subclass mappings classifying information-bearing resources as provenance entities. |
| `swande:ResearchStatement`, `swande:Question` → `prov:Entity` | [SWAN-DE] [PROV] [SAGE]. Two SAGE-added subclass mappings classifying discourse records as provenance entities. |
| SAGE subclasses and `assumes` → `rdo:has_premise` | The class and premise specializations defined in §3.2. |

For the supplied SHACL file, prepare a metadata view in two steps. First, replace the §3.1 alias classes in `rdf:type` positions and alias properties in predicate positions with their original identifiers. Then apply the supplied `rdfs:subClassOf` and `rdfs:subPropertyOf` axioms repeatedly: add the superclass type for each instance and the superproperty triple for each property use. Stop when no further triples are added. These two operations define the preparation step. [SHACL] [RDFS] [OWL] [SAGE]

The metadata view contains the descriptions of assertions, arguments, and related resources. Preparation uses only the schema supplied in `sage.ttl`; stored identifiers and source graphs can remain unchanged. Ontology declarations, shapes, and named graphs encoding proposition content are kept separate from this view. The supplied vocabulary is a selected set of axioms. Full upstream ontologies can be loaded for additional reasoning in a separate view. [RDO] [SEE] [RDF] [OWL] [SHACL] [SAGE]

## 6. Example

In this fictional example, an analysis reports mean marker signals of 8 and 4 units for samples A and B. Assuming a common ratio scale with a meaningful zero, the researcher concludes that A's signal is twice B's. The graph records this reasoning, a stance toward the conclusion, an assessment relating the argument to a question, and a follow-up choice.

```turtle
@prefix sage: <https://example.org/sage/ontology#> .
@prefix ex: <https://example.org/project/> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix pav: <http://purl.org/pav/> .
@prefix oa: <http://www.w3.org/ns/oa#> .
@prefix dcterms: <http://purl.org/dc/terms/> .
@prefix swandr: <http://purl.org/swan/2.0/discourse-relationships/> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

ex:researcher a sage:Agent, prov:Person .
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
    sage:text "In assay M, sample A has twice the mean marker signal of sample B." ] .

ex:observation a sage:Assertion ; sage:asserts ex:p1 ;
    sage:assertedBy ex:assistant ; sage:reportedIn ex:report .
ex:assumption a sage:Assertion ; sage:asserts ex:p2 ;
    sage:assertedBy ex:researcher .
ex:conclusion a sage:Assertion ; sage:asserts ex:p3 ;
    sage:assertedBy ex:researcher ; pav:createdBy ex:assistant .
ex:argument a sage:Argument ; pav:authoredBy ex:researcher ;
    sage:hasPremise ex:observation ; sage:assumes ex:assumption ;
    sage:hasConclusion ex:conclusion .
ex:position a sage:Stance ; sage:heldBy ex:researcher ;
    oa:hasTarget ex:p3 ; oa:hasBody sage:Accepts ;
    sage:hasRationale ex:argument .

ex:question a sage:Question ; pav:authoredBy ex:researcher ;
    dcterms:description "What is the ratio of mean marker signal in sample A to sample B in assay M?" ;
    swandr:refersTo ex:report .
ex:assessment a sage:DiscourseAssessment ;
    rdf:subject ex:argument ; rdf:predicate swandr:respondsTo ;
    rdf:object ex:question ; pav:authoredBy ex:researcher .
ex:plan a prov:Plan ;
    dcterms:description "Repeat assay M with independent biological replicates." .
ex:choice a sage:Decision ; sage:decidedBy ex:researcher ;
    sage:selects ex:plan ; dcterms:description "Repeat the assay to assess biological variability." .
```

The researcher asserts the conclusion; the assistant records it. The assessment records the researcher's judgment that the argument responds to the question. The question also refers directly to the report. The decision records the chosen follow-up plan. [RDO] [PAV] [SWAN-DR] [SAGE]

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
