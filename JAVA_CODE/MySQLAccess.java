/*
 * Class for sql connection from clc of cloud to server as a route form user to machine 
 * And machine to user
 * 
 */
package mysqlprogram;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class MySQLAccess {
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;
 
  public int EditDataBase(String Status,int task_id) throws Exception {
	    try {
	    	Class.forName("com.mysql.jdbc.Driver");
	        connect = DriverManager.getConnection("jdbc:mysql://localhost/test?"
	                + "user=root&password=root123");
            // Statements allow to issue SQL queries to the database
	        statement = connect.createStatement();
	        statement.execute("UPDATE HADOOP_DB.HADOOP_TASK SET STATUS= \""+Status+"\" WHERE TASK_ID=" + task_id);    	
	    	return 1;
	    }
	    catch (Exception ex1)
	    {
	    	System.out.print(ex1.toString());
	    	return 0;
	    }
	    
	    }
  
  public int insert_master(String master,int task_id, int user_id) throws Exception {
	    try {
	    	Class.forName("com.mysql.jdbc.Driver");
	        connect = DriverManager.getConnection("jdbc:mysql://localhost/HADOOP_DB?"
	                + "user=root&password=root123");
          // Statements allow to issue SQL queries to the database
	        statement = connect.createStatement();
	        statement.execute("INSERT INTO HADOOP_DB.HADOOP_TASKS(TASK_ID,USER_ID,MASTER) VALUES("+String.valueOf(task_id)+"," + String.valueOf(user_id)+ ","+ master+");");
	    	return 1;
	    }
	    catch (Exception ex1)
	    {
	    	System.out.print(ex1.toString());
	    	return 0;
	    }
	    
	    }
  
  

  public String readDataBase(String user) throws Exception {
    try {
    String result;
      //load the MySQL driver
      Class.forName("com.mysql.jdbc.Driver");
      // Setup the connection with the DB
      connect = DriverManager.getConnection("jdbc:mysql://localhost/test?"
              + "user=root&password=root123");

      // Statements allow to issue SQL queries to the database
      statement = connect.createStatement();
      // Result set get the result of the SQL query
      resultSet = statement
          .executeQuery("select * from HADOOP_DB.HADOOP_USERS");
      System.out.println(user);
      result= writeResultSet(resultSet,user);
      //System.out.println(result);
      return result;
    } catch (Exception e) {
      throw e;
    } finally {
      close();
    }

  }
/*
  private void writeMetaData(ResultSet resultSet) throws SQLException {
    //   Now get some metadata from the database
    // Result set get the result of the SQL query
    
    System.out.println("The columns in the table are: ");
    
    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
    for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
      System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
    }
  }
*/
  private String writeResultSet(ResultSet resultSet, String user) throws SQLException {
    // ResultSet is initially before the first data set
    while (resultSet.next()) {
      // It is possible to get the columns via name
      // also possible to get the columns via the column number
      // which starts at 1
      // e.g. resultSet.getSTring(2);
      String id = resultSet.getString("USER_ID");
      String name = resultSet.getString("USER_NAME");
      String ip = resultSet.getString("MASTER_IP");
      System.out.println(user);
      if(name.equals(user))
      { 
    	  System.out.println("HO");
    	   return ip;
      
      }
    }
	return null;
  }

  // You need to close the resultSet
  private void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (connect != null) {
        connect.close();
      }
    } catch (Exception e) {

    }
}

}