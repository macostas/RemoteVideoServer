package remoteVideoServer;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.*;

public class main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Tomcat tomcat = new Tomcat();
			tomcat.setPort(4455);
			
			File base = new File(System.getProperty("java.io.tmpdir"));
			Context rootCtx = tomcat.addContext("", base.getAbsolutePath());
			
			Tomcat.addServlet(rootCtx, "videoRemote", new videoServer());
			rootCtx.addServletMapping("/videoRemote", "videoRemote");
			tomcat.start();
			tomcat.getServer().await();
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
