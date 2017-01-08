package mysqlprogram;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Data_client {
	public static void main(String[] args) {
		try {
			 int req = 1;
			new Data_client().startClient(req);
		} catch (Exception e) {
			System.out.println("Something falied: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public void startClient(int req) throws IOException {
		 
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		InetAddress host = null;
		BufferedReader stdIn = null;
 
		try {
			host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 5559);
			
 
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			out.println("MASTER");
			String fromUser = null;
			switch(req)
			{
			case 1: // STATUS IS STARTED
				fromUser = "STARTED";
				out.println(fromUser);
				fromUser = "1";
				out.println(fromUser);
				break;
			case 2: // STATUS IS MACHINE_CONF STARTED
				break;
			case 3: // STATUS IS HADOOP_JOB STARTED
				break;
			case 4: // STATUS IS HADOOP_JOB RUNNING
				break;
			case 5: // STATUS IS HADOOP_JOB RUNNING
				break;
			
			}
			out.println("MASTER_END");
			out.println("null");
			

 } catch (Exception Ex)
 {
	 System.out.print(Ex.toString());
 }
		finally { //Make sure we always clean up
			out.close();
			in.close();
			stdIn.close();
			socket.close();
		}
}
}