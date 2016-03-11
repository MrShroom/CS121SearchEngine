/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 */
package CS121SearchEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PageFetcherForSearchEngine {
	
	private Connection dBConnection;
    private ResultSet rs;
	private static final String DB_URL = "jdbc:mysql://shaunmcthomas.me:3306/cs121DB";
	private static final String DB_USER = "dbuser";
	private static final String DU_PASSWORD = "password";
	
    public PageFetcherForSearchEngine() throws SQLException
	{	
        dBConnection = DriverManager.getConnection(DB_URL, DB_USER, DU_PASSWORD);
	}
	 
	public Document getDoc(Integer docID) throws SQLException
	{
		String statement = "SELECT Id, Url, WebText, Html FROM Visited_URL WHERE Id=?;";
		java.sql.PreparedStatement preparedStatement = dBConnection.prepareStatement(statement);
		preparedStatement.setInt(1, docID);
		
		rs = preparedStatement.executeQuery();
		if(rs.next())
			return new Document(rs.getInt("Id"), rs.getString("Url"),rs.getString("WebText"), rs.getString("Html") );
		return null;
	}
	
	
	
	public void close() throws SQLException
	{
		dBConnection.close();
		rs.close();
	}
	

}
