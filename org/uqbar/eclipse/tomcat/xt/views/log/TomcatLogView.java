package org.uqbar.eclipse.tomcat.xt.views.log;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.views.OpenTomcatFileAction;
import org.uqbar.eclipse.tomcat.xt.views.TomcatManagementView;

/**
 * @author jfernandes
 */
public class TomcatLogView extends ViewPart {
	private TableViewer viewer;

	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new LogTableLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(getViewSite());
		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				for (Object file : selection.toArray()) {
					OpenTomcatFileAction.openEditor((File) file);
				}
			}
		});

		this.hookContextMenu();
		
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
		
		this.createColumns(this.viewer);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
	    manager.add(new Action("Refresh", UqbarSydeoXtActivator.getImageDescriptor("/icons/refresh.gif")) {
				public void run() {
					viewer.refresh();
				}
			});
	    manager.add(new Action("Clear Log Files", UqbarSydeoXtActivator.getImageDescriptor("/icons/delete.gif")) {
		@Override
		public void run() {
		    for (File logFile : ViewContentProvider.getLogsDir().listFiles()) {
		    	logFile.delete();
		    }
		    viewer.refresh();
		}
	    });
	}

	protected void createColumns(TableViewer viewer2) {
		int i = 0;
		for (String name : this.getColumnNames()) {
			final int colIdx = i;
			TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
			column.setText(name);
			column.setMoveable(true);

			TableColumnSorter cSorter = new TableColumnSorter(viewer, column) {
				protected int doCompare(Viewer v, Object e1, Object e2) {
					ITableLabelProvider lp = ((ITableLabelProvider) viewer.getLabelProvider());
					String t1 = lp.getColumnText(e1, colIdx);
					String t2 = lp.getColumnText(e2, colIdx);
					return t1.compareTo(t2);
				}
			};

			cSorter.setSorter(cSorter, TableColumnSorter.ASC);
			column.pack();
			i++;
		}
	}

	private List<String> getColumnNames() {
		return Arrays.asList("Name", "Size", "Last Modified");
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TomcatLogView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		final File logFile = (File) selection.getFirstElement();
		
		manager.add(new Action("Delete", UqbarSydeoXtActivator.getImageDescriptor("/icons/delete.gif")) {
			@Override
			public void run() {
				logFile.delete();
				viewer.refresh();
			}
		});
		
		/*manager.add(new Action("Send By Mail") {
			@Override
			public void run() {
				try {
					Workbench.getInstance().getBrowserSupport().createBrowser("mailTo").openURL(new URL("mailto:iudith.m@zim.co.il?subject=Tomcat log file&body=see attachment&attachment=" + logFile.getAbsolutePath()));
				}
				catch (MalformedURLException e) {
					throw new RuntimeException("Sarsaas", e);
				}
				catch (IOException e) {
					throw new RuntimeException("Sarsaas", e);
				}
				catch (PartInitException e) {
					throw new RuntimeException("Sarsaas", e);
				}
			}
		});*/
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}