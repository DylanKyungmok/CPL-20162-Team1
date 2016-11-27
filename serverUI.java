package ocsf;

import java.io.IOException;
import java.util.StringTokenizer;

import DB.*;

public class serverUI extends AbstractServer{
	static int login_cnt = 0;
	int cnt = 0;
	String cust_id = null;

	private static serverUI sv;

	public serverUI(int port) {
		super(port);
	}
	@Override
	protected void clientConnected(ConnectionToClient client){
		if(login_cnt == 0){
			System.out.println("RaspberryPi connected");
			login_cnt++;
		}
		else{
			System.out.println("Mobile connected");
		}

	}
	@Override
	protected void handleMessageFromClient(String msg, ConnectionToClient client) {//해당 클라이언트로부터 어떤 메시지 받아서 다시 전
		String index = null;
		String meter_id = null;
		String usage = null;
		String cust_password = null;
		

		if(msg != null)
			System.out.println("Server Received : " + msg);
		
		String id=null;
		StringTokenizer st = new StringTokenizer(msg, " ");//토큰 자르기 
		index = st.nextToken();

		if(index.equals("login")){
			//로그인 기능 
			String login_check = null;
			cust_id = st.nextToken();
			cust_password = st.nextToken();
			login_check = DBmanager.getInstace().LoginCheck(cust_id, cust_password);
			//System.out.println(login_check);
			System.out.println("-------------Sent to client---------------");
			sv.sendToAllClients(login_check);
//			try {
//				client.sendToClient(login_check);
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			};
		}
		else if(index.equals("w")){
			
			//수도 검침 
			cust_id = st.nextToken();
			//cust_id = "id0001";
			String w_info;
			//w_info = DBmanager.getInstace().getWater(cust_id);
			w_info = DBmanager.getInstace().getFee(cust_id, "w");
			System.out.println(w_info);//w id0001 201611 05221859 201610 763 
			sv.sendToAllClients(w_info);
			System.out.println("-------------Sent to client---------------");
		}
		else if(index.equals("g")){
			//가스 검침 
			cust_id = st.nextToken();
			//cust_id = "id0001";
			String g_info;
			g_info = DBmanager.getInstace().getFee(cust_id,"g");
			System.out.println(g_info);	//g id0001 201611 05221859 201610 763 

			sv.sendToAllClients(g_info);
			System.out.println("-------------Sent to client---------------");

		}
		else if(index.equals("e")){
			
			//전기 검침 
			cust_id = st.nextToken();
			//cust_id = "id0001";
			sv.sendToAllClients("take");
			cnt = 0;
		}
		else if(index.equals("all")){
			cust_id = st.nextToken();
			String info = "all " + cust_id;
			info += " " +  DBmanager.getInstace().getAll(cust_id);
			System.out.println(info);//
			sv.sendToAllClients(info);
			System.out.println("-------------Sent to client---------------");
			
			
		}
		else if(index.equals("2")){
			 DBmanager.getInstace().processNum(2);
		}
		else if(index.equals("1")){
			//검침 후 다시 서버로 날리기 
			System.out.println("-----------Check Usage-------------");
			meter_id = st.nextToken();
			usage = st.nextToken();
			String cust_id;
			cnt=1;
			//System.out.println(usage);
			DBmanager.getInstace().saveElec(meter_id, usage);
			//String sql = "update electricity set e_usage = '"+ usage + "' where elec_meter = '" + meter_id +"' and yearmonth = '201611'";
			//DBmanager.getInstace().getcust_id(meter_id, "e");

			if(cnt == 1 ){
				String e_info = null;
				cust_id = DBmanager.getInstace().getCustId(meter_id, "e");
				
				//e_info = DBmanager.getInstace().getElec("id0001");
				e_info = DBmanager.getInstace().getFee(cust_id, "e");
				System.out.println( e_info);//e id0001 201611 05221859 201610 763 
				sv.sendToAllClients(e_info);
				System.out.println("-------------Sent to client---------------");
			}
		}
		else{
			sv.sendToAllClients("fail");
			System.out.println("-------------Wrong Request---------------");
		}


	}


	public static void main(String [] args) throws IOException
	{
		sv = new serverUI(6666);
		System.out.println("Server Started!");
		sv.listen();
	}
}
