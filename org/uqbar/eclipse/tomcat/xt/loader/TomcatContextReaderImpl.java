/**
 * 
 */
package org.uqbar.eclipse.tomcat.xt.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.uqbar.eclipse.tomcat.xt.UqbarSydeoXtActivator;
import org.uqbar.eclipse.tomcat.xt.model.FileTomcatContext;
import org.uqbar.eclipse.tomcat.xt.model.SimpleTomcatContext;
import org.uqbar.eclipse.tomcat.xt.model.Tomcat;
import org.uqbar.eclipse.tomcat.xt.model.TomcatConfig;
import org.uqbar.eclipse.tomcat.xt.model.TomcatContext;
import org.uqbar.eclipse.tomcat.xt.xml.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sysdeo.eclipse.tomcat.FileUtil;
import com.sysdeo.eclipse.tomcat.TomcatLauncherPlugin;
import com.sysdeo.eclipse.tomcat.TomcatProject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * @author jfernandes
 */
// HARDCODE!!! terriblemente!!
public class TomcatContextReaderImpl implements TomcatContextReader {

	public static final class ContextFileFilter implements FilenameFilter {
		public static final ContextFileFilter SHARED_INSTANCE = new ContextFileFilter();

		public boolean accept(File dir, String name) {
			return name.endsWith(".xml") || name.endsWith(".disabled");
		}
	}

	// DUPLICATED FROM TomcatLauncherPlugin
	public static final String SERVERXML_MODE = "serverFile";

	public List<TomcatContext> readContexts(Tomcat tomcat) {
		List<TomcatContext> contexts = new ArrayList<TomcatContext>();
		//HARCODE: handle "no tomcat configured" state well. 
		if(!TomcatLauncherPlugin.isTomcatConfigured()) {
			return contexts;
		}
		this.loadFromServerXML(contexts);
		this.loadFromContextFiles(contexts);
		return contexts;
	}

	private void loadFromContextFiles(List<TomcatContext> contexts) {
		File contextsFolder = new File(TomcatLauncherPlugin.getDefault().getContextsDir());
		if (!contextsFolder.exists()) {
			return;
		}
		for (File contextFile : contextsFolder.listFiles(ContextFileFilter.SHARED_INSTANCE)) {
			contexts.add(this.parseContextFile(contextFile));
		}
	}

	private void loadFromServerXML(List<TomcatContext> contexts) {
		XPathExpression exp = this.findContextXPatchExpression();
		try {
			NodeList result = (NodeList) exp.evaluate(this.getServerXMLDOM(), XPathConstants.NODESET);
			for (int i = 0; i < result.getLength(); i++) {
				Node item = result.item(i);
				Context context = new Context();
				this.mapAttribute(context, item, "path");
				this.mapAttribute(context, item, "workDir");
				this.mapAttribute(context, item, "docBase");
				Node reloadableAttribute = item.getAttributes().getNamedItem("reloadable");
				if (reloadableAttribute != null) {
					context.setReloadable(Boolean.valueOf(reloadableAttribute.getTextContent()));
				}
				contexts.add(new SimpleTomcatContext(context));
			}
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("Errow while quering server.xml for contexts", e);
		}
	}

	protected void mapAttribute(Object obj, Node item, String name) {
		Node attribute = item.getAttributes().getNamedItem(name);
		if (attribute != null) {
			UqbarSydeoXtActivator.setPropertyValue(obj, name, attribute.getTextContent());
		}
	}

	private XPathExpression findContextXPathExpression;
	private Document serverXML;
	private XPath xpath;

	private XPathExpression findContextXPatchExpression() {
		if (this.findContextXPathExpression == null) {
			try {
				this.findContextXPathExpression = this.getXPath().compile("/Server/Service/Engine/Host/Context");
			}
			catch (XPathExpressionException e) {
				throw new RuntimeException("Error while creating xpath expression for context on server.xml", e);
			}
		}
		return this.findContextXPathExpression;
	}

	private XPath getXPath() {
		if (this.xpath == null) {
			this.xpath = XPathFactory.newInstance().newXPath();
		}
		return this.xpath;
	}

	private Document getServerXMLDOM() {
		if (this.serverXML == null) {
			this.serverXML = this.parseServerXMLDOM();
		}
		return this.serverXML;
	}

	private Document parseServerXMLDOM() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new File(TomcatLauncherPlugin.getDefault().getConfigFile()));
		}
		catch (SAXException e) {
			throw new RuntimeException("Error while reading server.xml", e);
		}
		catch (IOException e) {
			throw new RuntimeException("Error while reading server.xml", e);
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException("Error while reading server.xml", e);
		}
	}

	// ****************************************
	// ** Copied from TomcatProject
	// ****************************************

	protected boolean hasTomcatContext(TomcatProject tomcatProject) {
		return isServerXmlConfig() ? this.hasContextInServerXML(tomcatProject) : this.hasContextFile(tomcatProject);
	}

	private boolean hasContextInServerXML(TomcatProject tomcatProject) {
		try {
			String xml = FileUtil
					.readTextFile((File) UqbarSydeoXtActivator.forcedInvoke(tomcatProject, "getServerXML"));
			return !contextExistsInXML(xml, tomcatProject);
		}
		catch (IOException e) {
			throw new RuntimeException("Error while reading tomcat contexts", e);
		}
	}

	private boolean contextExistsInXML(String xml, TomcatProject project) throws IOException {
		return (getContextTagIndex(xml, project) != -1);
	}

	private int getContextTagIndex(String xml, TomcatProject project) throws IOException {
		int pathIndex = xml.indexOf((String) UqbarSydeoXtActivator.forcedInvoke(project, "getContextPath"));

		if (pathIndex == -1)
			return -1;

		int tagIndex = (xml.substring(0, pathIndex)).lastIndexOf('<');
		String tag = xml.substring(tagIndex, tagIndex + 8);
		if (!tag.equalsIgnoreCase((String) UqbarSydeoXtActivator.forcedInvoke(project, "getContextStartTag")))
			return -1;

		return tagIndex;
	}

	private boolean hasContextFile(TomcatProject tomcatProject) {
		return ((File) UqbarSydeoXtActivator.forcedInvoke(tomcatProject, "getContextFile")).exists();
	}

	protected boolean isServerXmlConfig() {
		return TomcatLauncherPlugin.getDefault().getConfigMode().equals(SERVERXML_MODE);
	}

	protected TomcatContext parseContextFile(File contextFile) {
		FileInputStream fileStream = null;
		try {
			XStream xstream = UqbarSydeoXtActivator.getDefault().getXStream();
			fileStream = new FileInputStream(contextFile);
			return new FileTomcatContext(contextFile, (Context) xstream.fromXML(new BufferedInputStream(fileStream)));
		}
		catch (XStreamException e) {
			throw new RuntimeException("Errow while parsing context file '" + contextFile.getAbsolutePath() + "'");
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException("Errow while parsing context file '" + contextFile.getAbsolutePath() + "'");
		}
		finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public TomcatConfig loadConfig(Tomcat tomcat) {
		Node hostNode = this.executeServerPortExpression("/Server/Service/Engine/Host");
		TomcatConfig config = new TomcatConfig();
		this.mapAttribute(config, hostNode, "name");
		Node connectorNode = this.executeServerPortExpression("/Server/Service/Connector");
		Node portAttribute = connectorNode.getAttributes().getNamedItem("port");
		if (portAttribute == null) {
			throw new RuntimeException("The 'port' attribute in the <Connector> tag is not defined!!!");
		}
		config.setPort(Integer.valueOf(portAttribute.getTextContent()));
		return config;
	}

	private Node executeServerPortExpression(String expression) {
		try {
			// HARDCODE GODDAMN !!
			NodeList hosts = (NodeList) this.getXPath().evaluate(expression, this.getServerXMLDOM(),
					XPathConstants.NODESET);
			return hosts.item(0);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("Error while queryin server.xml for <Host> element", e);
		}
	}
	
	public void refresh() {
		this.serverXML = null;
	}

}