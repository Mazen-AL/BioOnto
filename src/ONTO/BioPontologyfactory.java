package ONTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;











import javax.swing.tree.DefaultMutableTreeNode;

import org.json.simple.parser.ParseException;

import util.bioportal;
import HRCHY.hierarchy;
import RICH.Enrichment;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class BioPontologyfactory {

   public static  String skos = "http://www.w3.org/2004/02/skos/core#" ;
   static String  rdfs = "http://www.w3.org/2000/01/rdf-schema#" ;
   static String  rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
   static String  owl = "http://www.w3.org/2002/07/owl#" ;
   
   // An ontology model is an extension of the Jena RDF model 
  // static    OntModel OntoGraph = ModelFactory.createOntologyModel(); 
	

	
	public static OntModel createOntoBioP (String concept,OntModel OntoGraph) throws IOException, ParseException
	{

		// generating owl:class
		String URI = classToOnto (concept,OntoGraph);
		if(URI != null)
		{
			sameAsToOnto(concept,URI,OntoGraph) ;
		    prefLabelToOnto(concept,URI,OntoGraph) ;
		    //OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
			synonymToOnto(concept,URI,OntoGraph)	;
			definitionToOnto (concept,URI,OntoGraph) ;
			semTypeToOnto(concept,URI,OntoGraph) ;
			//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
			loadTaxonomic(concept,URI,1,OntoGraph);	
			//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		}
		return OntoGraph ;
	}
	
	public static OntModel createOntoBioP (String concept) throws IOException, ParseException
	{
		OntModel OntoGraph = ModelFactory.createOntologyModel();
		OntoGraph.setNsPrefix( "skos", skos ) ;
		
		// generating owl:class
		String URI = classToOnto (concept,OntoGraph);
		sameAsToOnto(concept,URI,OntoGraph) ;
	    prefLabelToOnto(concept,URI,OntoGraph) ;
	   // OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		synonymToOnto(concept,URI,OntoGraph)	;
		definitionToOnto (concept,URI,OntoGraph) ;
		semTypeToOnto(concept,URI,OntoGraph) ;
		OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		loadTaxonomic(concept,URI,1,OntoGraph);	
		//OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		return OntoGraph ;
	}
	
	// adding a concept as owl class and rdfs:label
	public static String classToOnto (String concept,OntModel OntoGraph)
	{
		// the URI is equal to preflabel uri
		String conceptURI = bioportal.getConceptID(concept);
		System.out.println("classToOnto");
		if (conceptURI != null )
		{
			OntClass rec = OntoGraph.createClass(conceptURI);
			// assign a Label 
			final Property p = ResourceFactory.createProperty(rdfs + "label") ;
			rec.addProperty(p, concept);
			return conceptURI ;
		}
		return null;
	}
	// adding a concept as owl class and rdfs:label
	public static OntClass classToOnto_URI (String conceptURI,OntModel OntoGraph)
	{
		// the URI is equal to preflabel uri
		System.out.println("classToOnto");
		if (conceptURI != null )
		{
			OntClass rec = OntoGraph.createClass(conceptURI);
			return rec ;
		}
		return null;
	}
	
	// generating synonyms with skos:altLabel
	public static void synonymToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> Synonyms =  bioportal.getSynonyms(concept);
		System.out.println("getontoSynonym");
		Resource r = null ; 
		for (String syn: Synonyms.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(skos + "altLabel") ;
				r.addProperty(p, syn);
			}
			else
				break ; 
			
		}
	}
	
	// generating prefLable with skos:prefLabel
	public static void prefLabelToOnto(String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		String prefLabel = bioportal.getPrefLabels(concept);
		System.out.println("prefLabel");
		if(prefLabel != null)
		{
			Resource r = null ; 
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
					final Property p = ResourceFactory.createProperty(skos + "prefLabel") ;
					r.addProperty(p, prefLabel);
			}
		}
	}
	
	
	public static void loadTaxonomic(String concept,String URI,int maxLevel,OntModel OntoGraph) throws IOException, ParseException {
		
		
		List<String>   listTaxon = bioportal.getTaxonomic(concept,1) ; 
 
		// get the class reference 
		OntClass child = OntoGraph.getOntClass(URI) ; 
		OntClass r1 = null ; 
		// loop though hierarchy 
		for(String hier: listTaxon)
		{
			// get the uri of the parent 
			String conceptURI = bioportal.getConceptID(hier);
			if(conceptURI ==  null)
				continue ; 
			// check if the parent already exit in the graph 
			if (( r1 = OntoGraph.getOntClass(conceptURI) ) != null)
			{
				child.addSuperClass(r1);
			}
			else
			{
				// create new class and assign it as parent 
				String uri = classToOnto (hier,OntoGraph);
				r1 = OntoGraph.getOntClass(uri) ;
				final Property p1 = ResourceFactory.createProperty(rdfs + "label") ;
				r1.addProperty(p1, hier);
				child.addSuperClass(r1);
			}
			child = r1 ; 
		}

	}
	
	// generating definition 
	public static void definitionToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> defs =  bioportal.getDefinitions(concept);
		System.out.println("getontoDefinition");
		Resource r = null ; 
		for (String def:  defs.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(skos + "definition") ;
				r.addProperty(p, def);
			}
			else
				break ; 
			
		}
	}
	
	public static void semTypeToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> defs =  bioportal.getSemanticTypes(concept);
		System.out.println("getontoSemantic Type");
		Resource r = null ; 
		for (String def:  defs.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(skos + "type") ;
				r.addProperty(p, def);
			}
			else
				break ; 
			
		}
	}
	public static void sameAsToOnto (String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		Map<String, Integer> sameases =  bioportal.getSameas(concept,URI);
		System.out.println("sameAsToOnto Type");
		Resource r = null ; 
		if (sameases == null)
			return ; 
		for (String sameas:  sameases.keySet())
		{
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
				final Property p = ResourceFactory.createProperty(owl + "sameAs") ;
				r.addProperty(p,sameas);
			}
			else
				break ; 
			
		}
	}
	
	

}
