package org.uqbar.eclipse.tomcat.xt;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import com.sysdeo.eclipse.tomcat.FileUtil;
import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.uqbar.eclipse.tomcat.xt.loader.TomcatContextReaderImpl;
import org.uqbar.eclipse.tomcat.xt.model.Tomcat;
import org.uqbar.eclipse.tomcat.xt.xml.Context;
import org.uqbar.eclipse.tomcat.xt.xml.Loader;
import org.uqbar.eclipse.tomcat.xt.xml.Logger;
import org.uqbar.eclipse.tomcat.xt.xml.ResourceLink;

/**
 * The activator class controls the plug-in life cycle
 */
public class UqbarSydeoXtActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.uqbar.eclipse.tomcat.xt";
	public static final String TOMCAT_HOME_PROPERTY = "tomcatDir";
	public static final String TOMCAT_PREF_CONFIGFILE_KEY = "contextsDir";

	private static UqbarSydeoXtActivator plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
	public static UqbarSydeoXtActivator getDefault() {
		return plugin;
	}

	    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private Tomcat tomcat;

	private XStream xstream;

	public Tomcat getTomcat() {
		if (tomcat == null) {
			this.tomcat = new Tomcat(new TomcatContextReaderImpl());
		}
		return this.tomcat;
	}

	private static final Object[] ARGS = new Object[] {};
	private static final Class[] PARAMETER_TYPES = new Class[] {};

	public static Object forcedInvoke(Object object, String methodName) {
		Method method = null;
		boolean accesible = true;
		try {
			method = object.getClass().getDeclaredMethod(methodName, PARAMETER_TYPES);
			accesible = method.isAccessible();
			method.setAccessible(true);
			return method.invoke(object, ARGS);
		}
		catch (SecurityException e) {
			throw new RuntimeException("Errow while forcing method access '" + object.getClass().getSimpleName() + "."
					+ methodName + "()");
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException("Errow while forcing method access '" + object.getClass().getSimpleName() + "."
					+ methodName + "()'. It doesn't exist!!");
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeException("Errow while forcing method access '" + object.getClass().getSimpleName() + "."
					+ methodName + "()");
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException("Errow while forcing method access '" + object.getClass().getSimpleName() + "."
					+ methodName + "()");
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException("Errow while forcing method access '" + object.getClass().getSimpleName() + "."
					+ methodName + "()");
		}
		finally {
			if (method != null) {
				method.setAccessible(accesible);
			}
		}
	}

	public XStream getXStream() {
		if (this.xstream == null) {
            this.xstream = new XStream() {
                @Override
                protected MapperWrapper wrapMapper(MapperWrapper next) {
                    return new MapperWrapper(next) {
                        @Override
                        public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                            if (definedIn == Object.class) {
                                return false;
                            }
                            return super.shouldSerializeMember(definedIn, fieldName);
                        }
                    };
                }
            };
			xstream.alias("Context", Context.class);
			xstream.useAttributeFor(Context.class, "path");
			xstream.useAttributeFor(Context.class, "reloadable");
			xstream.useAttributeFor(Context.class, "docBase");
			xstream.useAttributeFor(Context.class, "workDir");

			xstream.alias("Logger", Logger.class);
			xstream.useAttributeFor(Logger.class);

			xstream.alias("Loader", Loader.class);
			xstream.useAttributeFor(Loader.class);

			xstream.alias("ResourceLink", ResourceLink.class);
			xstream.useAttributeFor(ResourceLink.class);
		}
		return this.xstream;
	}

	public static void logWarning(String message) {
		log(IStatus.WARNING, message);
	}

	public static void logError(String message) {
		log(IStatus.ERROR, message);
	}

	protected static void log(int type, String message) {
		UqbarSydeoXtActivator.getDefault().getLog().log(new Status(type, PLUGIN_ID, message));
	}

	public static void setPropertyValue(Object pom, String propertyName, Object value) {
		try {
			getPropertyDescriptor(pom.getClass(), propertyName).getWriteMethod().invoke(pom, value);
		}
		catch (IllegalArgumentException e) {
			throw new RuntimeException("Error while setting property named '" + propertyName + "' in object " + pom);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException("Error while setting property named '" + propertyName + "' in object " + pom);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException("Error while setting property named '" + propertyName + "' in object " + pom);
		}
	}

	private static PropertyDescriptor getPropertyDescriptor(Class<? extends Object> class1, String propertyName) {
		try {
			for (PropertyDescriptor property : Introspector.getBeanInfo(class1).getPropertyDescriptors()) {
				if (property.getName().equals(propertyName)) {
					return property;
				}
			}
			throw new RuntimeException("No property named '" + propertyName + "' in class " + class1.getName());
		}
		catch (IntrospectionException e) {
			throw new RuntimeException("Error while getting property named '" + propertyName + "' from class "
					+ class1.getName());
		}
	}

	public static void logInfo(String string) {
		log(IStatus.INFO, string);
	}

	public static void showError(String title, String message) {
		MessageDialog.openError(Display.getCurrent().getActiveShell(), "Tomcat - " + title, message);
	}

	public static boolean noe(String label) {
		return label == null || "".equals(label);
	}

	public static void openPreferencePage(final String tomcatPrefsID) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				openPreferencesInUIThread(tomcatPrefsID);
			}
		});
	}

	private static void openPreferencesInUIThread(String tomcatPrefsID) {
		String[] prefPageIds = new String[5];
		for (int i = 1; i < 6; i++) {
			prefPageIds[i - 1] = "com.sysdeo.eclipse.tomcat.Page" + String.valueOf(i);
		}
		PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), tomcatPrefsID, prefPageIds,
				null).open();
		UqbarSydeoXtActivator.getDefault().getTomcat().refreshContexts();
	}

	public static void openPreferencePage() {
		openPreferencePage("com.sysdeo.eclipse.tomcat.Page1");
	}

	public static void openManagerPreferencesPage() {
		openPreferencePage("com.sysdeo.eclipse.tomcat.Page4");
	}

	public static void installDevloader() {
		try {
			String tomcatVersion = TomcatLauncherPlugin.getDefault().getTomcatVersion();
			if(tomcatVersion.equals("tomcatV6") || tomcatVersion.equals("tomcatV7")) {
				FileUtil.copy(getDevLoaderJarPath(), getDevLoaderJarDestination());
			} else {
				ZipUtils.unzip(getDevLoaderZipPath(), getDevLoaderDestination());
				MessageDialog.openInformation(null, "DevLoader installation.","DevLoader was successfully installed on: \n" + getDevLoaderDestination());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error trying to resolve path for devloader zip file.", e);
		}
	}

	private static String getDevLoaderDestination() {
		return UqbarSydeoXtActivator.getDefault().getTomcat().getTomcatHome() + "/server/classes";
	}

	private static String getDevLoaderZipPath() throws IOException {
		Bundle bundle = Platform.getBundle("com.sysdeo.eclipse.tomcat");
		Path path = new Path("DevLoader.zip");
		URL fileURL = FileLocator.find(bundle, path, null);
		return FileLocator.resolve(fileURL).getPath();
	}

	private static String getDevLoaderJarPath() throws IOException {
		Bundle bundle = Platform.getBundle("com.sysdeo.eclipse.tomcat");
		Path path = new Path("DevloaderTomcat7.jar");
		URL fileURL = FileLocator.find(bundle, path, null);
		return FileLocator.resolve(fileURL).getPath();
	}

	private static String getDevLoaderJarDestination() {
		return UqbarSydeoXtActivator.getDefault().getTomcat().getTomcatHome() + "/lib/DevloaderTomcat7.jar";
	}

}