/*
 * Things it do ::
 * 1) Update the user credentials in databag for particular tsk_id 
 * Things to be add:
 * Add the part of cloud machines
 * 
 * 
 */
package mysqlprogram;
import mysqlprogram.testobject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
public class User_Update  {
	
	     String update(testobject req) throws Exception {
		 File wd = new File("/root/chef-repo/");
		 Process proc = null;
		 // Change the code to prompting for input
		 String user_id = req.USER_ID;
		 String No_of_machine = req.No_of_machine;
		 //Remove the ip1 n ip2
		 
		 String ip1 = "11.11.3.190";
		 String ip2 = "11.11.3.223";
		 String TASK_ID = req.TASK_ID;
		 String JAR_NAME= req.JAR_NAME;
		 String CLASS_NAME= req.CLASS_NAME;
         
		 int num_machine = Integer.parseInt(No_of_machine);
		 int uid =  Integer.parseInt(user_id);
		 int tid =Integer.parseInt(TASK_ID);
		 List<String> IP_address = new ArrayList<String>();
		 /*
		  * INSERT THE CODE FOR Getting the ips and add it into the list. currently
		  * am adding static ips 
		  */
		 IP_address.add(ip1);
		 IP_address.add(ip2);
		 /*
		  * Assign the last Ip to the master
		  */
		 String master = IP_address.get(num_machine-1);
		 req.master=master;
		 
		 try {
			   proc = Runtime.getRuntime().exec("/bin/bash", null, wd);
			}
			catch (IOException e) {
			   e.printStackTrace();
			}
			
			/*
			 * This code will update the chef databag
			 */
			
			if (proc != null) {
			   BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			   PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
			   ///////////////CODE TO make and edit the user_id.json
			  try{ 
			//   out.println("echo \"Y\" |knife data bag delete USER");   
			  // out.println("knife data bag create USER");
			   	   
			   String file_name = "/root/chef-repo/data_bags/USER/"+ TASK_ID+".json";
			   PrintWriter writer = new PrintWriter(file_name);
			   writer.println("{\n\"id\" : "+ "\""+TASK_ID + "\",");
			   writer.println("\"No_of_Machine\" : "+"\""+No_of_machine + "\"," );
			   writer.println("\"master\" : "+"\""+master + "\"," );
			   
			   //Code to enter the IP in the list
			   
				for(int i=0;i<IP_address.size()-1;i++)   
				  {
					writer.println("\"ip"+ Integer.toString(i+1) + "\" : "+"\""+IP_address.get(i) + "\"," );
				  } 
				
			   
			   writer.println("\"USER_ID\" : "+"\""+user_id + "\"," );
			   writer.println("\"JAR_NAME\" : "+"\""+JAR_NAME + "\"," );
			   writer.println("\"CLASS_NAME\" : "+"\""+CLASS_NAME + "\"\n }" );
			   writer.close();
			   out.println("knife data bag from file USER "+ user_id+".json" ); 
			   //out.println("knife client list");
			   out.println("exit");
			  
			      String line;
			      while ((line = in.readLine()) != null) {
			         System.out.println(line);
			      }
			      proc.waitFor();
			      in.close();
			      out.close();
			      proc.destroy();
			   }
			  
			   catch (Exception e) {
			      e.printStackTrace();
			   }
			}
			return master;
        }
}
