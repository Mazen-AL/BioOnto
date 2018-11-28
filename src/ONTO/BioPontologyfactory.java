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
	
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		
		createOntoBioP("Venous Thromboembolism");
	}

	
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
	
	/*public static void getontoSemanticType (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoSemanticType");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "rdf", rdf ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Category = dataset.Category ;
	   		if (Category != null)
	   		{
	   			for (String Definition: Category)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty(rdf  + "type") ;
	 	         	rec.addProperty(p, Definition);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	


	
	public static void getontosameAs (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontosameas");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		
   			String topuri = ""; 
   			Map<String, Double> uriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: uriconfident.keySet())
	   		{
    			topuri = onto ;
	   		}
	   		
	   		if (topuri.isEmpty())
	   			   continue ; 
	   		
	   		
	   		
	   		graph.setNsPrefix( "owl", owl ) ;
   			
   			List<String> Topuriconfident = dataset.getTopBesturiconfident(3,0.5) ;
   			
   			if (Topuriconfident.size() > 1 )
   			{

		   		int count = 0 ; 
		   		for (String tempuri: Topuriconfident)
		   		{
		   			count++ ;
	    			if (count == 1)
	    				continue ; 
	    			
	    			
	    			
	    			
	    			// create sameas relation 
	    			String[] uri  = tempuri.split("!", 2) ;
	    			if(uri.length > 0 )
	    			{
		   				Resource rec = graph.createResource(topuri);
	 	        		// add the property
		 	         	final Property p = ResourceFactory.createProperty(owl  + "sameAs") ;
		 	         	rec.addProperty(p, uri[0]);
	    			}
	    			
		   		}
   			}
	   		
   	 	}
	}
	

	public static void getontodefinition (Map<String, Dataset> lookupresources)
	{
		
		System.out.println("getontodefinition");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
	   		// set the lexical alt label
	   		 List<String> Definitions = dataset.Definition ;
	   		if (Definitions != null)
	   		{
	   			for (String Definition: Definitions)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( skos + "definition") ;
	 	         	rec.addProperty(p, Definition);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	
	public static void getontoscheme (Map<String, Dataset> lookupresources)
	{
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ;  
	   		
	   		// set the lexical alt label
	   		 List<String> scheme = dataset.ontology ;
	   		if (scheme != null)
	   		{
	   			for (String label: scheme)
	   			{
	   				Resource rec = graph.createResource(uri);
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty(skos  + "inScheme") ;
	 	         	rec.addProperty(p, label);
	   			}
	   			
	   		}
	   		
   	 	}
	}
	public static void getontoPreflabel (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoPreflabel");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
   			
   			
   			if (Topuriconfident.size() == 0 )
   			   continue ; 	
   			 
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
   			
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
   			Map<String,List<String>> syn = new HashMap<String, List<String>>();
   			String PrefLabel = dataset.PrefLabel ;
   			
   			if (PrefLabel == null )
   				continue ; 
   			String tokens[] = PrefLabel.split(" ") ;
			Resource rec = graph.createResource(uri);
			Resource rec1 = graph.createResource(tokens[0]);
    		// add the property
         	final Property p = ResourceFactory.createProperty(skos+ "PrefLabel") ;
         	rec.addProperty(p, rec1);	
         	final Property p1 = ResourceFactory.createProperty(skos + "PrefLabel") ;
         	String Label = tokens[2] ;
         	
         	System.out.println(Label);
         	Label = Label.replace(")", " ") ;
         	Label = Label.replace("(", " ") ;
         	Label = Label.trim() ;
         	rec1.addProperty(p, Label);
   	 	}
	}
	
	public static void getontoSynonym (Map<String, Dataset> lookupresources)
	{
		System.out.println("getontoSynonym");
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getcandidateGraph();
	   		graph.setNsPrefix( "skos", skos ) ;
   			String uri = ""; 
   			
   			Map<String, Double> Topuriconfident = dataset.gettopuriconfident() ;
	   		for (String onto: Topuriconfident.keySet())
	   		{
    			uri = onto ;
	   		}
	   		
	   		if (uri.isEmpty())
	   			   continue ; 
	   		
   			Map<String,List<String>> syn = new HashMap<String, List<String>>();
   			List<String> Syns = dataset.Synonym ;
   			if (Syns == null )	
   				continue ;
   			
   			
   			double max = 0.0 ;
   			List<String> alts = new ArrayList<String>()  ;
   			
	    	for (String synon: Syns)
	    	{    		
	    		String[] words = synon.split("!");  
	    		
	    		if (syn.containsKey(words[1].toLowerCase()))
	    		{
	    			alts = syn.get(words[1].toLowerCase());
	    			if (!alts.contains(words[2].toLowerCase()))  
	    			{
	    				alts.add(words[2].toLowerCase()) ;
	    			}
	    		}
	    		else
	    		{
	    			alts.add(words[2].toLowerCase()) ;
	    			syn.put(words[1].toLowerCase(), alts) ;
	    		}

	    	}
	    	
	    	for (String label: syn.keySet())
	    	{
   				Resource rec = graph.createResource(uri);
	        		// add the property
 	         	final Property p = ResourceFactory.createProperty(skos + "altLabel") ;
 	         	rec.addProperty(p, label);
	    		
	    		
	    		
	    	}
   	 	}
	}
	
	
	public static void getontoassociate (Map<String, Dataset> lookupresources)
	{
		 Map<String, Dataset> tempresources = new HashMap<String, Dataset>();
		 tempresources.putAll(lookupresources);
		// construct whole subgraph for each concept
		for (String concept: lookupresources.keySet())
   	 	{
			System.out.println("******************" + concept + "******************");
			
	   		Dataset dataset = lookupresources.get(concept) ;
	   		Model graph = dataset.getGraph();
	   		Model candidategraph = dataset.getcandidateGraph() ;
			// list the statements in the Model
			StmtIterator iter =  graph.listStatements();
	   		
	    	while (iter.hasNext())
			{
			    Statement stmt      = iter.nextStatement();  // get next statement
			    Resource  subject   = stmt.getSubject();     // get the subject
			    Property  predicate = stmt.getPredicate();   // get the predicate
			    RDFNode   object    = stmt.getObject();      // get the object
			    for (String conceptin: tempresources.keySet()) 
			    {
			    	
			    	if (concept.equals(conceptin))
			    		continue ; 
			   		Dataset datasetin = lookupresources.get(concept) ;
			   		Model graphin = dataset.getGraph();
					// list the statements in the Model
					StmtIterator iterin =  graph.listStatements();
					while (iterin.hasNext())
					{
					    Statement stmtin      = iterin.nextStatement();  // get next statement
					    Resource  subjectin   = stmtin.getSubject();     // get the subject
					    Property  predicatein = stmtin.getPredicate();   // get the predicate
					    RDFNode   objectin    = stmtin.getObject();      // get the object
					    
					    // add a resource
					    if (object.toString().equals(subjectin.toString()) )
					    {
			   				Resource rec = candidategraph.createResource(subject);
		 	        		// add the property
			 	         	final Property p = ResourceFactory.createProperty(predicate.toString()) ;
			 	         	rec.addProperty(p, subjectin.toString());
			 	         	System.out.println(object.toString() + ", " + predicate.toString() + ", " + subjectin.toString());
					    }
					    
					    // add a resource
					    if(object.toString().equals(objectin.toString()) && predicate.toString().equals(predicatein))
					    {
			   				Resource rec = candidategraph.createResource(subject);
		 	        		// add the property
			 	         	final Property p = ResourceFactory.createProperty(predicate.toString()) ;
			 	         	rec.addProperty(p, subjectin.toString());
			 	         	System.out.println(object.toString() + ", " + predicate.toString() + ", " + subjectin.toString());
					    }
			    	
					}

			    }
	   		
   	 		}
   	 	}
	}
	
	
	public static void getontoPropertyHierarchy (String uri,String property ,Model graph) throws IOException
	{
		System.out.println("***********************************getontoHierarchy************************************************");

			
	   		graph.setNsPrefix( "skos", skos ) ;
	   		graph.setNsPrefix( "rdfs", rdfs ) ;
	   		
	   		
	   		
	   		List<String> Hierarchy = Enrichment.LLDHierarchyProperty(uri,property)  ;
	   		
	   		if (Hierarchy != null)
	   		{
	   			for (int i = Hierarchy.size()-2 ; i > -1; i--)
	   			{
	   				String hier = Hierarchy.get(i) ;
	   				String tokens[] = hier.split("!") ;
	   				Resource child = graph.createResource(uri);
	   				Resource parent = graph.createResource(tokens[0]);
	   				
 	        		// add the property
	 	         	final Property p = ResourceFactory.createProperty( rdfs + "subPropertyOf") ;
	 	         	child.addProperty(p, parent);
	 	         	
	 	         	
	 	         	final Property pp = ResourceFactory.createProperty(rdfs + "label") ;
	 	         	parent.addProperty(pp, tokens[1]);
	 	         	uri = tokens[0] ;
	   			}
	   			
	   		}
	   		

	}*/

}
