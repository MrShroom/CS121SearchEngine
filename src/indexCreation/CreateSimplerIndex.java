/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 * 
 * This is used to create acttual index that will be used
 */
package indexCreation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class CreateSimplerIndex {
	
	// #### Start These structures will be collectively or individually used
	// in(or as) part of our index
	// a map from term to term id
	private static HashMap<String, Integer> termToTermIDMap;
	// a map from term id to term
	private static HashMap<Integer, String> termIDToTermMap;
	// a map for document id to a map from term Id to term count in that
	// document
	private static HashMap<Integer, HashMap<Integer, Integer>> docIDToTermIDToTermCountMap;
	// a map for term id to a map from document Id to term count in that
	// document
	private static HashMap<Integer, HashMap<Integer, Integer>> termIDToDocIDToTermCountMap;
	// a map for term id to a map from document Id to term Frequency
	// Normalized to add to ~1 in that document
	private static HashMap<Integer, HashMap<Integer, Float>> termIDToDocIDToTFNormilizedMap;
	// a map for term id to a map from document Id to a logarithmic Weighted
	// term Frequency in that document
	private static HashMap<Integer, HashMap<Integer, Float>> termIDToDocIDToWTFMap;
	// a map from term id to it's inverse document frequency
	private static HashMap<Integer, Float> termIDToIdfMap;
	// ### End
	
	//The final index we will use (term ID -> Doc ID -> score);
	private static HashMap<Integer, HashMap<Integer, Float>> finalIndextoExport;

	public static void main(String[] args) {
		
		loadLargeData();
		makeTFIDFusingWeighted();
		writeFinalIndex();

	}

	/**
	 * Load the data produced in the Index creation portion of assignment 
	 */
	@SuppressWarnings("unchecked")
	private static void loadLargeData() {
		try {

			FileInputStream fin = new FileInputStream("data/index.ser");
			ObjectInputStream ois = new ObjectInputStream(fin);
			termToTermIDMap = (HashMap<String, Integer>) ois.readObject();
			termIDToTermMap = (HashMap<Integer, String>) ois.readObject();
			docIDToTermIDToTermCountMap = (HashMap<Integer, HashMap<Integer, Integer>>) ois.readObject();
			termIDToDocIDToTermCountMap = (HashMap<Integer, HashMap<Integer, Integer>>) ois.readObject();
			termIDToDocIDToTFNormilizedMap = (HashMap<Integer, HashMap<Integer, Float>>) ois.readObject();
			termIDToDocIDToWTFMap = (HashMap<Integer, HashMap<Integer, Float>>) ois.readObject();

			termIDToIdfMap = (HashMap<Integer, Float>) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void makeTFIDFusingWeighted()
	{
		finalIndextoExport =  new HashMap<Integer, HashMap< Integer, Float > >();
		for(Integer termId : termIDToTermMap.keySet())
		{
			finalIndextoExport.put(termId, new HashMap< Integer, Float >());
			for(Integer docID : termIDToDocIDToWTFMap.get(termId).keySet())
			{
				finalIndextoExport.get(termId).put(docID, 
						termIDToDocIDToWTFMap.get(termId).get(docID) * termIDToIdfMap.get(termId));
			}
		}
		
	}
	
	public static void writeFinalIndex()
	{
		try
		{
			FileOutputStream fout;
			fout = new FileOutputStream("data/indexScoreOnly.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);			
			oos.writeObject(finalIndextoExport);			
			oos.close();
			
			fout = new FileOutputStream("data/indexTermMapOnly.ser");
			oos = new ObjectOutputStream(fout);			
			oos.writeObject(termToTermIDMap);			
			oos.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
