/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.model;

import java.io.File;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.xml.Context;

/**
 * @author jfernandes
 */
public class FileTomcatContext extends SimpleTomcatContext {
	private static final String DISABLED_EXT = ".disabled";
	private static final int DISABLED_EXT_SIZE = DISABLED_EXT.length();
	
	private File contextFile;

	public FileTomcatContext(File contextFile, Context context) {
		super(context);
		this.contextFile = contextFile;
	}

	@Override
	public String getLabel(Object o) {
		String label = super.getLabel(o);
		return UqbarSydeoXtActivator.noe(label) ? this.contextFile.getName() : label;
	}
	
	@Override
	public void remove() {
		if (!this.contextFile.delete()) {
			UqbarSydeoXtActivator.showError("Error", "Couldn't delete context file '" + this.contextFile + "'\n");
		}
	}

	public boolean canRemove() {
		return true;
	}
	
	@Override
	public String getSourceDescription() {
		return this.contextFile.getName();
	}
	
	@Override
	public void disable() {
		if (!this.isDisable() && 
			!this.contextFile.renameTo(new File(this.contextFile.getPath()+ DISABLED_EXT))) {
			UqbarSydeoXtActivator.showError("Error", "Couldn't disable context file '" + this.contextFile + "'\n");
}
	}
	
	@Override
	public boolean isDisable() {
		return this.contextFile.getName().endsWith(DISABLED_EXT);
	}
	
	@Override
	public void enable() {
		String newName = this.contextFile.getPath().substring(0, this.contextFile.getPath().length() - DISABLED_EXT_SIZE);
		if(this.isDisable() &&
		   !this.contextFile.renameTo(new File(newName))) {
			UqbarSydeoXtActivator.showError("Error", "Couldn't disable context file '" + this.contextFile + "'\n");
		}
	}
	
}
