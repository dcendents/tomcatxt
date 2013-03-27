package org.uqbar.eclipse.tomcat.xt.runtime.jmx;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/*import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Engine;*/

/**
 * Super simplistic demo for JMX Cluster members query on Tomcat 5.5
 * 
 * @author Edmon B.
 */
public class JMXClient {

	/**
	 */
	public JMXClient() {
		super();
	}

	/**
	 * Demo method
	 * 
	 * @param args
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws Exception {

		String urlForJMX = "service:jmx:rmi:///jndi/rmi://localhost:8888/jmxrmi";

		MBeanServerConnection jmxServerConnection = JMXConnectorFactory.connect(new JMXServiceURL(urlForJMX), null).getMBeanServerConnection();

		ObjectName engineName = new ObjectName("Catalina:type=Engine");
		
		/*Engine engine = (Engine) UqbarMBeanServerInvocationHandler.newProxyInstance(jmxServerConnection, engineName, Engine.class, false);
		System.out.println("engine name = " + engine.getName());
		engine.addContainerListener(new UqbarContainerListener());*/
		Thread.sleep(20000);
	}

}