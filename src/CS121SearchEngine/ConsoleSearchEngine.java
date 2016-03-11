package CS121SearchEngine;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ConsoleSearchEngine {

	public static SearchLogicClass mySearch = new SearchLogicClass();
	public static PageFetcherForSreachEngine fetcher;
	public static Scanner input = new Scanner(System.in);
	
	
	public static void main(String[] args) 
	{
		String query = "";
		
		try {
			fetcher = new PageFetcherForSreachEngine();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true)
		{
			System.out.print("Enter your query:");
			query = input.next();
			if(query.equals("##exit##"))
				break;
			TreeMap <Integer,Float> results = mySearch.preformSearch(query);
			
			int k = (results.size() > 20 ? 20 : results.size());
			for(Map.Entry<Integer,Float> itr : results.entrySet() )
			{
				System.out.println(itr.getKey() + " --> score: " + itr.getValue());
				try {
					System.out.println(fetcher.getDoc(itr.getKey()));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(k == 0)
					break;
				k--;
				
			}
			
		}
		try {
			fetcher.close();
			input.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
