/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.views.log;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

/**
 * 
 * @author jfernandes
 */
public class LogTableLabelProvider extends LabelProvider implements ITableLabelProvider {
	private Image logImage = UqbarSydeoXtActivator.getImageDescriptor("/icons/log.gif").createImage();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Override
	public Image getImage(Object element) {
		return logImage;
	}

	@Override
	public String getText(Object element) {
		return ((File) element).getName();
	}

	@Override
	public void dispose() {
		this.logImage.dispose();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return columnIndex == 0 ? this.logImage : null;
	}

	public String getColumnText(Object element, int columnIndex) {
		File file = (File) element;
		switch (columnIndex) {
		case 0:
			return file.getName();
		case 1:
			return NumberFormat.getNumberInstance().format(file.length() / 1000) + " KB";
		case 2:
			return dateFormat.format(new Date(file.lastModified()));
		}
		throw new RuntimeException("Unknown Column with index '" + columnIndex + "'");
	}
}