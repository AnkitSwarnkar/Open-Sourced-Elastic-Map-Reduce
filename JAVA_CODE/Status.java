/*
 *Author: Ankit Swarnkar
 * Chef-client status code
 * The code compile as java Status code and will send the request to server to update the task 
 * task data base 
 * Req_id =3
 */
package mysqlprogram;
import java.io.*;
import java.net.*;
import mysqlprogram.testobject;
 
public class Status {
	public static void main(String[] args) {
		try {
			int status_code =3; //  make it as argument from command line
			int task_id= 1;
			new Status().startClient(status_code, task_id);
		} catch (Exception e) {
			System.out.println("Something falied: " + e.getMessage());
			e.printStackTrace();
		}
	}
 
	public void startClient(int status_code,int task_id ) throws IOException {
 
		Socket socket = null;
		String Status_task = null;
		InetAddress host = null;
		OutputStream os = null;
		testobject to =null, fin_obj=null;
		ObjectOutputStream oos =null;
		switch(status_code)
		{
		case 1: Status_task = "Started";
			break;
		case 2: Status_task = "Cluster_Setup";
			break;
		case 3: Status_task = "JOB_Started";
		    break;
		case 4: Status_task = "Finished";
	         break;
		case 5: Status_task = "Failed";
             break;
		}
 
		try {
			host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 5559);
			os = socket.getOutputStream();  
			oos = new ObjectOutputStream(os);  
			to = new testobject(3,Status_task,task_id); 
			
			if (status_code == 5)
			{
				fin_obj = new testobject(4,Status_task,task_id);
				oos.writeObject(fin_obj);
				
			}
			else
			{
				oos.writeObject(to); 
			}
			
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + host.getHostName());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't read/write from the connection: " + e.getMessage());
			System.exit(1);
		} finally { //Make sure we always clean up
			oos.close();  
			os.close();  
			socket.close();
		}
	}
}