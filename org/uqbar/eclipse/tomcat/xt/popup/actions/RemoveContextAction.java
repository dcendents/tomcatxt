package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * 
 * @author jfernandes
 */
public class RemoveContextAction extends AbstractTomcatContextAction {

	protected void run(TomcatContext context) {
		context.remove();
		UqbarSydeoXtActivator.getDefault().getTomcat().refreshContexts();
	}

	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return context.canRemove();
	}

}
