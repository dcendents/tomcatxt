/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.xml;

/**
 * @author jfernandes
 */
public class Logger {
	private String className;
	private int verbosity;
	private boolean timestamp;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getVerbosity() {
		return verbosity;
	}

	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}

	public boolean isTimestamp() {
		return timestamp;
	}

	public void setTimestamp(boolean timestamp) {
		this.timestamp = timestamp;
	}

}
