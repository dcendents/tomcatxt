/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * 
 * @author jfernandes
 */
public class ReloadContextAction extends AbstractTomcatContextAction {

	@Override
	protected void run(TomcatContext context) {
		context.reload();
	}
	
	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return context.isRunning() && !context.isDisable();
	}
	
}
