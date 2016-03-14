/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 */
package indexCreation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;

import CS121SearchEngine.Document;


public class IndexBuilder {
	// Parameters for Debug mode (limit number of pages and word per page)
	private final static boolean DEBUG = false;
	private final static int DEBUG_PAGE_AMOUNT = 5;
	private final static int DEBUG_WORDS_PER_PAGE_AMOUNT = 10; //Integer.MAX_VALUE;
	
	//Be sure that if DEBUG_PAGE_AMOUNT is larger than BATCH_SIZE, then BATCH_SIZE is a multiple of DEBUG_PAGE_AMOUNT
	//Number of documents to fetch at a time, smaller batches help keep run time memory usage down. However, larger batch go sightly faster. 
	private final static int BATCH_SIZE = (DEBUG ? Math.min(1000,DEBUG_PAGE_AMOUNT) : 1000); 
	
	// Pre-compile Regex for small speed up
	private final static Pattern singleQoute = Pattern.compile("\'|`");

	// Pre-compile Regex for small speed up
	private final static Pattern replaceRegexPattern = Pattern.compile("[^A-Za-z0-9]+");
	
	//number of documents to be used in the index
	private final static int COPUS_SIZE = (DEBUG ? DEBUG_PAGE_AMOUNT: PageFetcherForIndexBuilder.getCorpusSize());
	
	private final static int LARGEST_DOC_ID = PageFetcherForIndexBuilder.getLargestID();
	
	// Set of words to remove from BOW
	private static HashSet<String> stopwords = new HashSet<String>();

	//#### Start These structures will be collectively or individually used in(or as) part of our index
	/****************************************
	 *  Note, We are storing much more then we will ultimately need. This is to give us more flexibility in choosing 
	 *  the term ranking for term in the documents. With the information stored, we can :
	 *  
	 *  1. choose a straight boolean score by finding the term in termIDToDocIDToTermCountMap the checking if the sub map 
	 *     has the doc ID
	 *  
	 *  2. choose a score based on Term Frequency, by finding the term in termIDToDocIDToTermCountMap the checking the sub 
	 *     map for the doc ID which will give use the Term Frequency
	 *     
	 *  3. choose a score based on Term Frequency which has been normalized based on words in each document, by finding the term in 
	 *     termIDToDocIDToTFNormilizedMap the checking the sub map for the doc ID which will give use the normalized Term Frequency
	 *     
	 *  4. choose a score based on Weighted Term Frequency, by finding the term in termIDToDocIDToWTFMap the checking the sub map for
	 *   the doc ID which will give use the Weighted Term Frequency.
	 *  
	 *  5 choose a TF-IFD score by multiplying one of the above scores by the value in termIDToIdfMap for the term id. 
	 *  
	 *  6. choose another method not mentioned here using the additional data structures saved. 
	 *  
	 *  If the method of scoring was chosen before hand we would only need to store two Data Structures.  This would greatly reduce the 
	 *  memory usage of this index by at least 75%. 
	*/
	//a map from term to term id
	private static HashMap<String, Integer> termToTermIDMap = new HashMap<String, Integer>();
	//a map from term id to term 
	private static HashMap<Integer, String> termIDToTermMap = new HashMap<Integer, String>();
	//a map for document id to a map from term Id to term count in that document
	private static HashMap<Integer, HashMap<Integer, Integer>> docIDToTermIDToTermCountMap = new HashMap<Integer, HashMap<Integer, Integer>>(COPUS_SIZE);
	//a map for term id to a map from document Id to term count in that document
	private static HashMap<Integer, HashMap<Integer, Integer>> termIDToDocIDToTermCountMap = new HashMap<Integer, HashMap<Integer, Integer>>();
	
	//***Changed Double to Float to save memory,  at the cost of precision which we are unlikely to need. 
	//a map for term id to a map from document Id to term Frequency Normalized to add to ~1 in that document
	private static HashMap<Integer, HashMap<Integer, Float>> termIDToDocIDToTFNormilizedMap;
	
	//***Changed Double to Float to save memory,  at the cost of precision which we are unlikely to need. 
	//a map for term id to a map from document Id to a logarithmic Weighted term Frequency in that document
	private static HashMap<Integer, HashMap<Integer, Float>> termIDToDocIDToWTFMap;
	
	//***Changed Double to Float to save memory,  at the cost of precision which we are unlikely to need. 
	//a map from term id to it's inverse document frequency
	private static HashMap<Integer, Float> termIDToIdfMap;
	//### End
	
	//used to keep track of next TermId to be assigned
	private static int currentTermId = 0;
	
	
	public static void main(String args[]) 
	{
		long startTime  = System.currentTimeMillis();
		System.out.println("###Calling Make Index Function###\n");
		makeIndex();
		double makeIndexTime = (System.currentTimeMillis() - startTime)/1000.0;
		System.out.println("###Making Index Finished in " + (int)( makeIndexTime/60/60 )+ ":" + (int)(makeIndexTime/60)%60 + ":" + ((int)makeIndexTime%60%60)+ " hours:minutes:seconds");
		
		long startWrieTime  = System.currentTimeMillis();
		System.out.println("###Calling Write Index to disk Function###");
		writeIndexToDisk();
		double writeTime = (System.currentTimeMillis() - startWrieTime)/1000.0;
		System.out.println("###Writing to Disk Finished in " + (int)( writeTime/60/60 )+ ":" + (int)(writeTime/60)%60 + ":" + ((int)writeTime%60%60)+ " hours:minutes:seconds\n");
		
		
		try {
			PrintWriter out;
			out = new PrintWriter("answers.txt");
			out.println("1. There were " + COPUS_SIZE + " documents used to create the index." );
			out.println("2. There were " + termToTermIDMap.size() + " words." );
			out.println("3.Sample Index: \n To add later in PDF \n\n\n\n\n\n" );
			File file =new File(DEBUG?"data/indexDebug.ser":"data/index.ser");
			if(file.exists()){
				double kilobytes = (file.length() / 1024);
				out.println("4. The index file is " + kilobytes+ " kilobytes." );
			}
			out.print("5.The index file is created in " + (int)( makeIndexTime/60/60 )+ ":" + (int)(makeIndexTime/60)%60 + ":" + ((int)makeIndexTime%60%60)+ " hours:minutes:seconds " );
			out.print(" and written to disk in in " + (int)( writeTime/60/60 )+ ":" + (int)(writeTime/60)%60 + ":" + ((int)writeTime%60%60)+ " hours:minutes:seconds ");
			int total = (int)(makeIndexTime + writeTime);
			out.print(" total time is " + (int)( total/60/60 )+ ":" + (int)(total/60)%60 + ":" + ((int)total%60%60)+ " hours:minutes:seconds ");
			
			out.close();
		} catch (FileNotFoundException e) 
		{
			
			e.printStackTrace();
		}
		
	}

	/**
	 * 	
	 * Serializes and writes the parts on the index to disk.
	 * 
	 */
	public static void writeIndexToDisk() 
	{
		try
		{
			FileOutputStream fout;
			if (DEBUG)//add debug flag to output file name if in debug mode
				fout = new FileOutputStream("data/indexDebug.ser");
			else
				fout = new FileOutputStream("data/index.ser");
			
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			System.out.println("Writing termToTermIDMap to Disk..");
			oos.writeObject(termToTermIDMap);
			
			System.out.println("Writing termIDToTermMap to Disk..");
			oos.writeObject(termIDToTermMap);
			
			System.out.println("Writing docIDToTermIDToTermCountMap to Disk..");
			oos.writeObject(docIDToTermIDToTermCountMap);
			
			System.out.println("Writing termIDToDocIDToTermCountMap to Disk..");
			oos.writeObject(termIDToDocIDToTermCountMap);
			
			System.out.println("Writing termIDToDocIDToTFNormilizedMap to Disk..");
			oos.writeObject(termIDToDocIDToTFNormilizedMap);
			
			System.out.println("Writing termIDToDocIDToWTFMap to Disk..");
			oos.writeObject(termIDToDocIDToWTFMap);
			
			System.out.println("Writing termIDToIdfMap to Disk..");
			oos.writeObject(termIDToIdfMap);
			
			oos.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 * This is the main function that builds the index structures. 
	 * 
	 */
	public static void makeIndex() {
		try {
			
			System.out.println("Populating Stop words...\n");
			//populate stop words to use later in tokenizer
			populateStopWords();

			//create structure to track number of terms per document
			HashMap<Integer, Integer> docIDToTermCountMap = new HashMap<Integer, Integer>(COPUS_SIZE);
			
			int numberOfBatches = (COPUS_SIZE%BATCH_SIZE == 0 ? COPUS_SIZE/BATCH_SIZE : COPUS_SIZE/BATCH_SIZE + 1);
			System.out.println("There are " + numberOfBatches + " batchs to loop through...\n");
			
			for(int i = 0; i < numberOfBatches; i++ )
			{
				System.out.println("Querying the Database, batch:" + (i+1) + "...\n");
				//create structure used to get documents from database
				PageFetcherForIndexBuilder myPageFetcher;
				
				// last batch might miss a few, lets make sure that it doesn't(we don't care if in debug mode as long as we're close)
				if (!DEBUG && i == numberOfBatches-1)
					myPageFetcher = new PageFetcherForIndexBuilder(i*BATCH_SIZE, LARGEST_DOC_ID);
				else
					myPageFetcher = new PageFetcherForIndexBuilder(i*BATCH_SIZE,(i+1)*BATCH_SIZE);	
				
				System.out.println("Lopping through the Corpus...\n");
				//loop through each document
				for (Document currentDoc = myPageFetcher.getNext(); currentDoc != null; currentDoc = myPageFetcher.getNext()) 
				{
					//create a new map in docIDToTermIDToTermCountMap
					docIDToTermIDToTermCountMap.put(currentDoc.getDocId(), new HashMap<Integer, Integer>());
					//get reference to that map
					HashMap<Integer, Integer> currentTermIDToTermCountMap = docIDToTermIDToTermCountMap.get(currentDoc.getDocId());
	
					//tokenize document into terms
					ArrayList<String> termsInCurrentDoc = tokenizeText(currentDoc.getBody());
					
					//store number of tokens in document
					docIDToTermCountMap.put(currentDoc.getDocId(), termsInCurrentDoc.size());
					
					//loop through all terms
					for (String term : termsInCurrentDoc) 
					{		
						//for each term update necessary elements for index
						int termkey;
						
						if (!termToTermIDMap.containsKey(term)) 
						{
							termToTermIDMap.put(term, ++currentTermId);
							termkey = currentTermId;
							termIDToTermMap.put(termkey, term);
							termIDToDocIDToTermCountMap.put(termkey, new HashMap<Integer, Integer>());
						} 
						else 
						{
							termkey = termToTermIDMap.get(term);
						}
	
						HashMap<Integer, Integer> currentDocIDToTermCountMap = termIDToDocIDToTermCountMap.get(termkey);
						currentDocIDToTermCountMap.put(currentDoc.getDocId(), 1 + (currentDocIDToTermCountMap.containsKey(currentDoc.getDocId())
								? currentDocIDToTermCountMap.get(currentDoc.getDocId()) : 0 ));
	
						currentTermIDToTermCountMap.put(termkey, 1 + (currentTermIDToTermCountMap.containsKey(termkey) 
								? currentTermIDToTermCountMap.get(termkey) : 0 ));
					}
				}
			
				myPageFetcher.close();
			}
			System.out.println("termToTermIDMap Complete...");
			System.out.println("termIDToTermMap Complete...");
			System.out.println("docIDToTermIDToTermCountMap Complete...");
			System.out.println("termIDToDocIDToTermCountMap Complete...\n");
			
			System.out.println("Lopping through the terms...\n");
			
			int numberOfTerms = termIDToDocIDToTermCountMap.keySet().size();
			termIDToDocIDToTFNormilizedMap = new HashMap<Integer, HashMap<Integer, Float>> (numberOfTerms);
			termIDToIdfMap = new HashMap<Integer, Float>(numberOfTerms);
			termIDToDocIDToWTFMap = new HashMap<Integer, HashMap<Integer, Float>> (numberOfTerms);
					
			//now that we have all the terms loop through them and update the necessary elements 
			for (Integer termId : termIDToDocIDToTermCountMap.keySet()) 
			{
				termIDToDocIDToTFNormilizedMap.put(termId, new HashMap<Integer, Float>());
				termIDToDocIDToWTFMap.put(termId, new HashMap<Integer, Float>());
				
				termIDToIdfMap.put(termId, (float)Math.log10((float)COPUS_SIZE/(float)termIDToDocIDToTermCountMap.get(termId).size()));
				
				for (Integer docId : termIDToDocIDToTermCountMap.get(termId).keySet()) 
				{
					termIDToDocIDToTFNormilizedMap.get(termId).put(docId, termIDToDocIDToTermCountMap.get(termId).get(docId).floatValue()
									/ docIDToTermCountMap.get(docId).floatValue());
					
					termIDToDocIDToWTFMap.get(termId).put(docId, (float) (1 + Math.log10(termIDToDocIDToTermCountMap.get(termId).get(docId))));
				}
			}
			System.out.println("termIDToDocIDToTFNormilizedMap Complete...");
			System.out.println("termIDToDocIDToWTFMap Complete...");
			System.out.println("termIDToIdfMap Complete...\n");

		} catch (FileNotFoundException | SQLException e)
		{
			
			e.printStackTrace();
		}
	}

	private static void populateStopWords() throws FileNotFoundException 
	{
		// Create set of stop words from file "stopwords"
		Scanner in = new Scanner(new File("stopwords"));

		while (in.hasNext()) 
		{
			stopwords.add(singleQoute.matcher(in.nextLine().trim().toLowerCase()).replaceAll(""));
		}
		stopwords.add("");
		in.close();
	}

	public static ArrayList<String> tokenizeText(String input) 
	{
		// remove all single quotes
		input = singleQoute.matcher(input).replaceAll("").trim();

		// Change case to lower and replace all non word charters with space
		input = replaceRegexPattern.matcher(input.toLowerCase()).replaceAll(" ").trim();

		// Create new Array list to hold tokens
		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(input.split(" ")));

		// remove stopwords
		tokens.removeAll(stopwords);

		if (DEBUG)// limit output in debug mode
			return new ArrayList<String>(tokens.subList(0, Math.min(tokens.size(), DEBUG_WORDS_PER_PAGE_AMOUNT)));
		return tokens;
	}
}
