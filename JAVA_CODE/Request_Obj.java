package mysqlprogram;
import java.io.Serializable;

class testobject implements Serializable {  
private static final long serialVersionUID = 1L;
int req_type ;  
String status;  
int task_id;
int user_id;
/* 
 * User Information.
 */
String No_of_machine= null;
String TASK_ID= null;
String USER_ID=null;
String JAR_NAME = null;
String CLASS_NAME = null;
String master = null;
public testobject(int v)
{
	this.req_type=v;
}

public testobject(int v,int id)
{
	this.req_type=v;   
	this.task_id= id;
	}
public  testobject(int v, String s,int id ){  
this.req_type=v;  
this.status=s;  
this.task_id= id;
  }  
} 
