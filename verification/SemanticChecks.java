/* Small entailment checks using HermiT/OWLAPI bundled in ROBOT.
 * Expectations: SAGE specification §§2–3. No inference algorithm is implemented here.
 * API: https://owlcs.github.io/owlapi/apidocs_4/org/semanticweb/owlapi/reasoner/OWLReasoner.html
 */
import java.io.File;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.HermiT.ReasonerFactory;

public class SemanticChecks {
    static final String S = "https://example.org/sage/ontology#";
    static final String R = "http://purl.org/see/rdo#";
    static final String W = "http://purl.org/swan/2.0/discourse-elements/";
    static final String E = "https://example.org/project/";
    static final OWLDataFactory F = OWLManager.getOWLDataFactory();

    static OWLClass c(String iri) { return F.getOWLClass(IRI.create(iri)); }
    static OWLObjectProperty p(String iri) { return F.getOWLObjectProperty(IRI.create(iri)); }
    static OWLNamedIndividual i(String iri) { return F.getOWLNamedIndividual(IRI.create(iri)); }
    static void require(boolean result, String message) {
        if (!result) throw new IllegalStateException(message);
    }
    static void entailed(OWLReasoner reasoner, OWLAxiom axiom, boolean expected, String label) {
        require(reasoner.isEntailmentCheckingSupported(axiom.getAxiomType()),
                "Reasoner does not support this entailment type: " + axiom.getAxiomType());
        require(reasoner.isEntailed(axiom) == expected, "Unexpected entailment: " + label + "\n" + axiom);
    }
    static void pass(String label) { System.out.println("  PASS " + label); }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected the scenario OWL-view path");
        OWLOntology ontology = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new File(args[0]));
        OWLProfileReport profile = new OWL2DLProfile().checkOntology(ontology);
        require(profile.isInProfile(), "Scenario is outside OWL 2 DL:\n" + profile);
        // Ensure the recorder relation is present as an object-property axiom,
        // rather than silently treated as an uninterpreted documentary annotation.
        OWLAxiom recorder = F.getOWLObjectPropertyAssertionAxiom(
                p("http://purl.org/pav/createdBy"), i(E + "conclusion"), i(E + "assistant"));
        require(ontology.containsAxiom(recorder), "The example's recorder assertion was not loaded");
        OWLReasoner reasoner = new ReasonerFactory().createReasoner(ontology);
        try {
            // Check consistency before querying entailments (inconsistency entails everything).
            require(reasoner.isConsistent(), "Specification example is inconsistent");
            require(reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().isEmpty(),
                    "Scenario has an unsatisfiable named class");
            pass("the example is consistent and all named classes are satisfiable");

            String[][] classes = {
                {"Proposition", R + "proposition"}, {"Assertion", R + "assertion"},
                {"Argument", R + "argument"}, {"Agent", R + "agent"},
                {"Text", R + "text"}, {"Report", R + "report"}, {"Question", W + "Question"}
            };
            for (String[] pair : classes)
                entailed(reasoner, F.getOWLEquivalentClassesAxiom(c(S + pair[0]), c(pair[1])), true, pair[0]);
            String[][] properties = {
                {"asserts", "is_assertion_asserting"}, {"assertedBy", "is_assertion_made_by"},
                {"reportedIn", "is_assertion_made_in"}, {"hasPremise", "has_premise"},
                {"hasConclusion", "has_conclusion"}, {"expressedAs", "is_proposition_expressed_in"}
            };
            for (String[] pair : properties)
                entailed(reasoner, F.getOWLEquivalentObjectPropertiesAxiom(p(S + pair[0]), p(R + pair[1])), true, pair[0]);
            entailed(reasoner, F.getOWLEquivalentDataPropertiesAxiom(
                    F.getOWLDataProperty(IRI.create(S + "text")),
                    F.getOWLDataProperty(IRI.create(R + "has_lexical_structure"))), true, "text");
            pass("all 14 aliases retain their source meanings");

            for (String kind : new String[]{"Claim", "Hypothesis"}) {
                entailed(reasoner, F.getOWLSubClassOfAxiom(c(W + kind), c(R + "assertion")), true,
                        kind + " is an Assertion");
                entailed(reasoner, F.getOWLSubClassOfAxiom(c(W + kind), c(W + "ResearchStatement")), true,
                        kind + " remains a ResearchStatement");
                require(reasoner.isSatisfiable(c(W + kind)), kind + " is unsatisfiable");
            }
            for (String node : new String[]{"conclusion", "hypothesis"}) {
                entailed(reasoner, F.getOWLClassAssertionAxiom(c(R + "assertion"), i(E + node)), true,
                        node + " is the same Assertion individual");
                entailed(reasoner, F.getOWLClassAssertionAxiom(c(S + "Assertion"), i(E + node)), true,
                        node + " also has alias typing");
                entailed(reasoner, F.getOWLClassAssertionAxiom(c(W + "ResearchStatement"), i(E + node)), true,
                        node + " retains SWAN typing");
            }
            pass("Claims and Hypotheses are Assertions and ResearchStatements on the same individuals");

            entailed(reasoner, F.getOWLSubClassOfAxiom(c(W + "ResearchStatement"), c(R + "assertion")), false,
                    "all ResearchStatements become Assertions");
            entailed(reasoner, F.getOWLClassAssertionAxiom(c(R + "assertion"), i(E + "neutralResponse")), false,
                    "neutral response becomes an Assertion");
            entailed(reasoner, F.getOWLSubClassOfAxiom(c(W + "Question"), c(W + "DiscourseElement")), true,
                    "Question is a DiscourseElement");
            for (String target : new String[]{W + "ResearchStatement", R + "assertion"}) {
                entailed(reasoner, F.getOWLSubClassOfAxiom(c(W + "Question"), c(target)), false,
                        "Question becomes " + target);
                entailed(reasoner, F.getOWLClassAssertionAxiom(c(target), i(E + "question")), false,
                        "example Question becomes " + target);
            }
            entailed(reasoner, F.getOWLEquivalentClassesAxiom(c(R + "assertion"), c(R + "act_of_assertion")), false,
                    "assertion is equated with its activity");
            pass("generic ResearchStatements and Questions keep their distinct classifications");

            for (String premise : new String[]{"observation", "hypothesis"}) {
                entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                        p(R + "has_premise"), i(E + "argument"), i(E + premise)), true, "premise alias");
                entailed(reasoner, F.getOWLClassAssertionAxiom(c(W + "Claim"), i(E + premise)), false,
                        "premise must be an established Claim");
            }
            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(R + "has_conclusion"), i(E + "argument"), i(E + "conclusion")), true, "conclusion alias");
            pass("ordinary Assertions and Hypotheses serve as premises without becoming Claims");

            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(R + "is_assertion_made_by"), i(E + "conclusion"), i(E + "researcher")), true,
                    "original asserting agent");
            for (String other : new String[]{"assistant", "curator"})
                entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                        p(R + "is_assertion_made_by"), i(E + "conclusion"), i(E + other)), false,
                        "recording or curation implies assertion");
            pass("asserting, curating, and recording retain separate attribution");

            String D = "http://purl.org/swan/2.0/discourse-relationships/";
            String[][] responses = {{"agreement", "respondsPositivelyTo"},
                                    {"disagreement", "respondsNegativelyTo"},
                                    {"neutralResponse", "respondsNeutrallyTo"}};
            for (String[] response : responses)
                entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                        p(D + response[1]), i(E + response[0]), i(E + "hypothesis")), true,
                        "direct " + response[1]);
            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(D + "respondsTo"), i(E + "conclusion"), i(E + "question")), true,
                    "Claim directly responds to Question");
            for (String name : new String[]{"respondsTo", "respondsPositivelyTo", "respondsNegativelyTo", "respondsNeutrallyTo"}) {
                entailed(reasoner, F.getOWLObjectPropertyDomainAxiom(p(D + name), c(W + "DiscourseElement")), false,
                        "SAGE restricts discourse subjects");
                entailed(reasoner, F.getOWLObjectPropertyRangeAxiom(p(D + name), c(W + "DiscourseElement")), false,
                        "SAGE restricts discourse objects");
            }
            pass("direct discourse relations coexist without added endpoint class restrictions");
            for (String[] response : responses) {
                for (String broader : new String[]{"respondsTo", "refersTo", "relatesTo"})
                    entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                            p(D + broader), i(E + response[0]), i(E + "hypothesis")), true,
                            "response inherited as " + broader);
                entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                        p(D + "respondsTo"), i(E + "hypothesis"), i(E + response[0])), false,
                        "response must remain directed");
            }
            for (String symmetric : new String[]{"consistentWith", "inconsistentWith", "relevantTo", "alternativeTo"})
                entailed(reasoner, F.getOWLSymmetricObjectPropertyAxiom(p(D + symmetric)), true,
                        "source symmetry for " + symmetric);
            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(D + "inconsistentWith"), i(E + "agreement"), i(E + "disagreement")), true,
                    "symmetric reverse relationship");
            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(D + "relatesTo"), i(E + "agreement"), i(E + "disagreement")), true,
                    "symmetry followed by superproperty expansion");
            pass("SWAN hierarchy and symmetry work while responses remain directed");

            for (String node : new String[]{"hypothesis", "laterClaim"})
                entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                        p(R + "is_assertion_asserting"), i(E + node), i(E + "p2")), true,
                        "separate contributions share propositional content");
            entailed(reasoner, F.getOWLSameIndividualAxiom(i(E + "hypothesis"), i(E + "laterClaim")), false,
                    "shared content must not merge assertions");
            entailed(reasoner, F.getOWLClassAssertionAxiom(c(W + "Claim"), i(E + "hypothesis")), false,
                    "later Claim must not reclassify the earlier Hypothesis");
            pass("later assertions share content without merging or reclassifying earlier contributions");
        } finally {
            reasoner.dispose();
        }
    }
}
