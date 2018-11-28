package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import util.ReadXMLFile;

public class dataExtractor {
	
    //*************************************************************
	// not used any more 
	//*************************************************************
	
	public static void main(String[] args) throws IOException {
		
		semanticGroupAbbr(null) ;  
	}
	
	public static List<String> dataExtraction(String Keyword,String max) throws IOException {
		// TODO Auto-generated method stub

		List<String> Totaltitles = new ArrayList<String>();
		int count = 0 ; 
		int counttot = 0 ; 
		// get ID of article  for each concepts 
		//Map<String, List<String>> Alzheimerigoldstandard =  
		for (int i = 0 ; i < 1 ; i++)
		{

			String conpt = Keyword ;
			conpt  =    conpt .replace(" ", "%20")  ;
			// esearch 
			String urlStr = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=" + max + "&term=" + conpt + "[Title]%20AND%20(%222017/01/01%22[PDAT]%20:%20%222018/12/31%22[PDAT])" ;
			String result = restcalls.httpGet(urlStr);
			if (result == null )
				continue ; 
			List<String> IDs= ReadXMLFile.ReadPubmed(result,"Id") ;
			if (IDs == null)
				continue ;
			for (String id: IDs)
			{
				String urlid ="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+ id + "&retmode=xml" ;
				String resultid = restcalls.httpGet(urlid );
				if (resultid == null )
					continue ; 
				List<String> titles= ReadXMLFile.ReadPubmed(resultid ,"ArticleTitle") ;
				if (titles != null)
				{
					for (String title : titles )
					{
						Totaltitles.add(title) ; 
					}
				}
			}
		}
		
		
        return Totaltitles ; 
	}
	
	
	
	public static void Alzheimer(String[] args) throws IOException {
		// TODO Auto-generated method stub

		List<String> Totaltitles = new ArrayList<String>();
		int count = 0 ; 
		int counttot = 0 ; 
		// get ID of article  for each concepts 
		Map<String, List<String>> Alzheimerigoldstandard =  ReadXMLFile.Deserialize("F:\\eclipse64\\eclipse\\AlzheimerGoldstandard") ;
		for (String conpt : Alzheimerigoldstandard.keySet())
		{
			counttot++ ;
			conpt  =    conpt .replace(" ", "%20")  ;
			// esearch 
			String urlStr = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=100&term=" + conpt + "[Title]%20AND%20(%222017/01/01%22[PDAT]%20:%20%222018/12/31%22[PDAT])" ;
			String result = restcalls.httpGet(urlStr);
			if (result == null )
				continue ; 
			List<String> IDs= ReadXMLFile.ReadPubmed(result,"Id") ;
			if (IDs == null)
				continue ;
			for (String id: IDs)
			{
				// efetch 
				//https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=29630950&retmode=xml
				String urlid ="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+ id + "&retmode=xml" ;
				String resultid = restcalls.httpGet(urlid );
				if (resultid == null )
					continue ; 
				List<String> titles= ReadXMLFile.ReadPubmed(resultid ,"ArticleTitle") ;
				if (titles != null)
				{
					for (String title : titles )
					{
						Totaltitles.add(title) ; 
					}
				}
			}
			count++ ;
			if (count == 100)
			{
				count = 0 ;
				ReadXMLFile.Serializeddir(Totaltitles, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesAlzforconcepts.dat");
			}
		}
		
		ReadXMLFile.Serializeddir(Totaltitles, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesAlzforconcepts.dat");

	}
	
	public static void VTE(String[] args) throws IOException {
		// TODO Auto-generated method stub

		List<String> Totaltitles = new ArrayList<String>();
		int count = 0 ; 
		int counttot = 0 ; 
		// get ID of article  for each concepts 
		//Map<String, List<String>> Alzheimerigoldstandard =  
		for (int i = 0 ; i < 1 ; i++)
		{

			String conpt = "Venous thromboembolism" ;
			conpt  =    conpt .replace(" ", "%20")  ;
			// esearch 
			String urlStr = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=1000&term=" + conpt + "[Title]%20AND%20(%222017/01/01%22[PDAT]%20:%20%222018/12/31%22[PDAT])" ;
			String result = restcalls.httpGet(urlStr);
			if (result == null )
				continue ; 
			List<String> IDs= ReadXMLFile.ReadPubmed(result,"Id") ;
			if (IDs == null)
				continue ;
			for (String id: IDs)
			{
				// efetch 
				//https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=29630950&retmode=xml
				String urlid ="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+ id + "&retmode=xml" ;
				String resultid = restcalls.httpGet(urlid );
				if (resultid == null )
					continue ; 
				List<String> titles= ReadXMLFile.ReadPubmed(resultid ,"ArticleTitle") ;
				if (titles != null)
				{
					for (String title : titles )
					{
						Totaltitles.add(title) ; 
					}
				}
			}
			count++ ;
			if (count == 100)
			{
				count = 0 ;
				ReadXMLFile.Serializeddir(Totaltitles, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesVTE.dat");
			}
		}
		
		ReadXMLFile.Serializeddir(Totaltitles, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesVTE.dat");

	}
	
	/// extracted from MEDLINE for all titles that has Alzheimer for 01/2017-04/2018
	public static List<String> getAlzheimerTitles(String[] args) {
		// TODO Auto-generated method stub
		List<String> titleList = null ; 
		try {
			 
			titleList = ReadXMLFile.Deserializedirlis("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesAlz.dat") ;
			if (titleList != null)
			{
				return   titleList ; 
			}
			else
			{
				new ArrayList<String>();
			}
			
			File filename  = new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\data\\Alzheimer2017_18_Title.txt") ;
			List<String> patts = readfiles.readLinesbylines(filename.toURL()) ;
			String concept = "Alzheimer" ; 

			try {
			for (int i = 0; i < patts.size(); i++)
	        {
		        String text =  patts.get(i) ; 
		    	//System.out.println(text);
		    	if (text.toLowerCase().contains(concept.toLowerCase()) )
		    		titleList.add(text);
	        }
			}
			 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			
			
	        ReadXMLFile.Serializeddir(titleList, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesAlz.dat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return titleList ; 
		
	}
	
	
	
	
	/// extracted from MEDLINE for all titles that has Alzheimer for 01/2017-04/2018
		public static List<String> getAlzheimerTitlesForallConcepts() {
			// TODO Auto-generated method stub
			List<String> titleList = null ; 
				titleList = ReadXMLFile.Deserializedirlis("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesAlzforconcepts.dat") ;
				if (titleList != null)
				{
					return   titleList ; 
				}
				else
				{
					return null ; 
				}
			
		}
		
		
		public static void aneurysm(String[] args) throws IOException {
			// TODO Auto-generated method stub

			List<String> Totaltitles = new ArrayList<String>();
			int count = 0 ; 
			int counttot = 0 ; 
			// get ID of article  for each concepts 
			//Map<String, List<String>> Alzheimerigoldstandard =  
			for (int i = 0 ; i < 1 ; i++)
			{

				String conpt = "Intracranial aneurysm" ;
				conpt  =    conpt .replace(" ", "%20")  ;
				// esearch 
				String urlStr = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=1000&term=" + conpt + "[Title]%20AND%20(%222000/01/01%22[PDAT]%20:%20%222018/12/31%22[PDAT])" ;
				String result = restcalls.httpGet(urlStr);
				if (result == null )
					continue ; 
				List<String> IDs= ReadXMLFile.ReadPubmed(result,"Id") ;
				if (IDs == null)
					continue ;
				for (String id: IDs)
				{
					// efetch 
					//https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=29630950&retmode=xml
					String urlid ="https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id="+ id + "&retmode=xml" ;
					String resultid = restcalls.httpGet(urlid );
					if (resultid == null )
						continue ; 
					List<String> titles= ReadXMLFile.ReadPubmed(resultid ,"ArticleTitle") ;
					if (titles != null)
					{
						for (String title : titles )
						{
							Totaltitles.add(title) ; 
						}
					}
					
					count++ ;
					if (count == 100)
					{
						break ; 
						//count = 0 ;
						//ReadXMLFile.Serializeddir(Totaltitles, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesVTE.dat");
					}
				}

			}
			
			ReadXMLFile.Serializeddir(Totaltitles, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\titlesaneurysm.dat");

		}
		
		
		public static void semanticGroup(String[] args) throws IOException
		{
			// TODO Auto-generated method stub

			List<String> Totaltitles = new ArrayList<String>();
			Map<String, String> SemanticDir   = new HashMap<String, String>();

			File filename  = new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\TripleExtraction\\umls\\SemGroups_2013.txt") ;
			List<String> lines = readfiles.readLinesbylines(filename.toURL()) ;
			
			for (String line:lines)
			{
				String tokens[] = line.split("\\|"); 
				SemanticDir.put(tokens[2], tokens[1]); 
				
			}
			
			
			ReadXMLFile.Serializeddiectionary(SemanticDir, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\SemanticGroupDir.dat");

		}
	
		public static void semanticGroupAbbr(String[] args) throws IOException
		{
			// TODO Auto-generated method stub

			List<String> Totaltitles = new ArrayList<String>();
			Map<String, String> SemanticDir   = new HashMap<String, String>();
			Map<String, String> SemanticDir1   = new HashMap<String, String>();
			Map<String, String> SemanticDir2   = new HashMap<String, String>();

			File filename  = new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\TripleExtraction\\umls\\SemGroups_2013.txt") ;
			List<String> lines = readfiles.readLinesbylines(filename.toURL()) ;
			
			for (String line:lines)
			{
				String tokens[] = line.split("\\|"); 
				SemanticDir.put(tokens[2], tokens[1]); 
				
			}
			
			
			File filename1  = new File("C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\TripleExtraction\\umls\\SemanticTypes_2013AA.txt") ;
			List<String> lines1 = readfiles.readLinesbylines(filename1.toURL()) ;
			for (String line:lines1)
			{
				String tokens[] = line.split("\\|"); 
				SemanticDir1.put(tokens[1], tokens[0]); 
				
			}
			
			for (String gp:SemanticDir1.keySet())
			{
				SemanticDir.get(gp); 
				SemanticDir2.put(SemanticDir1.get(gp), SemanticDir.get(gp));
			}
			
			
			
			ReadXMLFile.Serializeddiectionary(SemanticDir2, "C:\\Users\\mazina\\Desktop\\School\\Khalid\\Paper\\Distance Supervision NER\\Data Medline_PubMed\\SemanticGroupDirAbbr.dat");

		}
}
