/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.upm.core.install;

import com.atlassian.security.xml.SecureXmlParserFactory;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.install.InvalidDescriptorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PluginDescriptor {
    private final Document document;

    private PluginDescriptor(Document document) {
        this.document = Objects.requireNonNull(document);
    }

    public static PluginDescriptor fromFile(File f) throws InvalidDescriptorException {
        PluginDescriptor pluginDescriptor;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            pluginDescriptor = PluginDescriptor.fromInputStream(fis);
        }
        catch (FileNotFoundException e) {
            try {
                throw new InvalidDescriptorException(e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(fis);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((InputStream)fis);
        return pluginDescriptor;
    }

    public static PluginDescriptor fromInputStream(InputStream is) throws InvalidDescriptorException {
        Document doc = null;
        try {
            DocumentBuilderFactory factory = SecureXmlParserFactory.newDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            doc = factory.newDocumentBuilder().parse(new InputSource(is));
        }
        catch (Exception e) {
            throw new InvalidDescriptorException(e);
        }
        if (!"atlassian-plugin".equals(doc.getDocumentElement().getNodeName())) {
            throw new InvalidDescriptorException();
        }
        return new PluginDescriptor(doc);
    }

    public String getPluginsVersion() {
        NamedNodeMap attributes = this.document.getDocumentElement().getAttributes();
        Node pluginsVersion = attributes.getNamedItem("plugins-version") != null ? attributes.getNamedItem("plugins-version") : attributes.getNamedItem("pluginsVersion");
        return pluginsVersion != null ? pluginsVersion.getTextContent() : "1";
    }

    public Option<String> getPluginKey() {
        NamedNodeMap attributes = this.document.getDocumentElement().getAttributes();
        Node pluginKey = attributes.getNamedItem("key");
        return pluginKey != null ? Option.some(pluginKey.getTextContent()) : Option.none(String.class);
    }

    public Option<String> getVersion() {
        NodeList versions = this.document.getDocumentElement().getElementsByTagName("version");
        for (int i = 0; i < versions.getLength(); ++i) {
            Node version = versions.item(i);
            if (!"plugin-info".equals(version.getParentNode().getNodeName())) continue;
            return Option.some(version.getTextContent());
        }
        return Option.none();
    }

    public boolean hasRemotePluginContainer() {
        return this.hasElementByTagName("remote-plugin-container");
    }

    public boolean isApplication() {
        return this.hasElementByTagName("application");
    }

    private boolean hasElementByTagName(String tagName) {
        NodeList found = this.document.getDocumentElement().getElementsByTagName(tagName);
        return found != null && found.getLength() > 0;
    }
}

