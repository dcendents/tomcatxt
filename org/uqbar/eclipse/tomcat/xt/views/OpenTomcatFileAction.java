/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.views;

import java.io.File;

import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;

/**
 * @author jfernandes
 */
public class OpenTomcatFileAction extends Action {
	private final String fileName;

	public OpenTomcatFileAction(String name, String fileName) {
		super(name);
		this.fileName = fileName;
		this.setImageDescriptor(UqbarSydeoXtActivator.getImageDescriptor("/icons/web_xml.gif"));
	}

	@Override
	public void run() {
		openEditor(new File(TomcatLauncherPlugin.getDefault().getTomcatDir() + File.separator + "conf" + File.separator
				+ fileName));
	}

	public static void openEditor(File file) {
		try {
			IDE.openEditorOnFileStore(Workbench.getInstance().getActiveWorkbenchWindow().getActivePage(),
					new LocalFile(file));
		}
		catch (PartInitException ex) {
			throw new RuntimeException("Error while opening editor for file '" + file + "'", ex);
		}
	}

}
