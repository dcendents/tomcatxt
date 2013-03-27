/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * @author jfernandes
 */
public class ManagerUser {
	public static final String OPERATION_START = "start";
	public static final String OPERATION_STOP = "stop";
	public static final String OPERATION_RELOAD = "reload";
	static final String OPERATION_UNDEPLOY = "undeploy";
	private final Tomcat tomcat;
	private Set<String> runningContexts;

	public ManagerUser(Tomcat tomcat) {
		this.tomcat = tomcat;
	}

	public String getUser() {
		return TomcatLauncherPlugin.getDefault().getManagerAppUser();
	}

	public String getPasswd() {
		return TomcatLauncherPlugin.getDefault().getManagerAppPassword();
	}

	public boolean canStart(TomcatContext tomcatContext) {
		return this.isEnabled() && !this.isRunning(tomcatContext);
	}

	public boolean isEnabled() {
		//TODO: it should be enabled only if can loggon to the manager, not if the user is defined
		return this.tomcat.isRunning() && !UqbarSydeoXtActivator.noe(this.getUser());
	}

	public void executeOperation(String operation, TomcatContext context) {
		this.executeOperation(operation + "?path=" + context.getContextPath());
	}

	public String executeOperation(String operation) {
		HttpURLConnection connection = null;
		BufferedReader in = null;
		try {
			URL url = new URL(tomcat.getURL() + "/manager/" + operation);
			String userPassword = this.getUser() + ":" + this.getPasswd();
			String encoding = new Base64Encoder().encode(userPassword.getBytes());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + encoding);

			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder inputLine = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				inputLine.append(line + "\n");
			}
			inputLine.deleteCharAt(inputLine.length() - 1);

			String response = inputLine.toString();
			if (!response.startsWith("OK")) {
				// TODO: throw exception and manage error dialog at ui invokers
				UqbarSydeoXtActivator.showError("Operation Failed", "Server Response: \n" + response);
			}

			return response;
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Couldn't execute operation", e);
		}
		catch (IOException e) {
			throw new RuntimeException("Couldn't execute operation", e);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					UqbarSydeoXtActivator.logWarning("Couldn't close http connection stream");
					e.printStackTrace();
				}
			}
		}
	}

	public boolean canStop(TomcatContext context) {
		return this.tomcat.isRunning() && !this.canStart(context);
	}

	private boolean isRunning(TomcatContext tomcatContext) {
		return this.getContextsState().contains(tomcatContext.getContextPath());
	}

	private Set<String> getContextsState() {
		if (this.runningContexts == null) {
			this.runningContexts = new HashSet<String>();
			String listResponse = this.executeOperation("list");

			for (String contexts : listResponse.substring(listResponse.indexOf('\n') + 1).split("\\n")) {
				String[] data = contexts.split(":");
				if (isRunning(data[1])) {
					this.runningContexts.add(data[0]);
				}
			}
		}
		return this.runningContexts;
	}

	private static boolean isRunning(String string) {
		return "running".equals(String.valueOf(string));
	}

	public void refresh() {
		this.runningContexts = null;
	}
}
