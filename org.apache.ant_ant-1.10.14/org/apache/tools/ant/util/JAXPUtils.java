/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class JAXPUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static SAXParserFactory parserFactory = null;
    private static SAXParserFactory nsParserFactory = null;
    private static DocumentBuilderFactory builderFactory = null;

    public static synchronized SAXParserFactory getParserFactory() throws BuildException {
        if (parserFactory == null) {
            parserFactory = JAXPUtils.newParserFactory();
        }
        return parserFactory;
    }

    public static synchronized SAXParserFactory getNSParserFactory() throws BuildException {
        if (nsParserFactory == null) {
            nsParserFactory = JAXPUtils.newParserFactory();
            nsParserFactory.setNamespaceAware(true);
        }
        return nsParserFactory;
    }

    public static SAXParserFactory newParserFactory() throws BuildException {
        try {
            return SAXParserFactory.newInstance();
        }
        catch (FactoryConfigurationError e) {
            throw new BuildException("XML parser factory has not been configured correctly: " + e.getMessage(), e);
        }
    }

    public static Parser getParser() throws BuildException {
        try {
            return JAXPUtils.newSAXParser(JAXPUtils.getParserFactory()).getParser();
        }
        catch (SAXException e) {
            throw JAXPUtils.convertToBuildException(e);
        }
    }

    public static XMLReader getXMLReader() throws BuildException {
        try {
            return JAXPUtils.newSAXParser(JAXPUtils.getParserFactory()).getXMLReader();
        }
        catch (SAXException e) {
            throw JAXPUtils.convertToBuildException(e);
        }
    }

    public static XMLReader getNamespaceXMLReader() throws BuildException {
        try {
            return JAXPUtils.newSAXParser(JAXPUtils.getNSParserFactory()).getXMLReader();
        }
        catch (SAXException e) {
            throw JAXPUtils.convertToBuildException(e);
        }
    }

    public static String getSystemId(File file) {
        return FILE_UTILS.toURI(file.getAbsolutePath());
    }

    public static DocumentBuilder getDocumentBuilder() throws BuildException {
        try {
            return JAXPUtils.getDocumentBuilderFactory().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new BuildException(e);
        }
    }

    private static SAXParser newSAXParser(SAXParserFactory factory) throws BuildException {
        try {
            return factory.newSAXParser();
        }
        catch (ParserConfigurationException e) {
            throw new BuildException("Cannot create parser for the given configuration: " + e.getMessage(), e);
        }
        catch (SAXException e) {
            throw JAXPUtils.convertToBuildException(e);
        }
    }

    private static BuildException convertToBuildException(SAXException e) {
        Exception nested = e.getException();
        if (nested != null) {
            return new BuildException(nested);
        }
        return new BuildException(e);
    }

    private static synchronized DocumentBuilderFactory getDocumentBuilderFactory() throws BuildException {
        if (builderFactory == null) {
            try {
                builderFactory = DocumentBuilderFactory.newInstance();
            }
            catch (FactoryConfigurationError e) {
                throw new BuildException("Document builder factory has not been configured correctly: " + e.getMessage(), e);
            }
        }
        return builderFactory;
    }
}

