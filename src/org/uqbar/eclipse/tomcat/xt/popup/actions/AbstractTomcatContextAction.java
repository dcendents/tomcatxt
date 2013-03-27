package org.uqbar.eclipse.tomcat.xt.popup.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.PluginAction;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * @author jfernandes
 */
public abstract class AbstractTomcatContextAction implements IObjectActionDelegate {

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		for (TomcatContext context : this.getSelectedContext((PluginAction) action)) {
			this.run(context);
		}
	}

	protected abstract void run(TomcatContext context);

	protected List<TomcatContext> getSelectedContext(PluginAction action) {
		return ((IStructuredSelection) action.getSelection()).toList();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// REFACTORME: store selection
		for (TomcatContext context : getSelectedContext((PluginAction) action)) {
			if (!this.isEnabledFor(context)) {
				action.setEnabled(false);
				return;
			}
		}
		;
		action.setEnabled(true);
	}

	protected boolean isEnabledFor(TomcatContext context) {
		return true;
	}

}