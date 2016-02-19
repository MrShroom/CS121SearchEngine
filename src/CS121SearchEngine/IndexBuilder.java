package CS121SearchEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;



public class IndexBuilder 
{
	//Pre-compile Regex for small speed up
	private final static Pattern singleQoute = Pattern.compile("\'|`");
	
	//Pre-compile Regex for small speed up
	private final static Pattern replaceRegexPattern = Pattern.compile("[^A-Za-z0-9]+");
	
	//Set of words to remove from BOW
	private static HashSet<String> stopwords = new HashSet<String>();
	
	//These three structures are collectively our index 
	private static HashMap< String, Integer > term2termid = new HashMap<String, Integer>();
	private static HashMap< Integer, HashSet < Integer > > docid2termlist = new HashMap<Integer, HashSet<Integer>>();
	private static HashMap< Integer, String > term2idterm = new HashMap< Integer, String >();
	
	private static int currentTermId = 0;
	
	public static void main(String args[])
	{
		makeIndex();
		writeIndexToDisk();
	}
	
	public static void writeIndexToDisk()
	{
		try{
		   
			FileOutputStream fout = new FileOutputStream("data/index.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(term2termid);
			oos.writeObject(docid2termlist);
			oos.writeObject(term2idterm);
			oos.close();
			System.out.println("Done");
		   
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	 
	}
	
	public static void makeIndex()
	{		
		try {
			//Create set of stop words from file "stopwords"
			Scanner in = new Scanner(new File("stopwords"));
		
			while(in.hasNext())
			{
				stopwords.add(singleQoute.matcher(in.nextLine().trim().toLowerCase()).replaceAll(""));
			}
			stopwords.add("");
			in.close();	
			
			PageFetcher myPageFetcher = new PageFetcher(0,100);

			for(Document currentDoc = myPageFetcher.getNext();currentDoc != null; currentDoc = myPageFetcher.getNext())
			{
				docid2termlist.put(currentDoc.getDocId(), new HashSet< Integer >());
				HashSet< Integer > adjacencyList = docid2termlist.get(currentDoc.getDocId());
				for(String term : tokenizeText(currentDoc.getBody()) )
				{
					int termkey;
					if(!term2termid.containsKey(term))
					{
						term2termid.put(term, ++currentTermId);						
						termkey = currentTermId;
						term2idterm.put(termkey, term);
					}
					else
					{
						termkey = term2termid.get(term);
					}
					adjacencyList.add(termkey);					
				}
			}
			
		} catch (FileNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashSet<String> tokenizeText(String input) 
	{
		input = singleQoute.matcher(input).replaceAll("").trim();
		input = replaceRegexPattern.matcher(input.toLowerCase()).replaceAll(" ").trim();//Change case to lower and remove all non word charters
		HashSet<String> tokens = new HashSet<String>(Arrays.asList(input.split(" ")));//Create new Array list to hold tokens
		tokens.removeAll(stopwords);
		return tokens;
	}
}
