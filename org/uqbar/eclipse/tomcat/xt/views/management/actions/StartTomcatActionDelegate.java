package org.uqbar.eclipse.tomcat.xt.views.management.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.Tomcat;

import com.sysdeo.eclipse.tomcat.actions.StartActionDelegate;

/**
 * @author jfernandes
 */
public class StartTomcatActionDelegate extends StartActionDelegate implements IViewActionDelegate {

	public void init(IViewPart view) {
	}

	private Tomcat getTomcat() {
		return UqbarSydeoXtActivator.getDefault().getTomcat();
	}

	@Override
	public void run(IAction action) {
		super.run(action);
		getTomcat().started();
	}
	
}
