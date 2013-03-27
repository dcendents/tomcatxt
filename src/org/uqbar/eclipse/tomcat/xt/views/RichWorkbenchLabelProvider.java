/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.uqbar.eclipse.swt.jface.richlabel.ColoredString;
import org.uqbar.eclipse.swt.jface.richlabel.ColoredViewersManager;
import org.uqbar.eclipse.swt.jface.richlabel.IRichLabelProvider;

/**
 * 
 * @author jfernandes
 */
public class RichWorkbenchLabelProvider extends DecoratingLabelProvider implements IRichLabelProvider {

	public RichWorkbenchLabelProvider() {
		super(new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

	public ColoredString getRichTextLabel(Object element) {
		String decorated = super.getText(element);
		return ColoredViewersManager.decorateColoredString(new ColoredString(this.getLabelProvider().getText(element)), decorated, ColoredViewersManager.DECORATIONS_STYLE);
	}
	
}
