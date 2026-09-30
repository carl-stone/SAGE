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
            require(reasoner.isConsistent(), "Opposing-stance example is inconsistent");
            require(reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().isEmpty(),
                    "Scenario has an unsatisfiable named class");
            pass("two researchers can hold opposing stances");

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

            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(R + "has_premise"), i(E + "argument"), i(E + "assumption")), true, "assumption is a premise");
            pass("an assumption is a premise of its argument");

            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(S + "assertedBy"), i(E + "conclusion"), i(E + "researcher")), true, "original asserting agent");
            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p(S + "assertedBy"), i(E + "conclusion"), i(E + "assistant")), false, "recorder becomes asserting agent");
            // Ask about ANY stance held by the recorder, including unnamed individuals.
            OWLClassExpression holdsStance = F.getOWLObjectSomeValuesFrom(
                    F.getOWLObjectInverseOf(p(S + "heldBy")), c(S + "Stance"));
            entailed(reasoner, F.getOWLClassAssertionAxiom(holdsStance, i(E + "assistant")), false,
                    "recording implies a held stance");
            pass("recording preserves the author's role without implying a recorder stance");

            require(reasoner.isSatisfiable(c(S + "Assertion")), "Assertion is unsatisfiable");
            require(reasoner.isSatisfiable(c(S + "Stance")), "Stance is unsatisfiable");
            require(!reasoner.isSatisfiable(F.getOWLObjectIntersectionOf(c(S + "Assertion"), c(S + "Stance"))),
                    "Assertion and Stance overlap despite the declared disjointness");
            pass("Assertion and Stance are individually possible and mutually disjoint");

            entailed(reasoner, F.getOWLObjectPropertyAssertionAxiom(
                    p("http://purl.org/swan/2.0/discourse-relationships/respondsTo"),
                    i(E + "argument"), i(E + "question")), false, "assessment entails the direct relationship");
            pass("the reification encoding keeps the assessed relationship separate");
        } finally {
            reasoner.dispose();
        }
    }
}
