/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

import java.io.File;
import java.io.IOException;

import org.uqbar.eclipse.tomcat.xt.xml.Context;

import com.sysdeo.eclipse.tomcat.FileUtil;

/**
 * @author jfernandes
 */
public class SimpleTomcatContext extends TomcatContext {
	private Context context;

	public SimpleTomcatContext(Context context) {
		this.context = context;
	}

	public String getLabel(Object o) {
		return this.getContextPath();
	}

	@Override
	public String getContextPath() {
		return this.context.getPath();
	}

	@Override
	public void remove() {
	}

	@Override
	public boolean canRemove() {
		return false;
	}

    @Override
    public String getDocBase() {
        return this.context.getDocBase();
    }
    
    @Override
    public String getSourceDescription() {
    	return "server.xml";
    }

	@Override
	public void disable() {
	}
	
	@Override
	public void enable() {
	}

	@Override
	public boolean isDisable() {
		return false;
	}
	
	@Override
	public void cleanWorkDir() {
		try {
			FileUtil.removeDir(new File(this.context.getWorkDir()));
		} catch (IOException e) {
		}
	}

}
