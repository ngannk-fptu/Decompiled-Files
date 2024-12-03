/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.dom4j.Namespace
 *  org.dom4j.QName
 *  org.dom4j.io.OutputFormat
 *  org.dom4j.io.XMLWriter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SpringHelper {
    private static final Logger log = LoggerFactory.getLogger(SpringHelper.class);

    SpringHelper() {
    }

    static Document createSpringDocument() {
        Document springDoc = DocumentHelper.createDocument();
        Element root = springDoc.addElement("beans");
        root.addNamespace("beans", "http://www.springframework.org/schema/beans");
        root.addNamespace("osgi", "http://www.eclipse.org/gemini/blueprint/schema/blueprint");
        root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addAttribute(new QName("schemaLocation", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")), "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\nhttp://www.eclipse.org/gemini/blueprint/schema/blueprint http://www.eclipse.org/gemini/blueprint/schema/blueprint/gemini-blueprint.xsd");
        root.setName("beans:beans");
        root.addAttribute("default-autowire", "constructor");
        root.addAttribute("osgi:default-timeout", "30000");
        return springDoc;
    }

    static byte[] documentToBytes(Document doc) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            XMLWriter writer = new XMLWriter((OutputStream)bout, format);
            writer.write(doc);
        }
        catch (IOException e) {
            throw new PluginTransformationException("Unable to print generated Spring XML", e);
        }
        return bout.toByteArray();
    }

    static boolean shouldGenerateFile(TransformContext context, String path) {
        if (context.getPluginJarEntry(path) == null) {
            log.debug("File {} not present, generating", (Object)path);
            return true;
        }
        log.debug("File {} already exists in jar, skipping generation", (Object)path);
        return false;
    }
}

