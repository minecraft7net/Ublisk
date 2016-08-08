package com.robinmc.ublisk.utils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.robinmc.ublisk.utils.Config;

public class MySQL extends SQLTableChanging {
	
	protected static Connection connection;

	public static DatabaseConnection getConnection(){
		return new DatabaseConnection("192.168.0.125", 3306, getUser(), getPassword(), "ublisk");
	}
	
	private static String getUser(){
		return Config.getString("mysql.user");
	}

	private static String getPassword(){
		return Config.getString("mysql.password");
	}
	
	public synchronized static void openConnection(DatabaseConnection dbCon) throws SQLException {
		String ip = dbCon.getIP();
		int port = dbCon.getPort();
		String user = dbCon.getUser();
		String pass = dbCon.getPassword();
		String db = dbCon.getDatabase();
		connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + db, user, pass);
	}
	
	public synchronized static void closeConnection() throws SQLException {
		connection.close();
	}
	
	public static void onDisable(){
		if (connection != null){
			try {
				closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void openConnection() throws SQLException {
		openConnection(getConnection());		
	}
}