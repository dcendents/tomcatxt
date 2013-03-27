package org.uqbar.eclipse.tomcat.xt.views.log;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * @author jfernandes
 */
public abstract class TableColumnSorter extends ViewerComparator {
    public static final int ASC = 1;
    public static final int NONE = 0;
    public static final int DESC = -1;
    private int direction = 0;
    private TableColumn column;
    private TableViewer viewer;

    public TableColumnSorter(TableViewer viewer, TableColumn column) {
	this.column = column;
	this.viewer = viewer;
	this.column.addSelectionListener(new SelectionAdapter() {

	    public void widgetSelected(SelectionEvent e) {
		if (TableColumnSorter.this.viewer.getComparator() != null) {
		    if (TableColumnSorter.this.viewer.getComparator() == TableColumnSorter.this) {
			int tdirection = TableColumnSorter.this.direction;

			if (tdirection == ASC) {
			    setSorter(TableColumnSorter.this, DESC);
			}
			else if (tdirection == DESC) {
			    setSorter(TableColumnSorter.this, NONE);
			}
		    }
		    else {
			setSorter(TableColumnSorter.this, ASC);
		    }
		}
		else {
		    setSorter(TableColumnSorter.this, ASC);
		}
	    }
	});
    }

    public void setSorter(TableColumnSorter sorter, int direction) {
	if (direction == NONE) {
	    column.getParent().setSortColumn(null);
	    column.getParent().setSortDirection(SWT.NONE);
	    viewer.setComparator(null);
	}
	else {
	    column.getParent().setSortColumn(column);
	    sorter.direction = direction;

	    if (direction == ASC) {
		column.getParent().setSortDirection(SWT.DOWN);
	    }
	    else {
		column.getParent().setSortDirection(SWT.UP);
	    }

	    if (viewer.getComparator() == sorter) {
		viewer.refresh();
	    }
	    else {
		viewer.setComparator(sorter);
	    }

	}
    }

    public int compare(Viewer viewer, Object e1, Object e2) {
	return direction * doCompare(viewer, e1, e2);
    }

    protected abstract int doCompare(Viewer TableViewer, Object e1, Object e2);
}