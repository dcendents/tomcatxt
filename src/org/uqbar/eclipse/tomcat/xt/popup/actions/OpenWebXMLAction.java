/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import java.io.File;

import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;
import org.uqbar.eclipse.tomcat.xt.views.OpenTomcatFileAction;

/**
 * @author jfernandes
 */
public class OpenWebXMLAction extends AbstractTomcatContextAction {

	@Override
	protected void run(TomcatContext context) {
		OpenTomcatFileAction.openEditor(this.getFile(context));
	}

	@Override
	protected boolean isEnabledFor(TomcatContext context) {
		return this.getFile(context).exists();
	}

	protected File getFile(TomcatContext context) {
		return new File(context.getDocBase() + File.separator + "WEB-INF" + File.separator + "web.xml");
	}

}
