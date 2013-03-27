/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

/**
 * Rough interface for listening for tomcat "changes".
 * It should be a lot more refined to particular events, but for now it's useful. 
 * 
 * @author jfernandes
 */
public interface TomcatObserver {

    public void tomcatChanged();
    
}
