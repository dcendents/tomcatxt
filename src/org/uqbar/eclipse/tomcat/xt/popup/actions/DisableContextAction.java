package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * 
 * @author ccancinos
 */
public class DisableContextAction extends AbstractTomcatContextAction {

	@Override
	protected void run(TomcatContext context) {
		context.disable();
		UqbarSydeoXtActivator.getDefault().getTomcat().refreshContexts();
	}
	
	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return context.canDisable();
	}

}
