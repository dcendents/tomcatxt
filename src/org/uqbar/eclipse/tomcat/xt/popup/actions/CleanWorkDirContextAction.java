/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.popup.actions;

import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;

/**
 * 
 * @author ccancinos
 */
public class CleanWorkDirContextAction extends AbstractTomcatContextAction {

	@Override
	protected void run(TomcatContext context) {
		context.cleanWorkDir();
	}
	
}
