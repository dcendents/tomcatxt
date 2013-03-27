/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatObserver;

/**
 * @author jfernandes
 */
public class TomcatContextsContentProvider implements IStructuredContentProvider, ITreeContentProvider, TomcatObserver {
	private Viewer viewer;

	public TomcatContextsContentProvider() {
		UqbarSydeoXtActivator.getDefault().getTomcat().addObserver(this);
	}

	public Object[] getElements(Object inputElement) {
		return UqbarSydeoXtActivator.getDefault().getTomcat().getContexts().toArray();
	}

	public Object[] getChildren(Object parentElement) {
		return this.getElements(parentElement);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public void dispose() {
		UqbarSydeoXtActivator.getDefault().getTomcat().removeObserver(this);
	}

	public void tomcatChanged() {
		this.viewer.refresh();
	}

}