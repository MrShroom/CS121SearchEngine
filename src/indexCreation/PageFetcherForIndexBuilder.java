/**
 * Shaun McThomas 13828643
 * Sean Letzer 24073320
 * Sean King 82425468 
 */
package indexCreation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import CS121SearchEngine.Document;

public class PageFetcherForIndexBuilder {
	
	private Connection dBConnection;
	private Statement st;
    private ResultSet rs;
	private static final String DB_URL = "jdbc:mysql://shaunmcthomas.me:3306/cs121DB";
	private static final String DB_USER = "dbuser";
	private static final String DU_PASSWORD = "password";
	
    public PageFetcherForIndexBuilder() throws SQLException
	{	
        dBConnection = DriverManager.getConnection(DB_URL, DB_USER, DU_PASSWORD);
        String statement = "SELECT Id,Url,WebText,Html FROM Visited_URL;";
        st = dBConnection.createStatement();
		rs = st.executeQuery(statement);
	}
	 
    public PageFetcherForIndexBuilder(int startID, int stopID) throws SQLException
	{	
        dBConnection = DriverManager.getConnection(DB_URL, DB_USER, DU_PASSWORD);
        String statement = "SELECT Id,Url,WebText,Html FROM Visited_URL WHERE Id BETWEEN "
        					+ startID + " AND " + stopID + " ;" ;
        st = dBConnection.createStatement();
		rs = st.executeQuery(statement);
	}
	public Document getNext() throws SQLException
	{
		if(rs.next())
			return new Document(rs.getInt("Id"), rs.getString("Url"),rs.getString("WebText"), rs.getString("Html") );
		return null;
	}
	
	public void close() throws SQLException
	{
		dBConnection.close();
		st.close();
		rs.close();
	}
	
	public static int getLargestID()
	{
		Connection dBConnection = null;
		Statement st = null;
		ResultSet rs =null;
		int out = 0;
		
        try {
        	dBConnection = DriverManager.getConnection(DB_URL, DB_USER, DU_PASSWORD);
        	String statement = " select MAX( Id ) from  Visited_URL;" ;
        	st = dBConnection.createStatement();
			rs = st.executeQuery(statement);
			if (rs.next())
				out = rs.getInt(1);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally
        {
			try {
				if(dBConnection != null )dBConnection.close();
				if(st != null) st.close();
				if(rs != null) rs.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
        }
        return out;
	}
	
	public static int getCorpusSize()
	{
		Connection dBConnection = null;
		Statement st = null;
		ResultSet rs =null;
		int out = 0;
		
        try {
        	dBConnection = DriverManager.getConnection(DB_URL, DB_USER, DU_PASSWORD);
        	String statement = " select COUNT( * ) from  Visited_URL;" ;
        	st = dBConnection.createStatement();
			rs = st.executeQuery(statement);
			if (rs.next())
				out = rs.getInt(1);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally
        {
			try {
				if(dBConnection != null )dBConnection.close();
				if(st != null) st.close();
				if(rs != null) rs.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
        }
        return out;
	}
	
	public static int getEstNumberOfWords()
	{
		Connection dBConnection = null;
		Statement st = null;
		ResultSet rs =null;
		int out = 0;
		
        try {
        	dBConnection = DriverManager.getConnection(DB_URL, DB_USER, DU_PASSWORD);
        	String statement = " select SUM( NumberOfWords  ) from  Visited_URL;" ;
        	st = dBConnection.createStatement();
			rs = st.executeQuery(statement);
			if (rs.next())
				out = rs.getInt(1);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally
        {
			try {
				if(dBConnection != null )dBConnection.close();
				if(st != null) st.close();
				if(rs != null) rs.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
        }
        return out;
	}

}
