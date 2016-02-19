package CS121SearchEngine;

import java.sql.SQLException;

public class PageFetchTest {

	public static void main(String[] args) 
	{
		try {
			
			PageFetcher fecther = new PageFetcher(0,1);
			int topId = PageFetcher.getLargestID();
			System.out.println(topId);
			System.out.println(fecther.getNext());
			fecther.close();
			fecther = new PageFetcher(topId-1,topId);
			System.out.println(fecther.getNext());
			fecther.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
