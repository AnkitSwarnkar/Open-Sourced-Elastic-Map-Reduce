/*
 * Author : ANKIT SWARNKAR
 * Contains a multi-threaded socket server. Receive the request obj to distinguish 
 * type of request.
 *
 */

package mysqlprogram;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import mysqlprogram.User_Update; 

public class EchoServer extends Thread
{
	 final static int _portNumber = 5559; //Arbitrary port number
	 public void startServer() throws Exception {
		ServerSocket serverSocket = null;
		boolean listening = true;
		try {
			serverSocket = new ServerSocket(_portNumber);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + _portNumber);
			System.exit(-1);
		}
 
		while (listening) {
			handleClientRequest(serverSocket);
		}
 
		serverSocket.close();
	}
 
	private void handleClientRequest(ServerSocket serverSocket) {
		try {
			new ConnectionRequestHandler(serverSocket.accept()).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	/**
	 * Handles client connection requests. 
	 */
	public class ConnectionRequestHandler implements Runnable{
		private Socket _socket = null;
		private PrintWriter _out = null;
		private BufferedReader _in = null;
		ObjectInputStream req_obj = null;
		MySQLAccess obj = new MySQLAccess();
		public ConnectionRequestHandler(Socket socket) {
			_socket = socket;
		}
 
		public void run() {
			System.out.println("Client connected to socket: " + _socket.toString());
 
			try {
				_out = new PrintWriter(_socket.getOutputStream(), true);
				_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
				req_obj = new ObjectInputStream(_socket.getInputStream()); 
				//String inputLine, outputLine;
                //Read from socket and write back the response to client. 
				testobject Req= (testobject)req_obj.readObject();  
				switch(Req.req_type)
				{
				case 1: 
					/*
					 * 
					 */
					
					break;
				case 2: 
					/*
					 * To update the user credential and allocate the machine
					 * and update the master_ip in the database.
					 */
					User_Update user_obj = new User_Update();
                                        System.out.println(Req.CLASS_NAME);
					//String master_ip = user_obj.update(Req);
					//*****UPDATE THE HADOOP_TASKS
					//obj.insert_master(master_ip, Req.task_id, Req.user_id);
					break;
				case 3:
					/*
					 * Update the user_task database
					 * 
					 */
					obj.EditDataBase(Req.status,Req.task_id);
					// REQUEST FROM CHEF CLIENT TO UPDATE the Status 
					break;
				case 4: 
					/*
					 * Code to deallocate the machines allocated to the user. 
					 * 
					 * *
					 * */
					
					break;
					
				
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally { //In case anything goes wrong we need to close our I/O streams and sockets.
				try {
					_out.close();
					_in.close();
					_socket.close();
				} catch(Exception e) { 
					System.out.println("Couldn't close I/O streams");
				}
			}
		}
 
	}
 
	/*
	 *
	 * Handles User logic of application.
	 */
}
