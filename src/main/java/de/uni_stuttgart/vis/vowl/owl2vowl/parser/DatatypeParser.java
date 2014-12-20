/*
 * DatatypeParser.java
 *
 */

package de.uni_stuttgart.vis.vowl.owl2vowl.parser;

import de.uni_stuttgart.vis.vowl.owl2vowl.Main;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.Vowl_Prop_Attr;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.nodes.datatypes.BaseDatatype;
import de.uni_stuttgart.vis.vowl.owl2vowl.parser.container.MapData;
import org.semanticweb.owlapi.model.*;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public class DatatypeParser extends GeneralNodeParser {
	private Set<OWLDatatype> datatypes;

	public DatatypeParser(Set<OWLDatatype> datatypes, OWLOntology ontology, OWLDataFactory factory, MapData mapData, OWLOntologyManager ontologyManager) {
		super(ontology, factory, mapData, ontologyManager);
		this.datatypes = datatypes;
	}

	public void execute() {
		Map<String, BaseDatatype> datatypeMap = mapData.getDatatypeMap();
		Map<String, OWLDatatype> owlDatatypes = mapData.getOwlDatatypes();

		for (OWLDatatype currentDatatype : datatypes) {
			isDeprecated = false;
			rdfsIsDefinedBy = "";
			owlVersionInfo = "";
			iri = currentDatatype.getIRI().toString();
			TypeFinder finder = new TypeFinder(ontology, factory);
			BaseDatatype theDatatype = finder.findVowlDatatype(currentDatatype);

			Main.logger.info("Datatype: " + currentDatatype);
			for (OWLAxiom currentAxiom : currentDatatype.getReferencingAxioms(ontology)) {
				Main.logger.info("\tAxiom: " + currentAxiom);

				for (OWLClassExpression nestExpr : currentAxiom.getNestedClassExpressions()) {
					Main.logger.info("\t\tNested: " + nestExpr);
				}
			}

			parseAnnotations(currentDatatype);

			theDatatype.setLabels(languageToLabel);
			theDatatype.setComments(comments);
			theDatatype.setName(languageToLabel.get("default"));
			theDatatype.setIri(iri);
			theDatatype.setDefinedBy(rdfsIsDefinedBy);
			theDatatype.setOwlVersion(owlVersionInfo);

			if (isDeprecated) {
				theDatatype.getAttributes().add(Vowl_Prop_Attr.PROP_ATTR_DEPR);
			}

			owlDatatypes.put(currentDatatype.getIRI().toString(), currentDatatype);
			datatypeMap.put(theDatatype.getIri(), theDatatype);
		}
	}
}
