/*
 * UnionParser.java
 *
 */

package de.uni_stuttgart.vis.vowl.owl2vowl.parser.helper;

import de.uni_stuttgart.vis.vowl.owl2vowl.constants.Axiom_Annotations;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.nodes.BaseNode;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.nodes.classes.OwlIntersectionOf;
import de.uni_stuttgart.vis.vowl.owl2vowl.parser.container.MapData;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class IntersectionParser extends AxiomParser {
	Map<String, OwlIntersectionOf> intersectionNodes = mapData.getIntersectionMap();

	public IntersectionParser(OWLOntology ontology, OWLDataFactory factory, MapData mapData, OWLOntologyManager ontologyManager) {
		super(ontology, factory, mapData, ontologyManager);
	}

	/**
	 * Searches for an union class of the property. If already exists take it an return else create
	 * a new node. If no union found return null.
	 *
	 * @param property  The property to search in.
	 * @param direction True for domain else range.
	 */
	public OwlIntersectionOf searchIntersection(OWLProperty property, boolean direction) {
		Set<OWLClass> classes = search(property, Axiom_Annotations.AXIOM_OBJ_INTERSECTION, direction);

		if (classes == null) {
			return null;
		}

		OwlIntersectionOf intersectionOf = getExistingIntersectionClass(classes);

		if (intersectionOf == null) {
			intersectionOf = createIntersectionClass(classes);
		}

		return intersectionOf;
	}

	private OwlIntersectionOf createIntersectionClass(Set<OWLClass> classesInSignature) {
		OwlIntersectionOf intersectionOf = new OwlIntersectionOf();

		for (OWLClass currentClass : classesInSignature) {
			BaseNode theNode = findNode(currentClass.getIRI().toString());

			if (theNode != null) {
				intersectionOf.addIntersection(theNode);
			}
		}

		intersectionNodes.put(intersectionOf.getId(), intersectionOf);
		return intersectionOf;
	}

	private OwlIntersectionOf getExistingIntersectionClass(Set<OWLClass> classes) {
		for (Map.Entry<String, OwlIntersectionOf> entry : intersectionNodes.entrySet()) {
			OwlIntersectionOf currentClass = entry.getValue();
			List<String> test = new ArrayList<String>();

			for (OWLClass curIri : classes) {
				test.add(curIri.getIRI().toString());
			}

			if (currentClass.equalIntersectionIris(test)) {
				return currentClass;
			}
		}

		return null;
	}
}
