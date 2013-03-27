/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.xml;

/**
 * @author jfernandes
 */
public class Loader {
	private String className;
	private boolean reloadable;
	private int debug;
	private boolean useSystemClassLoaderAsParent;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public int getDebug() {
		return debug;
	}

	public void setDebug(int debug) {
		this.debug = debug;
	}

	public boolean isUseSystemClassLoaderAsParent() {
		return useSystemClassLoaderAsParent;
	}

	public void setUseSystemClassLoaderAsParent(boolean useSystemClassLoaderAsParent) {
		this.useSystemClassLoaderAsParent = useSystemClassLoaderAsParent;
	}

}
