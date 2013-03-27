/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * @author jfernandes
 */
public class StartContextAction extends AbstractTomcatContextAction {

	@Override
	protected void run(TomcatContext context) {
		context.start();
	}

	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return UqbarSydeoXtActivator.getDefault().getTomcat().getManager().canStart(context);
	}

}
