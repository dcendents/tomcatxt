/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.context.decorator;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * 
 * @author jfernandes
 */
public class TomcatContextSourceDecoration implements ILightweightLabelDecorator {
	private Color color;
	
	public TomcatContextSourceDecoration() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				color = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
			}});
	}
	
	public void decorate(Object element, IDecoration decoration) {
		TomcatContext context = (TomcatContext) element;
		/*decoration.setForegroundColor(this.color);*/
		decoration.addSuffix("  [" + context.getSourceDescription() + "]");
		
	}

	public void dispose() {
		this.color.dispose();
		this.color = null;
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
	}
	
	public void removeListener(ILabelProviderListener listener) {
	}

}
