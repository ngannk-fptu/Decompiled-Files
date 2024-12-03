/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Writable;
import groovy.util.Node;
import groovy.util.XmlNodePrinter;
import groovy.util.slurpersupport.GPathResult;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XmlUtil {
    public static String serialize(Element element) {
        StringWriter sw = new StringWriter();
        XmlUtil.serialize((Source)new DOMSource(element), (Writer)sw);
        return sw.toString();
    }

    public static void serialize(Element element, OutputStream os) {
        DOMSource source = new DOMSource(element);
        XmlUtil.serialize((Source)source, os);
    }

    public static void serialize(Element element, Writer w) {
        DOMSource source = new DOMSource(element);
        XmlUtil.serialize((Source)source, w);
    }

    public static String serialize(Node node) {
        return XmlUtil.serialize(XmlUtil.asString(node));
    }

    public static void serialize(Node node, OutputStream os) {
        XmlUtil.serialize(XmlUtil.asString(node), os);
    }

    public static void serialize(Node node, Writer w) {
        XmlUtil.serialize(XmlUtil.asString(node), w);
    }

    public static String serialize(GPathResult node) {
        return XmlUtil.serialize(XmlUtil.asString(node));
    }

    public static void serialize(GPathResult node, OutputStream os) {
        XmlUtil.serialize(XmlUtil.asString(node), os);
    }

    public static void serialize(GPathResult node, Writer w) {
        XmlUtil.serialize(XmlUtil.asString(node), w);
    }

    public static String serialize(Writable writable) {
        return XmlUtil.serialize(XmlUtil.asString(writable));
    }

    public static void serialize(Writable writable, OutputStream os) {
        XmlUtil.serialize(XmlUtil.asString(writable), os);
    }

    public static void serialize(Writable writable, Writer w) {
        XmlUtil.serialize(XmlUtil.asString(writable), w);
    }

    public static String serialize(String xmlString) {
        StringWriter sw = new StringWriter();
        XmlUtil.serialize((Source)XmlUtil.asStreamSource(xmlString), (Writer)sw);
        return sw.toString();
    }

    public static void serialize(String xmlString, OutputStream os) {
        XmlUtil.serialize((Source)XmlUtil.asStreamSource(xmlString), os);
    }

    public static void serialize(String xmlString, Writer w) {
        XmlUtil.serialize((Source)XmlUtil.asStreamSource(xmlString), w);
    }

    public static SAXParser newSAXParser(String schemaLanguage, Source ... schemas) throws SAXException, ParserConfigurationException {
        return XmlUtil.newSAXParser(schemaLanguage, true, false, schemas);
    }

    public static SAXParser newSAXParser(String schemaLanguage, boolean namespaceAware, boolean validating, Source ... schemas) throws SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);
        if (schemas.length != 0) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
            factory.setSchema(schemaFactory.newSchema(schemas));
        }
        SAXParser saxParser = factory.newSAXParser();
        if (schemas.length == 0) {
            saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", schemaLanguage);
        }
        return saxParser;
    }

    public static SAXParser newSAXParser(String schemaLanguage, File schema) throws SAXException, ParserConfigurationException {
        return XmlUtil.newSAXParser(schemaLanguage, true, false, schema);
    }

    public static SAXParser newSAXParser(String schemaLanguage, boolean namespaceAware, boolean validating, File schema) throws SAXException, ParserConfigurationException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
        return XmlUtil.newSAXParser(namespaceAware, validating, schemaFactory.newSchema(schema));
    }

    public static SAXParser newSAXParser(String schemaLanguage, URL schema) throws SAXException, ParserConfigurationException {
        return XmlUtil.newSAXParser(schemaLanguage, true, false, schema);
    }

    public static SAXParser newSAXParser(String schemaLanguage, boolean namespaceAware, boolean validating, URL schema) throws SAXException, ParserConfigurationException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(schemaLanguage);
        return XmlUtil.newSAXParser(namespaceAware, validating, schemaFactory.newSchema(schema));
    }

    public static String escapeXml(String orig) {
        return StringGroovyMethods.collectReplacements(orig, new Closure<String>(null){

            public String doCall(Character arg) {
                switch (arg.charValue()) {
                    case '&': {
                        return "&amp;";
                    }
                    case '<': {
                        return "&lt;";
                    }
                    case '>': {
                        return "&gt;";
                    }
                    case '\"': {
                        return "&quot;";
                    }
                    case '\'': {
                        return "&apos;";
                    }
                }
                return null;
            }
        });
    }

    public static String escapeControlCharacters(String orig) {
        return StringGroovyMethods.collectReplacements(orig, new Closure<String>(null){

            public String doCall(Character arg) {
                if (arg.charValue() < '\u001f') {
                    return "&#" + arg.charValue() + ";";
                }
                return null;
            }
        });
    }

    private static SAXParser newSAXParser(boolean namespaceAware, boolean validating, Schema schema1) throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);
        factory.setSchema(schema1);
        return factory.newSAXParser();
    }

    private static String asString(Node node) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        XmlNodePrinter nodePrinter = new XmlNodePrinter(pw);
        nodePrinter.setPreserveWhitespace(true);
        nodePrinter.print(node);
        return sw.toString();
    }

    private static String asString(GPathResult node) {
        try {
            Object builder = Class.forName("groovy.xml.StreamingMarkupBuilder").newInstance();
            InvokerHelper.setProperty(builder, "encoding", "UTF-8");
            Writable w = (Writable)InvokerHelper.invokeMethod(builder, "bindNode", node);
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + w.toString();
        }
        catch (Exception e) {
            return "Couldn't convert node to string because: " + e.getMessage();
        }
    }

    private static String asString(Writable writable) {
        if (writable instanceof GPathResult) {
            return XmlUtil.asString((GPathResult)writable);
        }
        StringWriter sw = new StringWriter();
        try {
            writable.writeTo(sw);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return ((Object)sw).toString();
    }

    private static StreamSource asStreamSource(String xmlString) {
        return new StreamSource(new StringReader(xmlString));
    }

    private static void serialize(Source source, OutputStream os) {
        try {
            XmlUtil.serialize(source, new StreamResult(new OutputStreamWriter(os, "UTF-8")));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
    }

    private static void serialize(Source source, Writer w) {
        XmlUtil.serialize(source, new StreamResult(w));
    }

    private static void serialize(Source source, StreamResult target) {
        TransformerFactory factory = TransformerFactory.newInstance();
        XmlUtil.setIndent(factory, 2);
        try {
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("method", "xml");
            transformer.setOutputProperty("media-type", "text/xml");
            transformer.transform(source, target);
        }
        catch (TransformerException e) {
            throw new GroovyRuntimeException(e.getMessage());
        }
    }

    private static void setIndent(TransformerFactory factory, int indent) {
        try {
            factory.setAttribute("indent-number", indent);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }
}

