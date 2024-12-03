/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.SystemUtils
 *  org.dom4j.Branch
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.dom4j.io.OutputFormat
 *  org.dom4j.io.SAXReader
 *  org.dom4j.io.XMLWriter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config.xml;

import com.atlassian.config.AbstractConfigurationPersister;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.xml.Dom4jXmlListConfigElement;
import com.atlassian.config.xml.Dom4jXmlMapConfigElement;
import com.atlassian.config.xml.Dom4jXmlMapEntryConfigElement;
import com.atlassian.config.xml.Dom4jXmlStringConfigElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public abstract class AbstractDom4jXmlConfigurationPersister
extends AbstractConfigurationPersister {
    private static final Logger privateLog = LoggerFactory.getLogger(AbstractDom4jXmlConfigurationPersister.class);
    private Document document;
    private boolean useCData = false;

    public AbstractDom4jXmlConfigurationPersister() {
        this.clearDocument();
        this.addConfigMapping(String.class, Dom4jXmlStringConfigElement.class);
        this.addConfigMapping(Map.class, Dom4jXmlMapConfigElement.class);
        this.addConfigMapping(Map.Entry.class, Dom4jXmlMapEntryConfigElement.class);
        this.addConfigMapping(List.class, Dom4jXmlListConfigElement.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void saveDocumentTo(Document doc, String folder, String fileName) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        File file = new File(folder, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter writeOut = new OutputStreamWriter((OutputStream)new FileOutputStream(file), "UTF-8");
        try (XMLWriter writer = new XMLWriter((Writer)writeOut, format);){
            writer.write(doc);
        }
    }

    private static void setFeature(SAXReader xmlReader, String name, boolean value) {
        try {
            xmlReader.setFeature(name, value);
        }
        catch (SAXException e) {
            privateLog.warn("Unable to set " + name + " to " + value);
        }
    }

    public Document loadDocument(File xmlFile) throws DocumentException, MalformedURLException {
        SAXReader xmlReader = this.newSecureSaxReader();
        this.document = xmlReader.read(xmlFile);
        return this.document;
    }

    @Override
    public Object load(InputStream istream) throws ConfigurationException {
        try {
            return this.loadDocument(istream);
        }
        catch (DocumentException e) {
            throw new ConfigurationException("Failed to load Xml doc: " + e.getMessage(), e);
        }
    }

    public Document loadDocument(InputStream istream) throws DocumentException {
        SAXReader xmlReader = this.newSecureSaxReader();
        this.document = xmlReader.read(istream);
        return this.document;
    }

    @Override
    public void save(String configPath, String configFile) throws ConfigurationException {
        this.saveDocument(configPath, configFile);
    }

    public void saveDocument(String configPath, String configFile) throws ConfigurationException {
        try {
            this.saveDocumentAtomically(this.getDocument(), configPath, configFile);
        }
        catch (IOException e) {
            throw new ConfigurationException("Couldn't save " + configFile + " to " + configPath + " directory.", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveDocumentAtomically(Document document, String configPath, String configFile) throws IOException {
        File tempFile = File.createTempFile(configFile, "tmp", new File(configPath));
        File saveFile = new File(configPath, configFile);
        try {
            AbstractDom4jXmlConfigurationPersister.saveDocumentTo(document, configPath, tempFile.getName());
            if (!tempFile.renameTo(saveFile)) {
                String nonAtomicWriteWarning = "Unable to move " + tempFile.getCanonicalPath() + " to " + saveFile.getCanonicalPath() + ". Falling back to non-atomic overwrite.";
                if (SystemUtils.IS_OS_WINDOWS) {
                    privateLog.debug(nonAtomicWriteWarning);
                } else {
                    privateLog.warn(nonAtomicWriteWarning);
                }
                AbstractDom4jXmlConfigurationPersister.saveDocumentTo(document, configPath, configFile);
            }
        }
        finally {
            tempFile.delete();
        }
    }

    public Document getDocument() {
        return this.document;
    }

    @Override
    public Object getRootContext() {
        return this.document.getRootElement();
    }

    public Element getElement(String path) {
        return DocumentHelper.makeElement((Branch)this.document, (String)path);
    }

    @Override
    public void clear() {
        this.clearDocument();
    }

    private void clearDocument() {
        this.document = null;
        this.document = DocumentHelper.createDocument();
        this.document.addElement(this.getRootName());
    }

    public abstract String getRootName();

    public boolean isUseCData() {
        return this.useCData;
    }

    public void setUseCData(boolean useCData) {
        this.useCData = useCData;
    }

    private SAXReader newSecureSaxReader() {
        SAXReader saxReader = new SAXReader();
        AbstractDom4jXmlConfigurationPersister.setFeature(saxReader, "http://xml.org/sax/features/external-parameter-entities", false);
        AbstractDom4jXmlConfigurationPersister.setFeature(saxReader, "http://xml.org/sax/features/external-general-entities", false);
        AbstractDom4jXmlConfigurationPersister.setFeature(saxReader, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        AbstractDom4jXmlConfigurationPersister.setFeature(saxReader, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        return saxReader;
    }
}

