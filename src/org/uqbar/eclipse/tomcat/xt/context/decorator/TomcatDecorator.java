/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.context.decorator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.Tomcat;
import org.uqbar.eclipse.tomcat.xt.model.TomcatRuntimeListener;

/**
 * @author jfernandes
 */
public class TomcatDecorator implements ILightweightLabelDecorator, TomcatRuntimeListener {

	public TomcatDecorator() {
		UqbarSydeoXtActivator.getDefault().getTomcat().addRuntimeListener(this);
	}

	public void decorate(Object element, IDecoration decoration) {
		final ImageDescriptor decorationImage = UqbarSydeoXtActivator.getDefault().getImageDescriptor(
				"/icons/" + this.getImageName((Tomcat) element) + ".gif");
		decoration.addOverlay(decorationImage, IDecoration.BOTTOM_RIGHT);
	}

	private String getImageName(Tomcat tomcat) {
		return tomcat.isRunning() ? "tomcat_running" : "tomcat_stopped";
	}

	private List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();

	public void addListener(ILabelProviderListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ILabelProviderListener listener) {
		this.listeners.remove(listener);
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
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
					listener.labelProviderChanged(new LabelProviderChangedEvent(TomcatDecorator.this));
				}
			}
		});
	}

	public void dispose() {
		UqbarSydeoXtActivator.getDefault().getTomcat().removeRuntimeListener(this);
	}

}
