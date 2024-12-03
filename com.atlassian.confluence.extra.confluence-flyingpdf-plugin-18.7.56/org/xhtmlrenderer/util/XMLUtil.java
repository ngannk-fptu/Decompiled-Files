/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLUtil {
    public static Document documentFromString(String documentContents) throws Exception {
        return XMLUtil.createDocumentBuilder().parse(new InputSource(new StringReader(documentContents)));
    }

    public static Document documentFromFile(String filename) throws Exception {
        return XMLUtil.createDocumentBuilder().parse(new File(filename).toURI().toURL().openStream());
    }

    private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = fact.newDocumentBuilder();
        builder.setErrorHandler(null);
        return builder;
    }
}

