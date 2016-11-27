package DB;
import java.awt.List;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.*;

public class DBmanager {
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/ARS_server";
	final String USERNAME = "root";
	final String PASSWORD = "123123";

	private static DBmanager instance;

	ServerSocket serverSocket = null;

	// PrintWriter out = null;
	BufferedReader in = null;

	Connection conn = null;
	Statement stmt = null;
	private DBmanager(){
		initialize();
	}

	public static DBmanager getInstace()
	{
		if(instance == null)
		{
			instance = new DBmanager();
			
		}

		return instance;
	}

	private void initialize(){
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
			System.out.println("<MySQL Connection Complete>");
			stmt = conn.createStatement();

			String sql;
			sql = "SELECT * FROM customer;";//
			ResultSet rs = stmt.executeQuery(sql);

			
			System.out.println("+------------------------------------------------------+");
			System.out.println("+                                                      +");
			System.out.println("+        DB <---> Server <---> Client connected        +");
			System.out.println("+                                                      +");
			System.out.println("+------------------------------------------------------+");
			System.out.println("");
			
		}catch(Exception e){

		}
	}
	public String processNum(int num){
		//System.out.println("okay!");
		String sql = "select cust_id from customer;";
		String name = null;
		int i = 0;
		try{

			PreparedStatement pre = conn.prepareStatement(sql);
			ResultSet result = pre.executeQuery();//result에 결과값을 반환한다.

			while(result.next()){
				name += result.getString("cust_id") + ' ';
				i++;
			}

			//pre.executeQuery();//select
			//pre.executeUpdate();//update

		}catch(Exception e){
			e.printStackTrace();
		}
		return name;
	}

	public String LoginCheck(String cust_id, String cust_password) { //Login check
		// TODO Auto-generated method stub
		String id;
		String add1, add2, add3;
		String phone;
		String user_info;

		String sql = "select * from customer where cust_id = '" + cust_id + "' and cust_password = password('" + cust_password +"')";
		//String sql = "select * from customer where cust_id = 'id0001' and cust_password = '123123'";
		try {
			PreparedStatement pre = conn.prepareStatement(sql);
			ResultSet result = pre.executeQuery();
			while (result.next()){
				id = result.getString("cust_id");
				add1 = result.getString("address1");
				add2 = result.getString("address2");
				add3 = result.getString("address3");
				phone = result.getString("phone");
				user_info = id + " " + add1 + " " + add2 + " " + add3 + " " + phone;

				System.out.println(user_info);
				return user_info;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";

	}

	public void saveElec(String meter_id, String usage) { //Update usage
		// TODO Auto-generated method stub
		System.out.println("Usage from pi : " + meter_id + ", " + usage);
		String sql = "update electricity set e_usage = '"+ usage + "' where elec_meter = '" + meter_id + "' and yearmonth = '201611';";

		try {
			PreparedStatement pre = conn.prepareStatement(sql);
			int result  = pre.executeUpdate();
			System.out.println("---------ElecDB has been Updated----------");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	public String getCustId(String meter, String option){ //Get customer's id from meter_id
		String sql;
		PreparedStatement pre;
		String cust_id = null;
//		select cust_id from gas 
//		where gas_meter = 'g04'
		if(option.equals("w")){
			sql = "select cust_id from Water where water_meter = '" + meter + "';";
			try {
				pre = conn.prepareStatement(sql);
				ResultSet result = pre.executeQuery();
				while (result.next()){
					cust_id = result.getString("cust_id");
					return cust_id;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(option.equals("g")){
			sql = "select cust_id from gas where gas_meter = '" + meter + "';";
			try {
				pre = conn.prepareStatement(sql);
				ResultSet result = pre.executeQuery();
				while (result.next()){
					cust_id = result.getString("cust_id");
					return cust_id;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(option.equals("e")){
			sql = "select cust_id from Electricity where elec_meter = '" + meter + "';";
			try {
				pre = conn.prepareStatement(sql);
				ResultSet result = pre.executeQuery();
				while (result.next()){
					cust_id = result.getString("cust_id");
					return cust_id;

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else return "fail";
		return cust_id;

	}

	public String getFee(String cust_id, String option){
		String sql;
		PreparedStatement pre;
		String usage = null, yearmonth = null, info = null;

		if(option.equals("w")){
			info="w " + cust_id;
			sql = "select w_usage, yearmonth from Water where cust_id = '"+ cust_id + "';";

			try {
				pre = conn.prepareStatement(sql);
				ResultSet result = pre.executeQuery();
				while (result.next()){
					usage = result.getString("w_usage");
					yearmonth = result.getString("yearmonth");
					info += " " + yearmonth + " " + usage;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(option.equals("g")){
			info="g " + cust_id;
			sql = "select g_usage, yearmonth from GAS where cust_id = '"+ cust_id + "';";
			try {
				pre = conn.prepareStatement(sql);
				ResultSet result = pre.executeQuery();
				while (result.next()){
					usage = result.getString("g_usage");
					yearmonth = result.getString("yearmonth");
					info += " " + yearmonth + " " + usage;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "fail";
			}
		}
		else if(option.equals("e")){
			info="e " + cust_id;
			sql = "select e_usage, yearmonth from Electricity where cust_id = '"+ cust_id + "';";
			try {
				pre = conn.prepareStatement(sql);
				ResultSet result = pre.executeQuery();
				while (result.next()){
					usage = result.getString("e_usage");
					yearmonth = result.getString("yearmonth");
					info += " " + yearmonth + " " + usage;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(option.equals("all")){

		}
		else return "fail";

		return info;

	}


	public String getAll(String cust_id) {
		// TODO Auto-generated method stub
		String w_sql = "select w_usage from water where cust_id = '" + cust_id + "' and yearmonth = '201611';";
		String g_sql = "select g_usage from gas where cust_id = '" + cust_id + "' and yearmonth = '201611';";
		String e_sql = "select e_usage from electricity where cust_id = '" + cust_id + "' and yearmonth = '201611';";
		PreparedStatement pre;
		String info=null;
		try {
			pre = conn.prepareStatement(w_sql);
			ResultSet result = pre.executeQuery();
			while (result.next()){
				info = result.getString("w_usage");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pre = conn.prepareStatement(g_sql);
			ResultSet result = pre.executeQuery();
			while (result.next()){
				info += " " + result.getString("g_usage");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pre = conn.prepareStatement(e_sql);
			ResultSet result = pre.executeQuery();
			while (result.next()){
				info += " " + result.getString("e_usage");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return info;
	}
}

/*
public String getWater(String cust_id) { //Water!!!!!
	// TODO Auto-generated method stub
	String sql = "select w_usage, yearmonth from Water where cust_id = '"+ cust_id + "';";
	PreparedStatement pre;
	String w_usage, yearmonth;
	String w_info="w " + cust_id; // cust_id usage ym usage ym usage ym.....
	try {
		pre = conn.prepareStatement(sql);
		ResultSet result = pre.executeQuery();
		while (result.next()){
			w_usage = result.getString("w_usage");
			yearmonth = result.getString("yearmonth");
			w_info += " " + yearmonth+" " + w_usage;
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return w_info;


}


public String getGas(String cust_id) { //GAS!!!!!!
	// TODO Auto-generated method stub
	String sql = "select g_usage, yearmonth from GAS where cust_id = '"+ cust_id + "';";
	PreparedStatement pre;
	String g_usage, yearmonth;
	String g_info="g " + cust_id; // cust_id usage ym usage ym usage ym.....
	try {
		pre = conn.prepareStatement(sql);
		ResultSet result = pre.executeQuery();
		while (result.next()){
			g_usage = result.getString("g_usage");
			yearmonth = result.getString("yearmonth");
			g_info += " " + yearmonth +" " + g_usage;
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return g_info;

}

public String getElec(String cust_id) { //Elec!!!!!
	// TODO Auto-generated method stub
	String sql = "select e_usage, yearmonth from Electricity where cust_id = '"+ cust_id + "';";
	PreparedStatement pre;
	String e_usage, yearmonth;
	String e_info="e " + cust_id; // cust_id usage ym usage ym usage ym.....
	try {
		pre = conn.prepareStatement(sql);
		ResultSet result = pre.executeQuery();
		while (result.next()){
			e_usage = result.getString("e_usage");
			yearmonth = result.getString("yearmonth");
			e_info += " " + yearmonth + " " + e_usage;
		}
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return e_info;
}
*/