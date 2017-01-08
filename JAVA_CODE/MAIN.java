package mysqlprogram;
//import mysqlprogram.MySQLAccess;
import mysqlprogram.EchoServer;
public class MAIN {
	
	  public static void main(String[] args) throws Exception {
	  try {
              new EchoServer().startServer();
      } catch (Exception e) {
              System.out.println("I/O failure: " + e.getMessage());
              e.printStackTrace();
      }
	  
	  }


}

