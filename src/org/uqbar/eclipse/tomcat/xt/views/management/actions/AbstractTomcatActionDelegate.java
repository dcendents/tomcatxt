package org.uqbar.eclipse.tomcat.xt.views.management.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.Tomcat;
import org.uqbar.eclipse.tomcat.xt.model.TomcatRuntimeListener;

/**
 * 
 * @author jfernandes
 */
public abstract class AbstractTomcatActionDelegate implements IWorkbenchWindowActionDelegate, TomcatRuntimeListener, IViewActionDelegate {
    private IAction delegated;
    private IWorkbenchWindowActionDelegate tomcatAction;

    public AbstractTomcatActionDelegate(IWorkbenchWindowActionDelegate tomcatActionDecoratee) {
	super();
	this.tomcatAction = tomcatActionDecoratee;
    }

    protected Tomcat getTomcat() {
        return UqbarSydeoXtActivator.getDefault().getTomcat();
    }
    
    public void init(IViewPart view) {
	this.getTomcat().addRuntimeListener(this);
    }
    
    public void init(IWorkbenchWindow window) {
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.delegated = action;
        this.updateState();
    }

    private void updateState() {
        this.delegated.setEnabled(this.isEnabled());
    }
    
    public void run(IAction action) {
	this.tomcatAction.run(action);
    }

    protected abstract boolean isEnabled();

    public void tomcatStarted() {
        this.updateState();
    }

    public void tomcatStopped() {
        this.updateState();
    }

    public void dispose() {
        this.getTomcat().removeRuntimeListener(this);
    }

}