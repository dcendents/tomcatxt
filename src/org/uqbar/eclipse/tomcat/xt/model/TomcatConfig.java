/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

/**
 * @author jfernandes
 */
public class TomcatConfig {
	private int port;
	private String name;

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPort() {
		return this.port;
	}

}
