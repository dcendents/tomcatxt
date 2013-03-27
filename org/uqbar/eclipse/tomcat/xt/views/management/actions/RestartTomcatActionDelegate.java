package org.uqbar.eclipse.tomcat.xt.views.management.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

import com.sysdeo.eclipse.tomcat.actions.RestartActionDelegate;

/**
 * @author jfernandes
 */
public class RestartTomcatActionDelegate extends RestartActionDelegate implements IViewActionDelegate {

	public void init(IViewPart view) {
	}

	@Override
	public void run(IAction action) {
		super.run(action);
		UqbarSydeoXtActivator.getDefault().getTomcat().started();
	}

}
