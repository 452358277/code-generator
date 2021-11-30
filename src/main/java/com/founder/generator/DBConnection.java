package com.founder.generator;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
	
	private static final String DRIVER_CLASSNAME = (String)PropertyHelper.getJdbcProperty().get("jdbc.driverClassName");
	private static final String URL = (String)PropertyHelper.getJdbcProperty().get("jdbc.url");
	private static final String USERNAME = (String)PropertyHelper.getJdbcProperty().get("jdbc.username");
	private static final String PASSWORD = (String)PropertyHelper.getJdbcProperty().get("jdbc.password");
	
	public static Connection getConnection() throws Exception {
		try {
			Driver dbDriver = (Driver) Class.forName(DRIVER_CLASSNAME).newInstance();
			DriverManager.registerDriver(dbDriver);
			return DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void close(Connection conn,PreparedStatement pstmt,ResultSet rs){
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			/*if (stmt != null) {
				stmt.close();
				stmt = null;
			}*/
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
