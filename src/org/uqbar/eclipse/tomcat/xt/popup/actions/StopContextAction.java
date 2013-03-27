/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * @author jfernandes
 */
public class StopContextAction extends AbstractTomcatContextAction {

	@Override
	protected void run(TomcatContext context) {
		context.stop();
	}

	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return UqbarSydeoXtActivator.getDefault().getTomcat().getManager().canStop(context) && !context.isDisable();
	}

}
