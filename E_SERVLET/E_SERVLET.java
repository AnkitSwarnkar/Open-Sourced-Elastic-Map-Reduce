package mysqlprogram;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import mysqlprogram.testobject;



public class E_SERVLET extends HttpServlet{
  public void doGet(HttpServletRequest request, HttpServletResponse response)
                                   throws ServletException, IOException {
    String classname = request.getParameter("classname");
    String jarname = request.getParameter("jarname");
    String no_of_machine = request.getParameter("no_of_machine");
    Socket socket = null;
    InetAddress host = null;
    OutputStream os = null;
    ObjectOutputStream oos =null;
    testobject to =null;
    try {
                        host = InetAddress.getLocalHost();
                        socket = new Socket(host.getHostName(), 5559);
                        os = socket.getOutputStream();
                        oos = new ObjectOutputStream(os);
                        to = new testobject(2);
                        to.CLASS_NAME = classname;
 			to.JAR_NAME = jarname;
			to.No_of_machine=no_of_machine;
                        to.TASK_ID = "1";
                        to.USER_ID = "1";
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

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<body>");
    out.println("CLASSNAME" + "  " + classname + " "+jarname+" " + "<br>" );
    out.println("YOU required " +no_of_machine + "<br>");
    out.println("</body></html>");
  }
}
