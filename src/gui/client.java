package gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import NER.ontologyMapping;
import ONTO.BioPontologyfactory;
import ONTO.ontologyfactory;
import util.NGramAnalyzer;
import util.ReadXMLFile;
import util.removestopwords;

public class client {
	  static  String skos = "http://www.w3.org/2004/02/skos/core#" ;
	   static String  rdfs = "http://www.w3.org/2000/01/rdf-schema#" ;
	   static String  rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#" ;
	   static String  owl = "http://www.w3.org/2002/07/owl#" ;

	public static void main(String[] args) throws IOException, ParseException
	{
		// TODO Auto-generated method stub
		OntModel OntoGraph = ModelFactory.createOntologyModel();
		OntoGraph.setNsPrefix( "skos", skos ) ;
		// is disease
		
		List<String> titleList = new ArrayList<String>();
		titleList = ReadXMLFile.Deserializedirlis("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesaneurysm.dat");
		int count = 0 ;
		int counter = 0 ; 
		for (String title : titleList)
		{
			String text  = removestopwords.removestopwordfromsen(title) ;
			Map<String, Integer> mentions = NGramAnalyzer.entities(1,3, text) ;
			Map<String, Integer> concepts = null ;
			try 
			{
				concepts = ontologyMapping.getAnnotation(mentions)  ;
				for ( String cpt : concepts.keySet())
				{
					if (!cpt.isEmpty())
					{
						//ontologyfactory.createOnto(cpt,OntoGraph) ; 
						BioPontologyfactory.createOntoBioP(cpt,OntoGraph);
					}
				}
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++ ; 
			counter++; 
			 if (count == 50)
			 {
				     System.out.println("+++++++++++++++++++++++++++++++++");
				     System.out.println(counter);
				     System.out.println("+++++++++++++++++++++++++++++++++");
			        // Creating a File object that represents the disk file.
			        PrintStream o = new PrintStream(new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\ANEBioPortal.rdf"));
			        
			        // Store current System.out before assigning a new value
			        PrintStream console = System.out;
			 
			        // Assign o to output stream
			        System.setOut(o);	
					OntoGraph.write(System.out, "RDF/XML-ABBREV") ;
					
			        // Use stored value for output stream
			        System.setOut(console);
			        count = 0 ; 
			 }
			
		}
		
		System.out.println("++++++++++++++Done+++++++++++++++");

	}
}

