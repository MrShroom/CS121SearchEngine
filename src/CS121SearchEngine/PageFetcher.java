package CS121SearchEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageFetcher {
	
	public Connection dBConnection;
	Statement st;
    ResultSet rs;
	
	public PageFetcher() throws SQLException
	{
		String url = "jdbc:mysql://shaunmcthomas.me:3306/cs121DB";
		String user = "dbuser";
        String password = "password";
        dBConnection = DriverManager.getConnection(url, user, password);
        String statement = "SELECT Id,Url,WebText,Html FROM Visted_URL;";
        st = dBConnection.createStatement();
		rs = st.executeQuery(statement);
	}
	 
	public Document getNext() throws SQLException
	{
		if(rs.next())
			return new Document(rs.getInt("Id"), rs.getString("Url"),rs.getString("WebText"), rs.getString("Html") );
		return null;
	}

}
