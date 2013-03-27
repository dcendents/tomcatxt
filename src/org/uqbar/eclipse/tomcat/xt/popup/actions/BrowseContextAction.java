/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.Workbench;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * @author jfernandes
 */
public class BrowseContextAction extends AbstractTomcatContextAction {
	public static final int DEFAULT_STYLE = IWorkbenchBrowserSupport.LOCATION_BAR
			| IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.STATUS
			| IWorkbenchBrowserSupport.AS_EDITOR;

	@Override
	protected void run(TomcatContext context) {
		openBrowser(context, this.getBrowserStyle());
	}

	protected int getBrowserStyle() {
		return DEFAULT_STYLE;
	}

	public static void openBrowser(TomcatContext context) {
		openBrowser(context, DEFAULT_STYLE);
	}

	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return context.isRunning() && !context.isDisable();
	}

	public static void openBrowser(TomcatContext context, int style) {
		try {
			Workbench.getInstance().getBrowserSupport().createBrowser(style, context.getContextPath(), "", "").openURL(
					context.getURL());
		}
		catch (PartInitException e) {
			throw new RuntimeException("Couldn't open browser for context", e);
		}
	}

}
