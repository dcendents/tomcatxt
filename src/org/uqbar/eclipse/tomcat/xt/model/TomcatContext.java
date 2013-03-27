package org.uqbar.eclipse.tomcat.xt.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

/**
 * 
 * @author jfernandes
 */
public abstract class TomcatContext implements IWorkbenchAdapter, IAdaptable, IActionFilter {

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}
		if (adapter == IActionFilter.class) {
			return this;
		}
		return null;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return UqbarSydeoXtActivator.getImageDescriptor("/icons/context.png");
	}

	public Object[] getChildren(Object o) {
		return null;
	}

	public Object getParent(Object o) {
		return null;
	}

	/**
	 * Removes this context from the tomcat configuration
	 */
	public abstract void remove();

	public URL getURL() {
		try {
			return new URL(this.getTomcat().getURL() + this.getContextPath());
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Error while creating context URL", e);
		}
	}

	public abstract String getContextPath();

	private Tomcat getTomcat() {
		return UqbarSydeoXtActivator.getDefault().getTomcat();
	}

	public void start() {
		this.executeManagerOperation(ManagerUser.OPERATION_START);
		this.fireStarted();
	}

	public boolean canStart() {
		return this.getTomcat().getManager().canStart(this);
	}

	public void stop() {
		this.executeManagerOperation(ManagerUser.OPERATION_STOP);
		this.fireStopped();
	}

	public void reload() {
		this.executeManagerOperation(ManagerUser.OPERATION_RELOAD);
	}

	public void redeploy() {
		this.executeManagerOperation(ManagerUser.OPERATION_UNDEPLOY);
	}

	private void executeManagerOperation(String operation) {
		this.getTomcat().getManager().executeOperation(operation, this);
	}

	public boolean canRemove() {
		return true;
	}

	public boolean isRunning() {
		return this.getTomcat().isRunning() && !this.canStart();
	}

	private Set<TomcatContextListener> runtimeListeners = new HashSet<TomcatContextListener>();

	public void addRuntimeListener(TomcatContextListener listener) {
		this.runtimeListeners.add(listener);
	}

	public void removeRuntimeListener(TomcatContextListener listener) {
		this.runtimeListeners.remove(listener);
	}

	protected void fireStarted() {
		for (TomcatContextListener listener : this.runtimeListeners) {
			listener.contextStarted(this);
		}
	}

	protected void fireStopped() {
		for (TomcatContextListener listener : this.runtimeListeners) {
			listener.contextStopped(this);
		}
	}

    public abstract String getDocBase();

    public boolean testAttribute(Object target, String name, String value) {
	if("running".equals(name)) {
	    return Boolean.valueOf(value).equals(((TomcatContext)target).isRunning());
	}
        return false;
    }

    /**
     * Returns a name describing where this context is located (server.xml | contextfile)
     * @return
     */
	public abstract String getSourceDescription();

	
	public abstract void disable();
	public abstract boolean isDisable();
	public abstract void enable();
	public abstract void cleanWorkDir();
	
	public boolean canDisable() {
		return !this.isDisable();
	}

}