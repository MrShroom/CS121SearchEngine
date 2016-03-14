/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 */
package CS121SearchEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SearchLogicClass 
{
	
	// Pre-compile Regex for small speed up
	private final static Pattern singleQoute = Pattern.compile("\'|`");

	// Pre-compile Regex for small speed up
	private final static Pattern replaceRegexPattern = Pattern.compile("[^A-Za-z0-9]+");
	
	// Set of words to remove from BOW
	private static HashSet<String> stopwords = new HashSet<String>();
	
	private static HashMap<Integer, HashMap<Integer, Float>> termIdToDocIdtoScoreMap;
	private static HashMap<String, Integer> termToTermIDMap;
	private static boolean finishedLoading = false;
	
	public SearchLogicClass()
	{
		try {
			populateStopWords();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadScoreMap();
	}
	
	@SuppressWarnings("unchecked")
	public void loadScoreMap()
	{
		InputStream fin  = null;
		ObjectInputStream ois =null;
		try {

			
			try{
				fin = new FileInputStream("data/indexScoreOnly.ser");
			}catch(FileNotFoundException ex)
			{
				fin = this.getClass().getResourceAsStream("/data/indexScoreOnly.ser");	
			}

			ois = new ObjectInputStream(fin);
			termIdToDocIdtoScoreMap = (HashMap<Integer, HashMap<Integer, Float>>) ois.readObject();
			ois.close();
			fin.close();
			
			try{
				fin = new FileInputStream("data/indexTermMapOnly.ser");
			}catch(FileNotFoundException ex)
			{
				fin = this.getClass().getResourceAsStream("/data/indexTermMapOnly.ser");	
			}			
			ois = new ObjectInputStream(fin);
			termToTermIDMap = (HashMap<String, Integer>) ois.readObject();
			ois.close();
			fin.close();
			
		} catch (IOException | ClassNotFoundException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
		}
		finishedLoading = true;
	}
	
	public TreeMap <Integer,Float> preformSearch(String query)
	{
		if(!finishedLoading)
		{
			TreeMap<Integer,Float > sorted_map = new TreeMap<Integer,Float >();
			sorted_map.put(148325, (float) 1);//created a fake page to return if the index is not yet loaded. 
			return sorted_map;
		}
		
		HashMap<Integer,Float >  scores = new  HashMap<Integer,Float > ();
		ValueComparator bvc = new ValueComparator(scores);
	    TreeMap<Integer,Float > sorted_map = new TreeMap<Integer,Float >(bvc);
		
	    HashSet<String> queryVector = tokenizeText(query);
		ArrayList<Integer> termIdsInQuery = new  ArrayList<Integer>();
		
		for(String term : queryVector)
		{
			termIdsInQuery.add(termToTermIDMap.get(term));
		}
		
		for( Integer termId : termIdsInQuery )
		{
			if(termIdToDocIdtoScoreMap.get(termId) == null)
			{
				continue;
			}	
			
			for( Integer docID : termIdToDocIdtoScoreMap.get(termId).keySet())
			{
				if(scores.containsKey(docID))
				{
					scores.put(docID, scores.get(docID) + termIdToDocIdtoScoreMap.get(termId).get(docID));
				}
				else
				{
					scores.put(docID, termIdToDocIdtoScoreMap.get(termId).get(docID));
				}
			}
				
		}
		sorted_map.putAll(scores);
		return sorted_map;		
	}
	
	//this implementation of Tree map comparitor is taken from http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	class ValueComparator implements Comparator <Integer>{
	    Map<Integer, Float > base;

	    public ValueComparator(Map<Integer,Float >  base) {
	        this.base = base;
	    }

		@Override
		public int compare(Integer o1, Integer o2) 
		{
			Float a = base.get(o1);
			Float b = base.get(o2);
			if (a.compareTo(b) >= 0) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
		}

	}
	
	private  void populateStopWords() throws FileNotFoundException 
	{
		// Create set of stop words from file "stopwords"
		Scanner in = null;
		try{
			in = new Scanner(new File("stopwords"));
		}catch(FileNotFoundException ex)
		{
			in = new Scanner(this.getClass().getResourceAsStream("/stopwords"));
		}
		

		while (in.hasNext()) 
		{
			stopwords.add(singleQoute.matcher(in.nextLine().trim().toLowerCase()).replaceAll(""));
		}
		stopwords.add("");
		in.close();
	}

	
	public static HashSet<String> tokenizeText(String input) 
	{
		// remove all single quotes
		input = singleQoute.matcher(input).replaceAll("").trim();

		// Change case to lower and replace all non word charters with space
		input = replaceRegexPattern.matcher(input.toLowerCase()).replaceAll(" ").trim();

		// Create new Array list to hold tokens
		HashSet<String> tokens = new HashSet<String>(Arrays.asList(input.split(" ")));

		// remove stopwords
		tokens.removeAll(stopwords);

		return tokens;
	}
}
