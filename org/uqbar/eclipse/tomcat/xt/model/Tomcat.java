package org.uqbar.eclipse.tomcat.xt.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.loader.TomcatContextReader;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;

/**
 * Model class for the whole plugin (ui) It will hide all implementation details
 * and expose a behaviorally complete interface for the plugin functions.
 * 
 * 
 * @author jfernandes
 */
// Unify eclipse interfaces impl with TomcatContext
public class Tomcat implements IWorkbenchAdapter, IAdaptable, IActionFilter, IPropertyChangeListener {
	private static final int MAX_CHECK_STATUS = 10;
	private static final int CHECK_STATUS_SCHEDULE_DELAY = 2000;  
	
	private List<TomcatContext> contexts;
	private TomcatContextReader reader;
	private List<TomcatObserver> observers = new ArrayList<TomcatObserver>();
	private TomcatConfig config;
	private Boolean running;
	private ManagerUser manager;

	public Tomcat(TomcatContextReader reader) {
		this.reader = reader;
		TomcatLauncherPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	public TomcatConfig getConfig() {
		if (this.config == null) {
			this.config = this.reader.loadConfig(this);
		}
		return this.config;
	}
	
	public String getTomcatHome() {
		return TomcatLauncherPlugin.getDefault().getPreferenceStore().getString("tomcatDir");
	}

	public List<TomcatContext> getContexts() {
		if (this.contexts == null) {
			this.contexts = this.reader.readContexts(this);
		}
		return this.contexts;
	}

	public void refreshContexts() {
		this.contexts = null;
		this.running = null;
		this.getManager().refresh();
		for (TomcatObserver observer : this.observers) {
			observer.tomcatChanged();
		}
	}

	public void addObserver(TomcatObserver observer) {
		this.observers.add(observer);
	}
	
	public void removeObserver(TomcatObserver observer) {
		this.observers.remove(observer);
	}

	public String getURL() {
		return "http://" + this.getHostPort();
	}

	public ManagerUser getManager() {
		if (this.manager == null) {
			this.manager = new ManagerUser(this);
		}
		return manager;
	}

	public String getHostPort() {
		return this.getConfig().getName() + ":" + this.getConfig().getPort();
	}

	public boolean isRunning() {
		if (this.running == null) {
			return this.running = this.fetchTomcatState();
		}
		return this.running;
	}

	private boolean fetchTomcatState() {
		// HACKERIL, HACKERIL!!! esto hace tardar bocha el menu
		HttpURLConnection connection = null;
		try {
			URL url = new URL(this.getURL());
			connection = (HttpURLConnection) url.openConnection();
			connection.getResponseCode();
			return true;
		}
		catch (MalformedURLException e) {
			return false;
		}
		catch (IOException e) {
			return false;
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private Set<TomcatRuntimeListener> runtimeListeners = new HashSet<TomcatRuntimeListener>();

	public void addRuntimeListener(TomcatRuntimeListener listener) {
		this.runtimeListeners.add(listener);
	}

	public void removeRuntimeListener(TomcatRuntimeListener listener) {
		this.runtimeListeners.remove(listener);
	}

	public void started() {
		//TODO unify with stoped()
		new Job("Checking tomcat state") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				running = null;
				for (int i = 0; i < MAX_CHECK_STATUS; i++) {
					if (isRunning()) {
						for (TomcatRuntimeListener listener : runtimeListeners) {
							listener.tomcatStarted();
						}
						break;
					}
					this.schedule(CHECK_STATUS_SCHEDULE_DELAY);
				}
				return Status.OK_STATUS;
			}
		}.schedule(CHECK_STATUS_SCHEDULE_DELAY);
	}

	public void stoped() {
		//TODO unify with started()
		new Job("Checking tomcat state") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				running = null;
				for (int i = 0; i < MAX_CHECK_STATUS; i++) {
					if (!isRunning()) {
						for (TomcatRuntimeListener listener : runtimeListeners) {
							listener.tomcatStopped();
						}
						break;
					}
					this.schedule(CHECK_STATUS_SCHEDULE_DELAY);
				}
				return Status.OK_STATUS;
			}
		}.schedule(CHECK_STATUS_SCHEDULE_DELAY);
	}

	// IWorkbenchAdapter

	public Object[] getChildren(Object o) {
		return null;
	}

	public String getLabel(Object o) {
		return null;
	}

	public Object getParent(Object o) {
		return null;
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}
		else if (adapter == IActionFilter.class) {
			return this;
		}
		return null;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return UqbarSydeoXtActivator.getImageDescriptor("/icons/tomcat.gif");
	}

	public boolean isManagerEnabled() {
		return this.getManager().isEnabled();
	}

	public boolean testAttribute(Object target, String name, String value) {
		if ("running".equals(name)) {
			return Boolean.valueOf(value).equals(((TomcatContext) target).isRunning());
		}
		return false;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(UqbarSydeoXtActivator.TOMCAT_PREF_CONFIGFILE_KEY.equals(event.getProperty())) {
			this.config = null;
			this.reader.refresh();
			this.refreshContexts();
		}
	}

}