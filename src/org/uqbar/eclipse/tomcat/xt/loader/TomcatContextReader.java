/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.loader;

import java.util.List;

import org.uqbar.eclipse.tomcat.xt.model.Tomcat;
import org.uqbar.eclipse.tomcat.xt.model.TomcatConfig;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;


/**
 * @author jfernandes
 */
public interface TomcatContextReader {

    public List<TomcatContext> readContexts(Tomcat tomcat);

    public TomcatConfig loadConfig(Tomcat tomcat);

    /**
     * Negrada porque la impl guarda estado (el DOM del server.xml para no leerlo varias veces)
     * Entonces cuando cambia TOMCAT_HOME hay que recargar todo :(
     * Tal vez el reader debería tener otro nombre y ser más piola, escuchar
     * la preferencia de TOMCAT_HOME y recargarse solito.
     */
	public void refresh();
    
}
