/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.dom4j.io.OutputFormat
 *  org.dom4j.io.XMLWriter
 *  org.dom4j.util.NonLazyElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.output;

import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.salext.output.PropertiesStoreParser;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.NonLazyElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlSupportDataFormatter {
    private static final Logger LOG = LoggerFactory.getLogger(XmlSupportDataFormatter.class);
    private static final OutputFormat XML_OUTPUT_FORMAT = OutputFormat.createPrettyPrint();

    private static String toXmlString(Document document) {
        StringWriter stringWriter = new StringWriter();
        try {
            XMLWriter xmlWriter = new XMLWriter((Writer)stringWriter, XML_OUTPUT_FORMAT);
            xmlWriter.write(document);
        }
        catch (Exception e) {
            LOG.error("Couldn't write XML output", (Throwable)e);
        }
        return stringWriter.toString();
    }

    public String getFormattedProperties(PropertyStore properties, Properties propertyNameMappings) {
        Document doc = DocumentHelper.createDocument();
        NonLazyElement root = new NonLazyElement("properties");
        doc.setRootElement((Element)root);
        PropertiesStoreParser.loadStore(properties, (Element)root, propertyNameMappings);
        return XmlSupportDataFormatter.toXmlString(doc);
    }
}

