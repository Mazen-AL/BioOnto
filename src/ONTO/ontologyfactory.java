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

public class ontologyfactory {

   static  String skos = "http://www.w3.org/2004/02/skos/core#" ;
   static String  rdfs = "http://www.w3.org/2000/01/rdf-schema#" ;
   static String  rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
   static String  owl = "http://www.w3.org/2002/07/owl#" ;
   static String  lo = "http://www.lifeOnto.org/lifeOnto#" ; 
    
   // An ontology model is an extension of the Jena RDF model 
  // static    OntModel OntoGraph = ModelFactory.createOntologyModel(); 
	
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		
		//createOnto("diabetes");
	}
	
	public static OntModel createOnto (String concept, OntModel OntoGraph) throws IOException, ParseException
	{

		// generating owl:class
		String URI = classToOnto (concept,OntoGraph);
		if (URI!= null)
		{
			sematicGroupToOnto(concept,URI, OntoGraph) ; 
			definitionToOnto(concept,URI, OntoGraph) ;
			synonymToOnto(concept,URI,OntoGraph)	;	
			prefLabelToOnto(concept,URI,OntoGraph) ;
			generateTaxonomic(concept,URI,4,OntoGraph); 
			OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
		}
		return OntoGraph ;
	}
	

	
	// adding a concept as owl class and rdfs:label
	public static String classToOnto (String concept,OntModel OntoGraph)
	{
		// the URI is equal to preflabel uri
		String conceptURI = Enrichment.getURI(concept);
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
		Map<String, Integer> Synonyms = Enrichment.getSynonyms(concept);
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
		String prefLabel = Enrichment.getPrefLabel(concept);
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
	
	// generating semantic type 
	public static void sematicGroupToOnto(String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		// generating synonyms wiht skos:altLabel
		String semGroup = Enrichment.getSemanticGroupType(concept);

		if(semGroup != null)
		{
			Resource r = null ; 
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
					final Property p = ResourceFactory.createProperty(lo + "semGroup") ;
					r.addProperty(p, semGroup );
			}
		}
	}
	
	
	// generating definition 
	public static void definitionToOnto(String concept,String URI,OntModel OntoGraph) throws IOException, ParseException
	{
		
		
		List<String> def = Enrichment.LLDDefinition(concept);

		if(def  != null && !def.isEmpty())
		{
			Resource r = null ; 
			if ( ( r= OntoGraph.getOntClass(URI) ) != null)
			{
					final Property p = ResourceFactory.createProperty(skos + "definition") ;
					r.addProperty(p, def.get(0) );
			}
		}
	}
	// this one is used 
	public static void generateTaxonomic(String concept,String URI,int maxLevel,OntModel OntoGraph) throws IOException {
		
		
		List<String>   listTaxon = hierarchy.Taxonomic_Extractor_origin(concept,URI,0,maxLevel) ; 
		
		
		
		ArrayList al = new ArrayList(maxLevel); 
		for (int i = 0; i <=  maxLevel ; ++i)
		{
			List<String> levelList = new ArrayList<String>() ;
			al.add(levelList);
		}
		
		for(String item:listTaxon)
		{
			String[] token  = item.split("!");
			int index= Integer.parseInt(token[2]); 
			List<String> level = (List<String>) al.get(index);
			level.add(item);
			al.set(index, level);
			
		}
		
		OntClass r0 = null ; 
		OntClass r1 = null ; 
		OntClass r2 = null ;
		
		if ( ( r0= OntoGraph.getOntClass(URI) ) != null)
		{
			for (int index = 1 ; index <= maxLevel ; ++index)
			{
				
				@SuppressWarnings("unchecked")
				List<String> level = (List<String>) al.get(index);
				
				for (int idx = 1 ; idx < level.size()  ; ++idx)
				{
					String[] token = level.get(idx).split("!");
					if(token.length == 4)
					{
						if ( ( r1= OntoGraph.getOntClass(token[3]) ) != null) // origin resource
						{
							    final Property p = ResourceFactory.createProperty(rdfs + "subClassOf") ;
						       // r.addProperty(p, token[0]);
						        
						        if ( ( r2= OntoGraph.getOntClass(token[0]) ) != null)
						        {
						        	r1.addSuperClass(r2);
								    final Property p1 = ResourceFactory.createProperty(skos + "altLabel") ;
								    if(token.length >1)
							         r2.addProperty(p1, token[1]);
						        }
						        else
						        {
						        	// create class
						        	r2= classToOnto_URI(token[0],OntoGraph);
								    final Property p1 = ResourceFactory.createProperty(rdfs + "label") ;
								    if(token.length >1)
							          r2.addProperty(p1, token[1]);
								    r1.addSuperClass(r2);
						        	
						        }
						        
						        /// label 
							   // final Property p2 = ResourceFactory.createProperty(rdfs + "label") ;
						       // r.addProperty(p2, token[0]);
						}

					}
				}
				
			}
		}
			

		

	}
	public static void loadTaxonomic(String concept,String URI,int maxLevel,OntModel OntoGraph) throws IOException {
		
		
		List<String>   listTaxon = hierarchy.Taxonomic_Extractor_origin(concept,URI,0,maxLevel) ; 
		 ArrayList al = new ArrayList(maxLevel); 
		
		for (int i = 0; i <=  maxLevel ; ++i)
		{
			List<String> levelList = new ArrayList<String>() ;
			al.add(levelList);
		}
		
		for(String item:listTaxon)
		{
			String[] token  = item.split("!");
			int index= Integer.parseInt(token[2]); 
			List<String> level = (List<String>) al.get(index);
			level.add(item);
			al.set(index, level);
			
		}
		Resource r = null ; 
		if ( ( r= OntoGraph.getOntClass(URI) ) != null)
		{
			for (int index = 1 ; index <= maxLevel ; ++index)
			{
				
				@SuppressWarnings("unchecked")
				List<String> level = (List<String>) al.get(index);
				
				for (int idx = 1 ; idx < level.size()  ; ++idx)
				{
					String[] token = level.get(idx).split("!");
					if(token.length == 4)
					{
						if ( ( r= OntoGraph.getOntClass(token[3]) ) != null) // origin resource
						{
							    final Property p = ResourceFactory.createProperty(rdfs + "subClassOf") ;
						        r.addProperty(p, token[0]);
						        if ( ( r= OntoGraph.getOntClass(token[0]) ) != null)
						        {
								    final Property p1 = ResourceFactory.createProperty(skos + "altLabel") ;
								    if(token.length >1)
							         r.addProperty(p1, token[1]);
						        }
						        else
						        {
						        	// create class
						        	r= classToOnto_URI(token[0],OntoGraph);
								    final Property p1 = ResourceFactory.createProperty(rdfs + "label") ;
								    if(token.length >1)
							          r.addProperty(p1, token[1]);
						        	
						        }
						        
						        /// label 
							   // final Property p2 = ResourceFactory.createProperty(rdfs + "label") ;
						       // r.addProperty(p2, token[0]);
						}

					}
				}
				
			}
		}
			

		

	}
}
