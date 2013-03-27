/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.context.decorator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContextListener;
import org.uqbar.eclipse.tomcat.xt.model.TomcatRuntimeListener;

/**
 * @author jfernandes
 */
// Unify tomcat state listening with TomcatDecorator
public class TomcatContextDecorator implements ILightweightLabelDecorator, TomcatRuntimeListener, TomcatContextListener {
	private Set<TomcatContext> listenedContexts = new HashSet<TomcatContext>();

	public TomcatContextDecorator() {
		UqbarSydeoXtActivator.getDefault().getTomcat().addRuntimeListener(this);
	}

	public void decorate(Object element, IDecoration decoration) {
		TomcatContext context = (TomcatContext) element;
		if(context.isDisable()) {
			decoration.addOverlay(UqbarSydeoXtActivator.getImageDescriptor("/icons/tomcat_stopped.gif"));
		} 

		if (UqbarSydeoXtActivator.getDefault().getTomcat().isRunning()) {
			if (context.isRunning() && !context.isDisable()) {
				decoration.addOverlay(UqbarSydeoXtActivator.getImageDescriptor("/icons/sample_decorator.gif"));
			}
			else {
				decoration.addOverlay(UqbarSydeoXtActivator.getImageDescriptor("/icons/context_stopped.gif"));
			}
			context.addRuntimeListener(this);
			this.listenedContexts.add(context);
		}
	}

	private List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();

	public void addListener(ILabelProviderListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ILabelProviderListener listener) {
		this.listeners.remove(listener);
	}

	public void tomcatStarted() {
		this.fireChanged();
	}

	public void tomcatStopped() {
		this.fireChanged();
	}

	private void fireChanged() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				for (ILabelProviderListener listener : listeners) {
					listener.labelProviderChanged(new LabelProviderChangedEvent(TomcatContextDecorator.this));
				}
			}
		});
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void contextStarted(TomcatContext tomcatContext) {
		this.fireContextChanged(tomcatContext);
	}

	private void fireContextChanged(TomcatContext tomcatContext) {
		for (ILabelProviderListener listener : this.listeners) {
			listener.labelProviderChanged(new LabelProviderChangedEvent(this, tomcatContext));
		}
	}

	public void contextStopped(TomcatContext tomcatContext) {
		for (ILabelProviderListener listener : this.listeners) {
			listener.labelProviderChanged(new LabelProviderChangedEvent(this, tomcatContext));
		}
	}

	public void dispose() {
		UqbarSydeoXtActivator.getDefault().getTomcat().removeRuntimeListener(this);
		for (TomcatContext context : this.listenedContexts) {
			context.removeRuntimeListener(this);
		}
	}
}
