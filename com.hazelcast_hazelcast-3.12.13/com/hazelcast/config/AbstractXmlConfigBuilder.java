/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractXmlConfigHelper;
import com.hazelcast.config.ConfigLoader;
import com.hazelcast.config.ConfigReplacerHelper;
import com.hazelcast.config.ConfigSections;
import com.hazelcast.config.DomConfigHelper;
import com.hazelcast.config.DomVariableReplacer;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.XmlDomVariableReplacer;
import com.hazelcast.config.replacer.PropertyReplacer;
import com.hazelcast.config.replacer.spi.ConfigReplacer;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.StringUtil;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXmlConfigBuilder
extends AbstractXmlConfigHelper {
    private static final ILogger LOGGER = Logger.getLogger(AbstractXmlConfigBuilder.class);
    private Properties properties = System.getProperties();
    private final Set<String> currentlyImportedFiles = new HashSet<String>();
    private final XPath xpath;

    public AbstractXmlConfigBuilder() {
        XPathFactory fac = XPathFactory.newInstance();
        this.xpath = fac.newXPath();
        this.xpath.setNamespaceContext(new NamespaceContext(){

            @Override
            public String getNamespaceURI(String prefix) {
                return "hz".equals(prefix) ? AbstractXmlConfigBuilder.this.xmlns : null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
    }

    protected void process(Node root) throws Exception {
        this.traverseChildrenAndReplaceVariables(root);
        this.replaceImportElementsWithActualFileContents(root);
    }

    private void replaceImportElementsWithActualFileContents(Node root) throws Exception {
        Document document = root.getOwnerDocument();
        NodeList misplacedImports = (NodeList)this.xpath.evaluate(String.format("//hz:%s/parent::*[not(self::hz:%s)]", ConfigSections.IMPORT.name, this.getConfigType().name), document, XPathConstants.NODESET);
        if (misplacedImports.getLength() > 0) {
            throw new InvalidConfigurationException("<import> element can appear only in the top level of the XML");
        }
        NodeList importTags = (NodeList)this.xpath.evaluate(String.format("/hz:%s/hz:%s", this.getConfigType().name, ConfigSections.IMPORT.name), document, XPathConstants.NODESET);
        for (Node node : DomConfigHelper.asElementIterable(importTags)) {
            this.loadAndReplaceImportElement(root, node);
        }
    }

    private void loadAndReplaceImportElement(Node root, Node node) throws Exception {
        NamedNodeMap attributes = node.getAttributes();
        Node resourceAttribute = attributes.getNamedItem("resource");
        String resource = resourceAttribute.getTextContent();
        URL url = ConfigLoader.locateConfig(resource);
        if (url == null) {
            throw new InvalidConfigurationException("Failed to load resource: " + resource);
        }
        if (!this.currentlyImportedFiles.add(url.getPath())) {
            throw new InvalidConfigurationException("Resource '" + url.getPath() + "' is already loaded! This can be due to duplicate or cyclic imports.");
        }
        Document doc = this.parse(url.openStream());
        Element importedRoot = doc.getDocumentElement();
        this.traverseChildrenAndReplaceVariables(importedRoot);
        this.replaceImportElementsWithActualFileContents(importedRoot);
        for (Node fromImportedDoc : DomConfigHelper.childElements(importedRoot)) {
            Node importedNode = root.getOwnerDocument().importNode(fromImportedDoc, true);
            root.insertBefore(importedNode, node);
        }
        root.removeChild(node);
    }

    protected abstract Document parse(InputStream var1) throws Exception;

    public Properties getProperties() {
        return this.properties;
    }

    protected void setPropertiesInternal(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected abstract ConfigType getConfigType();

    String getAttribute(Node node, String attName) {
        return DomConfigHelper.getAttribute(node, attName, this.domLevel3);
    }

    void fillProperties(Node node, Properties properties) {
        DomConfigHelper.fillProperties(node, properties, this.domLevel3);
    }

    private void traverseChildrenAndReplaceVariables(Node root) throws Exception {
        boolean failFast = false;
        ArrayList<ConfigReplacer> replacers = new ArrayList<ConfigReplacer>();
        PropertyReplacer propertyReplacer = new PropertyReplacer();
        propertyReplacer.init(this.getProperties());
        replacers.add(propertyReplacer);
        Node node = (Node)this.xpath.evaluate(String.format("/hz:%s/hz:%s", this.getConfigType().name, ConfigSections.CONFIG_REPLACERS.name), root, XPathConstants.NODE);
        if (node != null) {
            String failFastAttr = this.getAttribute(node, "fail-if-value-missing");
            failFast = StringUtil.isNullOrEmpty(failFastAttr) || Boolean.parseBoolean(failFastAttr);
            for (Node n : DomConfigHelper.childElements(node)) {
                String value = DomConfigHelper.cleanNodeName(n);
                if (!"replacer".equals(value)) continue;
                replacers.add(this.createReplacer(n));
            }
        }
        ConfigReplacerHelper.traverseChildrenAndReplaceVariables(root, replacers, failFast, (DomVariableReplacer)new XmlDomVariableReplacer());
    }

    private ConfigReplacer createReplacer(Node node) throws Exception {
        String replacerClass = this.getAttribute(node, "class-name");
        Properties properties = new Properties();
        for (Node n : DomConfigHelper.childElements(node)) {
            String value = DomConfigHelper.cleanNodeName(n);
            if (!"properties".equals(value)) continue;
            this.fillProperties(n, properties);
        }
        ConfigReplacer replacer = (ConfigReplacer)Class.forName(replacerClass).newInstance();
        replacer.init(properties);
        return replacer;
    }

    protected static enum ConfigType {
        SERVER("hazelcast"),
        CLIENT("hazelcast-client"),
        JET("hazelcast-jet"),
        CLIENT_FAILOVER("hazelcast-client-failover");

        final String name;

        private ConfigType(String name) {
            this.name = name;
        }
    }
}

