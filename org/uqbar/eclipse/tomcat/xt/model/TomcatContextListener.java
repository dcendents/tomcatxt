/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

/**
 * @author jfernandes
 */
public interface TomcatContextListener {

    public void contextStarted(TomcatContext tomcatContext);

    public void contextStopped(TomcatContext tomcatContext);

    
}
