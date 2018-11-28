package NER;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import util.NGramAnalyzer;
import util.removestopwords;
import NLP.preProcessing;

public class Engine {

	public static void main(String[] args) throws Exception {
/*		String text = "Diabetes is a chronic condition associated with abnormally high levels of glucose in the blood" ;
		// TODO Auto-generated method stub
		Map<String, Integer> mentions = new HashMap<String, Integer>();
		Map<String, Integer> concepts = new HashMap<String, Integer>();
		List<String> sentences = preProcessing.getSentences(text) ; 
		for (String sentence: sentences)
		{
			text = removestopwords.removestopwordfromsen(text) ;
			mentions = NGramAnalyzer.entities(1,3, text) ;		
			concepts.putAll(ontologyMapping.getAnnotation(mentions)) ; 
		}
		
        System.out.println(concepts);*/
		
		getAvaluation() ;
	}
	
	
	
	/*************************************************************************************************************************************************************************/
	/************************************************************ Evaluation Part *******************************************************************************************/
	  public static void getAvaluation()
	  {
		  Map<String, List<String>> titles =  ReadCDR_TestSet_BioC()  ; 
		  String result = getmeasureRPF( titles) ;
		  System.out.println(result);
		  
	  }
	  
	  public static String getmeasure(Map<String, List<String>> titles)
		{
			

			double avgRecall = 0.0  ; 
			double avgPrecision = 0.0 ;
			double avgFmeasure = 0.0 ; 
			int size_ = titles.size() ;
			int counter = 0 ; 
			for(String title : titles.keySet())
			{
				counter++  ; 
				// get concepts of the title 
				List<String> GoldSndconcepts = titles.get(title); 
				
				try {
					
					
					Map<String, Integer> concepts = new HashMap<String, Integer>();
					Map<String, Integer> mentions = new HashMap<String, Integer>();
					
					
					title  = removestopwords.removestopwordfromsen(title ) ;
					mentions = NGramAnalyzer.entities(1,3, title ) ;
					// removed to run the gui
					//concepts = ontologyMapping.getAnnotation(mentions)  ;


					String[] arr = new String[concepts.size()] ;
					int i = 0 ; 
					for( String concept : concepts.keySet())
					{
						arr[i] = concept.toLowerCase() ;
						i++ ; 
					}

					// measure the recall precision and  F-measure 
					double relevent = 0 ;
					for( String concept : arr)
					{
	                   if (GoldSndconcepts.contains(concept.toLowerCase()))
	                   {
	                	   relevent++ ; 
	                   }
						
					}
					
					// calculate the Recall 
					//For example for text search on a set of documents recall is the number of correct results divided by the number of results that should have been returned
					double recall = 0.0 ; 
					if (GoldSndconcepts.size() > 0  )
					{
						recall = relevent / GoldSndconcepts.size() ;
					}

					avgRecall = recall  + avgRecall; 
					
					double precision = 0 ; 
					if ( arr.length > 0  )
					   precision = relevent / arr.length ;
					
					 avgPrecision += precision ;	
					
					double Fmeasure  = 0.0 ;
					if (precision + recall > 0 )
					   Fmeasure = 2* ((precision * recall) / (precision + recall)) ;
					   avgFmeasure = Fmeasure + avgFmeasure ;

	                
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			avgRecall = avgRecall / titles.size() ;
			avgPrecision = avgPrecision / titles.size() ;
			avgFmeasure = avgFmeasure / titles.size() ;
			
			String result = Double.toString(avgRecall) + " " +  Double.toString(avgPrecision) +" " +  Double.toString(avgFmeasure) ;
			return result ;
		}
	  
	public static Map<String, List<String>> ReadCDR_TestSet_BioC() {

	    Map<String, List<String>> goldstandard = null ;
	    goldstandard =  null ;
	    if (goldstandard== null )
	    try {

       String filename = "F:\\eclipse64\\eclipse\\CDR_TestSet.BioC.xml" ;


	    goldstandard  = new HashMap<String, List<String>>();
		File fXmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
        
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		NodeList passageList = doc.getElementsByTagName("passage");
        int count = 0 ; 
		for (int i = 0; i < passageList.getLength()  ; i++)
		{
			List<String> conclist = new ArrayList<String>() ;
			NodeList childList = passageList.item(i).getChildNodes();
			for (int j = 0; j < childList.getLength(); j++)
			{
				if ("infon".equals(childList.item(j).getNodeName()) && "title".equals(childList.item(j).getTextContent()))
				{
					String title = null ; 
					NodeList childList1 = passageList.item(i).getChildNodes();
					for (int kk = 0; kk < childList1.getLength(); kk++)
					{
						 System.out.println(childList1.item(kk).getNodeName());
						if ("text".equals(childList1.item(kk).getNodeName()))
						{
							 System.out.println(childList1.item(kk).getTextContent()
					                    .trim());
							title = childList1.item(kk).getTextContent()
				                    .trim().toLowerCase() ;

							continue ; 
						}
						
						if ("annotation".equals(childList1.item(kk).getNodeName()))
						{
							NodeList childList2 = childList.item(kk).getChildNodes();
							for (int kkk = 0; kkk < childList2.getLength(); kkk++)
							{
								if ("text".equals(childList2.item(kkk).getNodeName()))
								{
									 conclist.add(childList2.item(kkk).getTextContent().trim().toLowerCase()) ;
									 System.out.println(childList2.item(kkk).getTextContent()
							                    .trim());

								}
							}

						}
					}
					
					goldstandard.put(title,conclist) ;
					
				}
			}
           
		}

	    } 
	    catch (Exception e) {
		e.printStackTrace();
	    }
	    return goldstandard;
	  } ;
	  
	  
	  // prefer
	  
	  public static String getmeasureRPF(Map<String, List<String>> titles)
		{
			

//			double avgRecall = 0.0  ; 
//			double avgPrecision = 0.0 ;
//			double avgFmeasure = 0.0 ; 
//			int size_ = titles.size() ;
			int counter = 0 ; 
			measure result = null ;
			measure synResult = null ;
			measure ansResult = null ;
			measure allcResult = new measure() ;  
			measure allsynResult = new measure() ;  
			measure allansResult = new measure() ;  
			measure totResult = new measure() ;
			for(String title : titles.keySet())
			{
				counter++  ; 
				// get concepts of the title 
				List<String> GoldSndconcepts = titles.get(title); 
				
				try {
					
					
					Map<String, Integer> concepts = new HashMap<String, Integer>();
					Map<String, Integer> synConcepts = new HashMap<String, Integer>();
					Map<String, Integer> ansConcepts = new HashMap<String, Integer>();
					Map<String, Integer> keywords = new HashMap<String, Integer>();
					title  = removestopwords.removestopwordfromsen(title ) ;
					keywords = NGramAnalyzer.entities(1,6, title ) ;
					//concepts = ontologyMapping.getAnnotation(mentions)  ;
					for(String keyword: keywords.keySet())
					{
						if (ontologyMapping.getKeywordAnnotation(keyword) )
						{
							if (ontologyMapping.getSemanticGroupTypeDISO_CHEM(keyword) )
							{
								concepts.put(keyword, 1) ;
							}
						}
/*						else if (ontologyMapping.getKeywordSynAnnotation(keyword))
						{
							synConcepts.put(keyword, 1) ;
						}
						else if (ontologyMapping.getKeywordAnsAnnotation(keyword))
						{
							ansConcepts.put(keyword, 1) ;
						}*/
						
					}
					result = getPRF(title,concepts,GoldSndconcepts) ; 
					// with no syn and ans
					allcResult.avgRecall =  result.avgRecall +  allcResult.avgRecall ;
					allcResult.avgPrecision =  result.avgPrecision  + allcResult.avgPrecision ;
					allcResult.avgFmeasure =  result.avgFmeasure  + allcResult.avgFmeasure ;
					
/*					synResult = getPRF(title,synConcepts,GoldSndconcepts) ; 
					
					allsynResult.avgRecall =  synResult .avgRecall +  allsynResult.avgRecall ;
					allsynResult.avgPrecision =  synResult .avgPrecision  + allsynResult.avgPrecision ;
					allsynResult.avgFmeasure =  synResult .avgFmeasure  + allsynResult.avgFmeasure ;
					
					ansResult = getPRF(title,ansConcepts,GoldSndconcepts) ; 
					
					allansResult.avgRecall =  ansResult.avgRecall +  allansResult.avgRecall ;
					allansResult.avgPrecision =  ansResult.avgPrecision  + allansResult.avgPrecision ;
					allansResult.avgFmeasure =  ansResult.avgFmeasure  + allansResult.avgFmeasure ;
					
					totResult.avgRecall =  result.avgRecall +  synResult.avgRecall + ansResult.avgRecall + totResult.avgRecall ;
					totResult.avgPrecision =  result.avgPrecision +  synResult.avgPrecision + ansResult.avgPrecision + totResult.avgPrecision  ;
					totResult.avgFmeasure =  result.avgFmeasure+  synResult.avgFmeasure+ ansResult.avgFmeasure  + totResult.avgFmeasure;*/
					

					
					
					
	                
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			allcResult.avgRecall =  allcResult.avgRecall  / titles.size() ;
			allcResult.avgPrecision =  allcResult.avgPrecision / titles.size() ;
			allcResult.avgFmeasure =  allcResult.avgFmeasure / titles.size() ;
			
/*			allsynResult.avgRecall =  allsynResult.avgRecall  / titles.size() ;
			allsynResult.avgPrecision =  allsynResult.avgPrecision / titles.size() ;
			allsynResult.avgFmeasure =  allsynResult.avgFmeasure / titles.size() ;
			
			allansResult.avgRecall =  allansResult.avgRecall  / titles.size() ;
			allansResult.avgPrecision =  allansResult.avgPrecision / titles.size() ;
			allansResult.avgFmeasure =  allansResult.avgFmeasure / titles.size() ;
			
			
			totResult.avgRecall = totResult.avgRecall  / titles.size() ;
			totResult.avgPrecision = totResult.avgPrecision  / titles.size() ;
			totResult.avgFmeasure = totResult.avgFmeasure / titles.size() ;*/
			
			String output = Double.toString(allcResult.avgRecall) + " " +  Double.toString(allcResult.avgPrecision) +" " +  Double.toString(allcResult.avgFmeasure) ;
			return output;
		}
	  public static measure getPRF(String  titles, Map<String, Integer> concepts,List<String> GoldSndconcepts)
	  {
			
		measure result = new measure() ; 
		result.Recall = 0.0  ; 
		result.Precision = 0.0 ;
		result.Fmeasure = 0.0 ; 
		try {

			String[] arr = new String[concepts.size()] ;
			int i = 0 ; 
			for( String concept : concepts.keySet())
			{
				arr[i] = concept.toLowerCase() ;
				i++ ; 
			}

			// measure the recall precision and  F-measure 
			double relevent = 0 ;
			for( String concept : arr)
			{
               if (GoldSndconcepts.contains(concept.toLowerCase()))
               {
            	   relevent++ ; 
               }
				
			}
			
			// calculate the Recall 
			//For example for text search on a set of documents recall is the number of correct results divided by the number of results that should have been returned
			double recall = 0.0 ; 
			if (GoldSndconcepts.size() > 0  )
			{
				recall = relevent / GoldSndconcepts.size() ;
			}

			result.Recall = recall ; 
			result.avgRecall = recall  + result.avgRecall ; 
			
			double precision = 0 ; 
			if ( arr.length > 0  )
			   precision = relevent / arr.length ;
			
			result.Precision = precision ;	
			result.avgPrecision = precision + result.avgPrecision ;  
			
			double Fmeasure  = 0.0 ;
			if (precision + recall > 0 )
			   Fmeasure = 2* ((precision * recall) / (precision + recall)) ;
			result.Fmeasure = Fmeasure  ;
			result.avgFmeasure = Fmeasure + result.avgFmeasure  ;
            
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result ;
	}

}
