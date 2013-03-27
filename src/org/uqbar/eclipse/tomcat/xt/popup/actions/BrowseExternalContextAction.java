/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author jfernandes
 * 
 */
public class BrowseExternalContextAction extends BrowseContextAction {

	@Override
	protected int getBrowserStyle() {
		return IWorkbenchBrowserSupport.AS_EXTERNAL;
	}

}
