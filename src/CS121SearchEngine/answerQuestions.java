/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 */
package CS121SearchEngine;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public class answerQuestions {
	private static final int NUMBER_OF_RESULTS =5;

	public static SearchLogicClass mySearch = new SearchLogicClass();
	public static PageFetcherForSearchEngine fetcher;

	public static void main(String[] args) 
	{
		try {
			PrintWriter out;
			out = new PrintWriter("answersForMilestone2.txt");
			out.println("1. Results for mondego : " + getQueryResults("mondego"));
			out.println("2. Results for machine learning : " + getQueryResults("machine learning"));
			out.println("3. Results for software engineering : " + getQueryResults("software engineering"));
			out.println("4. Results for security  : " + getQueryResults("security"));
			out.println("5. Results for student affairs : " + getQueryResults("student affairs"));
			out.println("6. Results for graduate courses : " + getQueryResults("graduate courses"));
			out.println("7. Results for informatics : " + getQueryResults("informatics"));
			out.println("8. Results for REST : " + getQueryResults("REST"));
			out.println("9. Results for computer games : " + getQueryResults("computer games"));
			out.println("10. Results for information retrieval : " + getQueryResults("information retrieval"));
			out.close();
		} catch (FileNotFoundException e) 
		{
			
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub

	}
	
	public static String getQueryResults(String query)
	{
		TreeMap <Integer,Float> results = mySearch.preformSearch(query);
		if(results.isEmpty())
			return ("No results\n");
		StringBuilder output = new StringBuilder();
		int i = 1;
		int k = (results.size() > NUMBER_OF_RESULTS ? NUMBER_OF_RESULTS : results.size());
		for(Map.Entry<Integer,Float> itr : results.entrySet() )
		{
			try {
				fetcher = new PageFetcherForSearchEngine();
				Document currentDoc = fetcher.getDoc(itr.getKey());
				fetcher.close();
				
				String html = currentDoc.getRawHTML();
				int start = html.toLowerCase().indexOf("<title>");
				int end = html.toLowerCase().indexOf("</title>");
				String title = "";
				if( end-start > 0)
				{
					start += 7;//"<title>".length();
					title = html.substring(start, end);
				}
				output.append("\n\t" + i + ": "+ title + "  " + currentDoc.getUrl() + "\n");
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(k == 1)
				break;
			k--;
			i++;
			
		}
		return output.toString();
		
	}

}
