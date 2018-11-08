/**
 * 
 */
package com.jpn.skd.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import com.jpn.skd.form.SKD_PFormLogin;
import com.jpn.util.DBUtil;

/**
 * @author Me
 *
 */
public class SKD_Logger {
	
	public static void in(String userId) {
		
		DBUtil dbUtil = new DBUtil();	
		Connection con = dbUtil.getDBConnection();
		
		PreparedStatement statement;
		try {
			statement = con.prepareStatement("INSERT INTO audit_logpengguna log_type, user_id, date_in VALUES(?, ?, ?)");
			Calendar cal = Calendar.getInstance();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
			statement.setString(1, "in");
			statement.setString(2, userId);
			statement.setTimestamp(3, timestamp);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void out(String userId) {
		
		DBUtil dbUtil = new DBUtil();	
		Connection con = dbUtil.getDBConnection();
		
		PreparedStatement statement;
		try {
			statement = con.prepareStatement("INSERT INTO audit_logpengguna log_type, user_id, date_out VALUES(?, ?, ?)");
			Calendar cal = Calendar.getInstance();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());
			statement.setString(1, "out");
			statement.setString(2, userId);
			statement.setTimestamp(3, timestamp);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
