/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.factory;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.factory.BaseFactory;
import com.opensymphony.module.sitemesh.factory.FactoryException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class DefaultFactory
extends BaseFactory {
    String configFileName;
    private static final String DEFAULT_CONFIG_FILENAME = "/WEB-INF/sitemesh.xml";
    File configFile;
    long configLastModified;
    private long configLastCheck = 0L;
    public static long configCheckMillis = 3000L;
    Map configProps = new HashMap();
    String excludesFileName;
    File excludesFile;

    public DefaultFactory(Config config) {
        super(config);
        String configFilePath;
        String initParamConfigFile;
        this.configFileName = config.getServletContext().getInitParameter("sitemesh.configfile");
        if (this.configFileName == null) {
            this.configFileName = DEFAULT_CONFIG_FILENAME;
        }
        if ((initParamConfigFile = config.getConfigFile()) != null) {
            this.configFileName = initParamConfigFile;
        }
        if ((configFilePath = config.getServletContext().getRealPath(this.configFileName)) != null) {
            this.configFile = new File(configFilePath);
        }
        this.loadConfig();
    }

    private synchronized void loadConfig() {
        try {
            Element root = this.loadSitemeshXML();
            NodeList sections = root.getChildNodes();
            for (int i = 0; i < sections.getLength(); ++i) {
                String fileName;
                if (!(sections.item(i) instanceof Element)) continue;
                Element curr = (Element)sections.item(i);
                NodeList children = curr.getChildNodes();
                if ("config-refresh".equalsIgnoreCase(curr.getTagName())) {
                    String seconds = curr.getAttribute("seconds");
                    configCheckMillis = Long.parseLong(seconds) * 1000L;
                    continue;
                }
                if ("property".equalsIgnoreCase(curr.getTagName())) {
                    String name = curr.getAttribute("name");
                    String value = curr.getAttribute("value");
                    if ("".equals(name) || "".equals(value)) continue;
                    this.configProps.put("${" + name + "}", value);
                    continue;
                }
                if ("page-parsers".equalsIgnoreCase(curr.getTagName())) {
                    this.loadPageParsers(children);
                    continue;
                }
                if ("decorator-mappers".equalsIgnoreCase(curr.getTagName())) {
                    this.loadDecoratorMappers(children);
                    continue;
                }
                if (!"excludes".equalsIgnoreCase(curr.getTagName()) || "".equals(fileName = this.replaceProperties(curr.getAttribute("file")))) continue;
                this.excludesFileName = fileName;
                this.loadExcludes();
            }
        }
        catch (ParserConfigurationException e) {
            throw new FactoryException("Could not get XML parser", e);
        }
        catch (IOException e) {
            throw new FactoryException("Could not read config file : " + this.configFileName, e);
        }
        catch (SAXException e) {
            throw new FactoryException("Could not parse config file : " + this.configFileName, e);
        }
    }

    private Element loadSitemeshXML() throws ParserConfigurationException, IOException, SAXException {
        Document doc;
        Element root;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = null;
        if (this.configFile == null) {
            is = this.config.getServletContext().getResourceAsStream(this.configFileName);
        } else if (this.configFile.exists() && this.configFile.canRead()) {
            is = this.configFile.toURI().toURL().openStream();
        }
        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream("com/opensymphony/module/sitemesh/factory/sitemesh-default.xml");
        }
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/opensymphony/module/sitemesh/factory/sitemesh-default.xml");
        }
        if (is == null) {
            throw new IllegalStateException("Cannot load default configuration from jar");
        }
        if (this.configFile != null) {
            this.configLastModified = this.configFile.lastModified();
        }
        if (!"sitemesh".equalsIgnoreCase((root = (doc = builder.parse(is)).getDocumentElement()).getTagName())) {
            throw new FactoryException("Root element of sitemesh configuration file not <sitemesh>", null);
        }
        return root;
    }

    private void loadExcludes() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = null;
        if (this.excludesFile == null) {
            is = this.config.getServletContext().getResourceAsStream(this.excludesFileName);
        } else if (this.excludesFile.exists() && this.excludesFile.canRead()) {
            is = this.excludesFile.toURI().toURL().openStream();
        }
        if (is == null) {
            throw new IllegalStateException("Cannot load excludes configuration file \"" + this.excludesFileName + "\" as specified in \"sitemesh.xml\" or \"sitemesh-default.xml\"");
        }
        Document document = builder.parse(is);
        Element root = document.getDocumentElement();
        NodeList sections = root.getChildNodes();
        for (int i = 0; i < sections.getLength(); ++i) {
            Element curr;
            if (!(sections.item(i) instanceof Element) || !"excludes".equalsIgnoreCase((curr = (Element)sections.item(i)).getTagName())) continue;
            this.loadExcludeUrls(curr.getChildNodes());
        }
    }

    private void loadPageParsers(NodeList nodes) {
        this.clearParserMappings();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Element curr;
            if (!(nodes.item(i) instanceof Element) || !"parser".equalsIgnoreCase((curr = (Element)nodes.item(i)).getTagName())) continue;
            String className = curr.getAttribute("class");
            String contentType = curr.getAttribute("content-type");
            this.mapParser(contentType, className);
        }
    }

    private void loadDecoratorMappers(NodeList nodes) {
        this.clearDecoratorMappers();
        Properties emptyProps = new Properties();
        this.pushDecoratorMapper("com.opensymphony.module.sitemesh.mapper.NullDecoratorMapper", emptyProps);
        for (int i = nodes.getLength() - 1; i > 0; --i) {
            Element curr;
            if (!(nodes.item(i) instanceof Element) || !"mapper".equalsIgnoreCase((curr = (Element)nodes.item(i)).getTagName())) continue;
            String className = curr.getAttribute("class");
            Properties props = new Properties();
            NodeList children = curr.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Element currC;
                if (!(children.item(j) instanceof Element) || !"param".equalsIgnoreCase((currC = (Element)children.item(j)).getTagName())) continue;
                String value = currC.getAttribute("value");
                props.put(currC.getAttribute("name"), this.replaceProperties(value));
            }
            this.pushDecoratorMapper(className, props);
        }
        this.pushDecoratorMapper("com.opensymphony.module.sitemesh.mapper.InlineDecoratorMapper", emptyProps);
    }

    private void loadExcludeUrls(NodeList nodes) {
        this.clearExcludeUrls();
        for (int i = 0; i < nodes.getLength(); ++i) {
            String pattern;
            Text patternText;
            Element p;
            if (!(nodes.item(i) instanceof Element) || !"pattern".equalsIgnoreCase((p = (Element)nodes.item(i)).getTagName()) && !"url-pattern".equalsIgnoreCase(p.getTagName()) || (patternText = (Text)p.getFirstChild()) == null || (pattern = patternText.getData().trim()) == null) continue;
            this.addExcludeUrl(pattern);
        }
    }

    public void refresh() {
        long time = System.currentTimeMillis();
        if (time - this.configLastCheck < configCheckMillis) {
            return;
        }
        this.configLastCheck = time;
        if (this.configFile != null && this.configLastModified != this.configFile.lastModified()) {
            this.loadConfig();
        }
    }

    private String replaceProperties(String str) {
        Set props = this.configProps.entrySet();
        for (Map.Entry entry : props) {
            int idx;
            String key = (String)entry.getKey();
            while ((idx = str.indexOf(key)) >= 0) {
                StringBuffer buf = new StringBuffer(100);
                buf.append(str.substring(0, idx));
                buf.append(entry.getValue());
                buf.append(str.substring(idx + key.length()));
                str = buf.toString();
            }
        }
        return str;
    }
}

