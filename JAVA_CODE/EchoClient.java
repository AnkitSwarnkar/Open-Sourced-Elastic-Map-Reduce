package mysqlprogram;
import java.io.*;
import java.net.*;
import mysqlprogram.testobject;
 
public class EchoClient {
	public static void main(String[] args) {
		try {
			new EchoClient().startClient();
		} catch (Exception e) {
			System.out.println("Something falied: " + e.getMessage());
			e.printStackTrace();
		}
	}
 
	public void startClient() throws IOException {
 
		Socket socket = null;
		InetAddress host = null;
		OutputStream os = null;
		testobject to =null;
		ObjectOutputStream oos =null;
 
		try {
			host = InetAddress.getLocalHost();
			socket = new Socket(host.getHostName(), 5559);
			os = socket.getOutputStream();  
			oos = new ObjectOutputStream(os);  
			to = new testobject(2,"object from client",1); 
			oos.writeObject(to);  
			
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