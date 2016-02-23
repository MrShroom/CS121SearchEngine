/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 */
package CS121SearchEngine;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class TestForIndexFile {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) 
	{		
		//#### Start These structures will be collectively or individually used in(or as) part of our index
		//a map from term to term id
		 HashMap<String, Integer> termToTermIDMap;
		//a map from term id to term 
		 HashMap<Integer, String> termIDToTermMap;
		//a map for document id to a map from term Id to term count in that document
		 HashMap<Integer, HashMap<Integer, Integer>> docIDToTermIDToTermCountMap;
		//a map for term id to a map from document Id to term count in that document
		 HashMap<Integer, HashMap<Integer, Integer>> termIDToDocIDToTermCountMap;
		//a map for term id to a map from document Id to term Frequency Normalized to add to ~1 in that document
		HashMap<Integer, HashMap<Integer, Float>> termIDToDocIDToTFNormilizedMap;
		//a map for term id to a map from document Id to a logarithmic Weighted term Frequency in that document
		 HashMap<Integer, HashMap<Integer, Float>> termIDToDocIDToWTFMap;
		//a map from term id to it's inverse document frequency
		 HashMap<Integer, Float> termIDToIdfMap ;
		//### End
		try{

			FileInputStream fin = new FileInputStream("data/indexDebug.ser");
			ObjectInputStream ois = new ObjectInputStream(fin);
			termToTermIDMap = (HashMap<String, Integer>) ois.readObject();
			termIDToTermMap =(HashMap<Integer, String>)ois.readObject();
			docIDToTermIDToTermCountMap = ( HashMap<Integer, HashMap<Integer, Integer>>)ois.readObject();
			termIDToDocIDToTermCountMap = ( HashMap<Integer, HashMap<Integer, Integer>>)ois.readObject();
			termIDToDocIDToTFNormilizedMap = ( HashMap<Integer, HashMap<Integer, Float>> )ois.readObject();
			termIDToDocIDToWTFMap = (  HashMap<Integer, HashMap<Integer, Float>>)ois.readObject();
			termIDToIdfMap = (HashMap<Integer, Float>) ois.readObject();
			ois.close();

			System.out.println("termToTermIDMap (map from term to term id) : \n" 
							+ termToTermIDMap + "\n");
			System.out.println("termIDToTermMap (map from term id to term): \n"
							+termIDToTermMap  + "\n");
			System.out.println("docIDToTermIDToTermCountMap (map for document id to a map from term Id to term count in that document):\n" 
							+ docIDToTermIDToTermCountMap  + "\n");
			System.out.println("termIDToDocIDToTermCountMap (map from document Id to term count in that document):\n" 
							+ termIDToDocIDToTermCountMap  + "\n");
			System.out.println("termIDToDocIDToTFNormilizedMap (map for term id to a map from document Id to term Frequency Normalized to add to ~1 in that document) :\n"
							+ termIDToDocIDToTFNormilizedMap + "\n");
			System.out.println("termIDToDocIDToWTFMap ( map from document Id to a logarithmic Weighted term Frequency in that document):\n" 
							+ termIDToDocIDToWTFMap + "\n");
			System.out.println("termIDToIdfMap(map from term id to it's inverse document frequency):\n"  
							+ termIDToIdfMap + "\n");

		}catch(Exception ex){
			ex.printStackTrace();

		} 

	}

}
