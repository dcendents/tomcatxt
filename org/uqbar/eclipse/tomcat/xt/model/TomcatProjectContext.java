/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.sysdeo.eclipse.tomcat.TomcatProject;

/**
 * @deprecated Now that the view just gets all the contexts from the tomcat
 *             config it's no longer needed this. However, It would be great if
 *             we have some integration between a {@link TomcatContext} that is
 *             correlated to a {@link TomcatProject}.
 * 
 * @author jfernandes
 */
public class TomcatProjectContext extends TomcatContext {
	private final IProject project;

	public TomcatProjectContext(IProject project) {
		this.project = project;
	}

	private TomcatProject getTomcatProject() {
		try {
			return (TomcatProject) this.project.getNature(TomcatLauncherPlugin.NATURE_ID);
		}
		catch (CoreException e) {
			throw new RuntimeException("Error while accesing tomcat nature from project", e);
		}
	}

	// IWorkbenchAdapter methods

	public String getLabel(Object o) {
		return this.project.getName() + " (" + (String) this.getContextPath() + ")";
	}

	@Override
	public String getContextPath() {
		return (String) UqbarSydeoXtActivator.forcedInvoke(this.getTomcatProject(), "getContextPath");
	}
	
	@Override
	public String getDocBase() {
	if(this.getTomcatProject().getRootDirFolder() == null) {
	    return project.getLocation().toOSString();
	} 
	else {
	    return this.getTomcatProject().getRootDirFolder().getLocation().toOSString();
	}
	}

	@Override
	public void remove() {
		try {
			this.getTomcatProject().removeContext();
		}
		catch (CoreException e) {
			UqbarSydeoXtActivator.logError("Could remove tomcat project context '" + this.project.getName() + "'");
		}
		catch (IOException e) {
			UqbarSydeoXtActivator.logError("Could remove tomcat project context '" + this.project.getName() + "'");
		}
	}
	
	@Override
	public String getSourceDescription() {
		return "workspace";
	}

	@Override
	public void disable() {
	}

	@Override
	public void enable() {
	}

	@Override
	public boolean isDisable() {
		return false;
	}
	
	@Override
	public void cleanWorkDir() {
	}

}
