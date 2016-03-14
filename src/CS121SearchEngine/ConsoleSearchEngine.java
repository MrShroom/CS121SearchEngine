package CS121SearchEngine;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ConsoleSearchEngine {
	
	private static final int NUMBER_OF_RESULTS =10;

	public static SearchLogicClass mySearch = new SearchLogicClass();
	public static PageFetcherForSearchEngine fetcher;
	public static Scanner input = new Scanner(System.in);
	
	
	public static void main(String[] args) 
	{
		String query = "";
		
		while(true)
		{
			System.out.print("Enter your query:");
			query = input.next();
			if(query.equals("##exit##"))
				break;
			TreeMap <Integer,Float> results = mySearch.preformSearch(query);
			
			int k = (results.size() > NUMBER_OF_RESULTS ? NUMBER_OF_RESULTS : results.size());
			for(Map.Entry<Integer,Float> itr : results.entrySet() )
			{
				System.out.println(itr.getKey() + " --> score: " + itr.getValue());
				try {
					fetcher = new PageFetcherForSearchEngine();
					System.out.println(fetcher.getDoc(itr.getKey()));
					fetcher.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(k == 0)
					break;
				k--;
				
			}
			
		}
		
		input.close();
		
	}

}
