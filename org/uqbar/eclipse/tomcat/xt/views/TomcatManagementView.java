package org.uqbar.eclipse.tomcat.xt.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.uqbar.eclipse.swt.jface.richlabel.ColoredViewersManager;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;
import org.uqbar.eclipse.tomcat.xt.model.TomcatRuntimeListener;
import org.uqbar.eclipse.tomcat.xt.popup.actions.BrowseContextAction;

/**
 * @author jfernandes
 */
public class TomcatManagementView extends ViewPart implements TomcatRuntimeListener, ILabelProviderListener {
	private TableViewer viewer;
	private Action refresh;
	private ILabelProvider labelProvider;

	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent);
		viewer.setContentProvider(new TomcatContextsContentProvider());
		labelProvider = new RichWorkbenchLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(getViewSite());
		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				TomcatContext context = (TomcatContext) ((IStructuredSelection) event.getSelection()).getFirstElement();
				BrowseContextAction.openBrowser(context);
			}
		});
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		labelProvider.addListener(this);
		ColoredViewersManager.install(this.viewer);
		UqbarSydeoXtActivator.getDefault().getTomcat().addRuntimeListener(this);
		this.tomcatStarted();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TomcatManagementView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Action("Preferences") {
			@Override
			public void run() {
				UqbarSydeoXtActivator.openPreferencePage();
			}
		});
		manager.add(new Action("Manager") {
			@Override
			public void run() {
				UqbarSydeoXtActivator.openManagerPreferencesPage();
			}
		});
		manager.add(new OpenTomcatFileAction("Tomcat-users.xml", "tomcat-users.xml"));
		manager.add(new OpenTomcatFileAction("Server.xml", "server.xml"));
		manager.add(new Action("Install Devloader") {
			@Override
			public void run() {
				UqbarSydeoXtActivator.installDevloader();
			}
		});
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refresh);
	}

	private void makeActions() {
		refresh = new Action("Refresh", UqbarSydeoXtActivator.getImageDescriptor("/icons/refresh.gif")) {
			public void run() {
				UqbarSydeoXtActivator.getDefault().getTomcat().refreshContexts();
				tomcatStarted();
			}
		};
		refresh.setToolTipText("Refresh Contexts");
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void tomcatStarted() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Image icon = labelProvider.getImage(UqbarSydeoXtActivator.getDefault().getTomcat());
				setTitleImage(icon);
				firePropertyChange(IViewPart.PROP_TITLE);
			}
		});
	}

	public void tomcatStopped() {
		this.tomcatStarted();
	}

	@Override
	public void dispose() {
		UqbarSydeoXtActivator.getDefault().getTomcat().removeRuntimeListener(this);
		super.dispose();
	}

	public void labelProviderChanged(LabelProviderChangedEvent event) {
		this.tomcatStarted();
	}
}