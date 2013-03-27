/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.xml;

/**
 * @author jfernandes
 */
public class Context {
	private String path;
	private boolean reloadable;
	private String docBase;
	private String workDir;
	private Logger logger;
	private Loader loader;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public String getDocBase() {
		return docBase;
	}

	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}

	public String getWorkDir() {
		return workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Loader getLoader() {
		return loader;
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " path:'" + this.getPath() + "' docBase:'" + this.getDocBase()
				+ "' workDir:'" + this.getWorkDir() + "' reloadable:'" + this.isReloadable() + "'";
	}

}