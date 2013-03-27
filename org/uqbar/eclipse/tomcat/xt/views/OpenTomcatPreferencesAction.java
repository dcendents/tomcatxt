/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.views;

import org.eclipse.jface.action.Action;
import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;

/**
 * @author jfernandes
 */
public class OpenTomcatPreferencesAction extends Action {

    public OpenTomcatPreferencesAction() {
	super("Preferences");
    }
    
    @Override
    public void run() {
	UqbarSydeoXtActivator.openPreferencePage(); 
    }

}
