package CS121SearchEngine;

import java.sql.SQLException;

public class PageFetchTest {

	public static void main(String[] args) 
	{
		try {
			
			PageFetcherForIndexBuilder fecther = new PageFetcherForIndexBuilder(0,1);
			int topId = PageFetcherForIndexBuilder.getLargestID();
			System.out.println(topId);
			System.out.println(fecther.getNext());
			fecther.close();
			fecther = new PageFetcherForIndexBuilder(topId-1,topId);
			System.out.println(fecther.getNext());
			fecther.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
