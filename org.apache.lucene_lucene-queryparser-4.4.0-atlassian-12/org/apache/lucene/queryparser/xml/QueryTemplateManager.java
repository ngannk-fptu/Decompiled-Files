/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class QueryTemplateManager {
    static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    static final TransformerFactory tFactory = TransformerFactory.newInstance();
    HashMap<String, Templates> compiledTemplatesCache = new HashMap();
    Templates defaultCompiledTemplates = null;

    public QueryTemplateManager() {
    }

    public QueryTemplateManager(InputStream xslIs) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        this.addDefaultQueryTemplate(xslIs);
    }

    public void addDefaultQueryTemplate(InputStream xslIs) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        this.defaultCompiledTemplates = QueryTemplateManager.getTemplates(xslIs);
    }

    public void addQueryTemplate(String name, InputStream xslIs) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        this.compiledTemplatesCache.put(name, QueryTemplateManager.getTemplates(xslIs));
    }

    public String getQueryAsXmlString(Properties formProperties, String queryTemplateName) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        Templates ts = this.compiledTemplatesCache.get(queryTemplateName);
        return QueryTemplateManager.getQueryAsXmlString(formProperties, ts);
    }

    public Document getQueryAsDOM(Properties formProperties, String queryTemplateName) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        Templates ts = this.compiledTemplatesCache.get(queryTemplateName);
        return QueryTemplateManager.getQueryAsDOM(formProperties, ts);
    }

    public String getQueryAsXmlString(Properties formProperties) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        return QueryTemplateManager.getQueryAsXmlString(formProperties, this.defaultCompiledTemplates);
    }

    public Document getQueryAsDOM(Properties formProperties) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        return QueryTemplateManager.getQueryAsDOM(formProperties, this.defaultCompiledTemplates);
    }

    public static String getQueryAsXmlString(Properties formProperties, Templates template) throws ParserConfigurationException, TransformerException {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        QueryTemplateManager.transformCriteria(formProperties, template, (Result)result);
        return writer.toString();
    }

    public static String getQueryAsXmlString(Properties formProperties, InputStream xslIs) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        QueryTemplateManager.transformCriteria(formProperties, xslIs, (Result)result);
        return writer.toString();
    }

    public static Document getQueryAsDOM(Properties formProperties, Templates template) throws ParserConfigurationException, TransformerException {
        DOMResult result = new DOMResult();
        QueryTemplateManager.transformCriteria(formProperties, template, (Result)result);
        return (Document)result.getNode();
    }

    public static Document getQueryAsDOM(Properties formProperties, InputStream xslIs) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        DOMResult result = new DOMResult();
        QueryTemplateManager.transformCriteria(formProperties, xslIs, (Result)result);
        return (Document)result.getNode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void transformCriteria(Properties formProperties, InputStream xslIs, Result result) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document xslDoc = builder.parse(xslIs);
        DOMSource ds = new DOMSource(xslDoc);
        Transformer transformer = null;
        TransformerFactory transformerFactory = tFactory;
        synchronized (transformerFactory) {
            transformer = tFactory.newTransformer(ds);
        }
        QueryTemplateManager.transformCriteria(formProperties, transformer, result);
    }

    public static void transformCriteria(Properties formProperties, Templates template, Result result) throws ParserConfigurationException, TransformerException {
        QueryTemplateManager.transformCriteria(formProperties, template.newTransformer(), result);
    }

    public static void transformCriteria(Properties formProperties, Transformer transformer, Result result) throws ParserConfigurationException, TransformerException {
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("Document");
        doc.appendChild(root);
        Enumeration<Object> keysEnum = formProperties.keys();
        while (keysEnum.hasMoreElements()) {
            String propName = (String)keysEnum.nextElement();
            String value = formProperties.getProperty(propName);
            if (value == null || value.length() <= 0) continue;
            DOMUtils.insertChild(root, propName, value);
        }
        DOMSource xml = new DOMSource(doc);
        transformer.transform(xml, result);
    }

    public static Templates getTemplates(InputStream xslIs) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document xslDoc = builder.parse(xslIs);
        DOMSource ds = new DOMSource(xslDoc);
        return tFactory.newTemplates(ds);
    }
}

