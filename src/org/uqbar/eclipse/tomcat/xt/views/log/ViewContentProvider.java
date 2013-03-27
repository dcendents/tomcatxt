/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.views.log;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;

/**
 * 
 * @author jfernandes
 */
class ViewContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object parent) {
		if( TomcatLauncherPlugin.isTomcatConfigured()) {
			return getLogsDir().listFiles();
		}
		return EMPTY_ARRAY; 
	}

	public static final Object[] EMPTY_ARRAY = new Object[0];
	
	public static File getLogsDir() {
		return new File((TomcatLauncherPlugin.getDefault().getTomcatDir() + File.separator + "logs"));
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
}