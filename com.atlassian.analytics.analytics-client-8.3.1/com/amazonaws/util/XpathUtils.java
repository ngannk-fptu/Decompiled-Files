/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.util;

import com.amazonaws.internal.SdkThreadLocalsRegistry;
import com.amazonaws.util.Base64;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.NamespaceRemovingInputStream;
import com.amazonaws.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XpathUtils {
    private static final String DTM_MANAGER_DEFAULT_PROP_NAME = "com.sun.org.apache.xml.internal.dtm.DTMManager";
    private static final String DOCUMENT_BUILDER_FACTORY_PROP_NAME = "javax.xml.parsers.DocumentBuilderFactory";
    private static final String DOCUMENT_BUILDER_FACTORY_IMPL_CLASS_NAME = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
    private static final String XPATH_CONTEXT_CLASS_NAME = "com.sun.org.apache.xpath.internal.XPathContext";
    private static final String DTM_MANAGER_IMPL_CLASS_NAME = "com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault";
    private static final Log log = LogFactory.getLog(XpathUtils.class);
    private static final ErrorHandler ERROR_HANDLER = new ErrorHandler(){

        @Override
        public void warning(SAXParseException e) throws SAXException {
            if (log.isDebugEnabled()) {
                log.debug((Object)("xml parse warning: " + e.getMessage()), (Throwable)e);
            }
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            if (log.isDebugEnabled()) {
                log.debug((Object)("xml parse error: " + e.getMessage()), (Throwable)e);
            }
        }
    };
    private static volatile DocumentBuilderInfo cachedDocumentBuilderInfo = null;
    private static final ThreadLocal<XPathFactory> X_PATH_FACTORY = SdkThreadLocalsRegistry.register(new ThreadLocal<XPathFactory>(){

        @Override
        protected XPathFactory initialValue() {
            return XPathFactory.newInstance();
        }
    });

    private static void speedUpDTMManager() throws Exception {
        Object XPathContext;
        Class<?> XPathContextClass;
        Method getDTMManager;
        Object dtmManager;
        if (System.getProperty(DTM_MANAGER_DEFAULT_PROP_NAME) == null && DTM_MANAGER_IMPL_CLASS_NAME.equals((dtmManager = (getDTMManager = (XPathContextClass = Class.forName(XPATH_CONTEXT_CLASS_NAME)).getMethod("getDTMManager", new Class[0])).invoke(XPathContext = XPathContextClass.newInstance(), new Object[0])).getClass().getName())) {
            System.setProperty(DTM_MANAGER_DEFAULT_PROP_NAME, DTM_MANAGER_IMPL_CLASS_NAME);
        }
    }

    private static void speedUpDcoumentBuilderFactory() {
        DocumentBuilderFactory factory;
        if (System.getProperty(DOCUMENT_BUILDER_FACTORY_PROP_NAME) == null && DOCUMENT_BUILDER_FACTORY_IMPL_CLASS_NAME.equals((factory = DocumentBuilderFactory.newInstance()).getClass().getName())) {
            System.setProperty(DOCUMENT_BUILDER_FACTORY_PROP_NAME, DOCUMENT_BUILDER_FACTORY_IMPL_CLASS_NAME);
        }
    }

    public static XPath xpath() {
        return X_PATH_FACTORY.get().newXPath();
    }

    public static Document documentFrom(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        is = new NamespaceRemovingInputStream(is);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        XpathUtils.configureDocumentBuilderFactory(factory);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(ERROR_HANDLER);
        Document doc = builder.parse(is);
        is.close();
        return doc;
    }

    public static Document documentFrom(String xml) throws SAXException, IOException, ParserConfigurationException {
        return XpathUtils.documentFrom(new ByteArrayInputStream(xml.getBytes(StringUtils.UTF8)));
    }

    public static Document documentFrom(URL url) throws SAXException, IOException, ParserConfigurationException {
        return XpathUtils.documentFrom(url.openStream());
    }

    public static Double asDouble(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asDouble(expression, node, XpathUtils.xpath());
    }

    public static Double asDouble(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String doubleString = XpathUtils.evaluateAsString(expression, node, xpath);
        return XpathUtils.isEmptyString(doubleString) ? null : Double.valueOf(Double.parseDouble(doubleString));
    }

    public static String asString(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.evaluateAsString(expression, node, XpathUtils.xpath());
    }

    public static String asString(String expression, Node node, XPath xpath) throws XPathExpressionException {
        return XpathUtils.evaluateAsString(expression, node, xpath);
    }

    public static Integer asInteger(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asInteger(expression, node, XpathUtils.xpath());
    }

    public static Integer asInteger(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String intString = XpathUtils.evaluateAsString(expression, node, xpath);
        return XpathUtils.isEmptyString(intString) ? null : Integer.valueOf(Integer.parseInt(intString));
    }

    public static Boolean asBoolean(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asBoolean(expression, node, XpathUtils.xpath());
    }

    public static Boolean asBoolean(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String booleanString = XpathUtils.evaluateAsString(expression, node, xpath);
        return XpathUtils.isEmptyString(booleanString) ? null : Boolean.valueOf(Boolean.parseBoolean(booleanString));
    }

    public static Float asFloat(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asFloat(expression, node, XpathUtils.xpath());
    }

    public static Float asFloat(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String floatString = XpathUtils.evaluateAsString(expression, node, xpath);
        return XpathUtils.isEmptyString(floatString) ? null : Float.valueOf(floatString);
    }

    public static Long asLong(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asLong(expression, node, XpathUtils.xpath());
    }

    public static Long asLong(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String longString = XpathUtils.evaluateAsString(expression, node, xpath);
        return XpathUtils.isEmptyString(longString) ? null : Long.valueOf(Long.parseLong(longString));
    }

    public static Byte asByte(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asByte(expression, node, XpathUtils.xpath());
    }

    public static Byte asByte(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String byteString = XpathUtils.evaluateAsString(expression, node, xpath);
        return XpathUtils.isEmptyString(byteString) ? null : Byte.valueOf(byteString);
    }

    public static Date asDate(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asDate(expression, node, XpathUtils.xpath());
    }

    public static Date asDate(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String dateString = XpathUtils.evaluateAsString(expression, node, xpath);
        if (XpathUtils.isEmptyString(dateString)) {
            return null;
        }
        try {
            return DateUtils.parseISO8601Date(dateString);
        }
        catch (Exception e) {
            log.warn((Object)("Unable to parse date '" + dateString + "':  " + e.getMessage()), (Throwable)e);
            return null;
        }
    }

    public static ByteBuffer asByteBuffer(String expression, Node node) throws XPathExpressionException {
        return XpathUtils.asByteBuffer(expression, node, XpathUtils.xpath());
    }

    public static ByteBuffer asByteBuffer(String expression, Node node, XPath xpath) throws XPathExpressionException {
        String base64EncodedString = XpathUtils.evaluateAsString(expression, node, xpath);
        if (XpathUtils.isEmptyString(base64EncodedString)) {
            return null;
        }
        if (!XpathUtils.isEmpty(node)) {
            byte[] decodedBytes = Base64.decode(base64EncodedString);
            return ByteBuffer.wrap(decodedBytes);
        }
        return null;
    }

    public static boolean isEmpty(Node node) {
        return node == null;
    }

    public static Node asNode(String nodeName, Node node) throws XPathExpressionException {
        return XpathUtils.asNode(nodeName, node, XpathUtils.xpath());
    }

    public static Node asNode(String nodeName, Node node, XPath xpath) throws XPathExpressionException {
        if (node == null) {
            return null;
        }
        return (Node)xpath.evaluate(nodeName, node, XPathConstants.NODE);
    }

    public static int nodeLength(NodeList list) {
        return list == null ? 0 : list.getLength();
    }

    private static String evaluateAsString(String expression, Node node, XPath xpath) throws XPathExpressionException {
        if (XpathUtils.isEmpty(node)) {
            return null;
        }
        if (!expression.equals(".") && XpathUtils.asNode(expression, node, xpath) == null) {
            return null;
        }
        String s = xpath.evaluate(expression, node);
        return s.trim();
    }

    private static boolean isEmptyString(String s) {
        return s == null || s.trim().length() == 0;
    }

    private static void configureDocumentBuilderFactory(DocumentBuilderFactory factory) {
        block6: {
            DocumentBuilderInfo cache = cachedDocumentBuilderInfo;
            if (cache != null && cache.clzz.equals(factory.getClass())) {
                if (cache.xxeMitigationSuccessful) {
                    try {
                        if (XpathUtils.isXerces(cache.canonicalName)) {
                            XpathUtils.configureXercesFactory(factory);
                            break block6;
                        }
                        XpathUtils.configureGenericFactory(factory);
                    }
                    catch (Throwable t) {
                        log.warn((Object)"Unable to configure DocumentBuilderFactory to protect against XXE attacks", t);
                    }
                }
            } else {
                XpathUtils.initialConfigureDocumentBuilderFactory(factory);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void initialConfigureDocumentBuilderFactory(DocumentBuilderFactory factory) {
        Class<XpathUtils> clazz = XpathUtils.class;
        synchronized (XpathUtils.class) {
            boolean xxeMitigationSuccessful;
            Class<?> clzz = factory.getClass();
            String canonicalName = clzz.getCanonicalName();
            try {
                if (XpathUtils.isXerces(canonicalName)) {
                    XpathUtils.configureXercesFactory(factory);
                } else {
                    XpathUtils.configureGenericFactory(factory);
                }
                xxeMitigationSuccessful = true;
            }
            catch (Throwable t) {
                log.warn((Object)"Unable to configure DocumentBuilderFactory to protect against XXE attacks", t);
                xxeMitigationSuccessful = false;
            }
            cachedDocumentBuilderInfo = new DocumentBuilderInfo(clzz, canonicalName, xxeMitigationSuccessful);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    private static boolean isXerces(String canonicalName) {
        return canonicalName.startsWith("org.apache.xerces.") || canonicalName.startsWith("com.sun.org.apache.xerces.");
    }

    private static void configureXercesFactory(DocumentBuilderFactory factory) throws ParserConfigurationException {
        XpathUtils.commonConfigureFactory(factory);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    }

    private static void configureGenericFactory(DocumentBuilderFactory factory) throws ParserConfigurationException {
        XpathUtils.commonConfigureFactory(factory);
        factory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        factory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
    }

    private static void commonConfigureFactory(DocumentBuilderFactory factory) throws ParserConfigurationException {
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
    }

    static {
        try {
            XpathUtils.speedUpDcoumentBuilderFactory();
        }
        catch (Throwable t) {
            log.debug((Object)"Ingore failure in speeding up DocumentBuilderFactory", t);
        }
        try {
            XpathUtils.speedUpDTMManager();
        }
        catch (Throwable t) {
            log.debug((Object)"Ingore failure in speeding up DTMManager", t);
        }
    }

    private static class DocumentBuilderInfo {
        private final Class<?> clzz;
        private final String canonicalName;
        private final boolean xxeMitigationSuccessful;

        private DocumentBuilderInfo(Class<?> clzz, String canonicalName, boolean xxeMitigationSuccessful) {
            this.clzz = clzz;
            this.canonicalName = canonicalName;
            this.xxeMitigationSuccessful = xxeMitigationSuccessful;
        }
    }
}

