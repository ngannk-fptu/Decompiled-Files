/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.SAXHelper;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class DocumentHelper {
    private static final Logger LOG = LogManager.getLogger(DocumentHelper.class);
    private static long lastLog;
    private static final DocumentBuilder documentBuilderSingleton;

    private DocumentHelper() {
    }

    public static DocumentBuilder newDocumentBuilder(XmlOptions xmlOptions) {
        try {
            DocumentBuilder documentBuilder = DocumentHelper.documentBuilderFactory(xmlOptions).newDocumentBuilder();
            documentBuilder.setEntityResolver(SAXHelper.IGNORING_ENTITY_RESOLVER);
            documentBuilder.setErrorHandler(new DocHelperErrorHandler());
            return documentBuilder;
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException("cannot create a DocumentBuilder", e);
        }
    }

    private static DocumentBuilderFactory documentBuilderFactory(XmlOptions options) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentHelper.trySetFeature(documentBuilderFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        DocumentHelper.trySetFeature(documentBuilderFactory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", options.isLoadDTDGrammar());
        DocumentHelper.trySetFeature(documentBuilderFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", options.isLoadExternalDTD());
        DocumentHelper.trySetFeature(documentBuilderFactory, "http://apache.org/xml/features/disallow-doctype-decl", options.disallowDocTypeDeclaration());
        DocumentHelper.trySetXercesSecurityManager(documentBuilderFactory, options);
        return documentBuilderFactory;
    }

    private static void trySetFeature(DocumentBuilderFactory dbf, String feature, boolean enabled) {
        try {
            dbf.setFeature(feature, enabled);
        }
        catch (Exception e) {
            LOG.atWarn().withThrowable(e).log("SAX Feature unsupported: {}", (Object)feature);
        }
        catch (AbstractMethodError ame) {
            LOG.atWarn().withThrowable(ame).log("Cannot set SAX feature {} because of outdated XML parser in classpath", (Object)feature);
        }
    }

    private static void trySetXercesSecurityManager(DocumentBuilderFactory dbf, XmlOptions options) {
        block6: {
            for (String securityManagerClassName : new String[]{"org.apache.xerces.util.SecurityManager"}) {
                try {
                    Object mgr = Class.forName(securityManagerClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                    setLimit.invoke(mgr, options.getEntityExpansionLimit());
                    dbf.setAttribute("http://apache.org/xml/properties/security-manager", mgr);
                    return;
                }
                catch (ClassNotFoundException mgr) {
                }
                catch (Throwable e) {
                    if (System.currentTimeMillis() <= lastLog + TimeUnit.MINUTES.toMillis(5L)) continue;
                    LOG.atWarn().withThrowable(e).log("DocumentBuilderFactory Security Manager could not be setup [log suppressed for 5 minutes]");
                    lastLog = System.currentTimeMillis();
                }
            }
            try {
                dbf.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", options.getEntityExpansionLimit());
            }
            catch (Throwable e) {
                if (System.currentTimeMillis() <= lastLog + TimeUnit.MINUTES.toMillis(5L)) break block6;
                LOG.atWarn().withThrowable(e).log("DocumentBuilderFactory Entity Expansion Limit could not be setup [log suppressed for 5 minutes]");
                lastLog = System.currentTimeMillis();
            }
        }
    }

    public static Document readDocument(XmlOptions xmlOptions, InputStream inp) throws IOException, SAXException {
        return DocumentHelper.newDocumentBuilder(xmlOptions).parse(inp);
    }

    public static Document readDocument(XmlOptions xmlOptions, InputSource inp) throws IOException, SAXException {
        return DocumentHelper.newDocumentBuilder(xmlOptions).parse(inp);
    }

    public static Document createDocument() {
        return documentBuilderSingleton.newDocument();
    }

    static {
        documentBuilderSingleton = DocumentHelper.newDocumentBuilder(new XmlOptions());
    }

    private static class DocHelperErrorHandler
    implements ErrorHandler {
        private DocHelperErrorHandler() {
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            LOG.atWarn().withThrowable(exception).log(this.asString(exception));
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            LOG.atError().withThrowable(exception).log(this.asString(exception));
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            LOG.atFatal().withThrowable(exception).log(this.asString(exception));
            throw exception;
        }

        private String asString(SAXParseException ex) {
            StringBuilder sb = new StringBuilder();
            String systemId = ex.getSystemId();
            if (systemId != null) {
                int index = systemId.lastIndexOf(47);
                if (index != -1) {
                    systemId = systemId.substring(index + 1);
                }
                sb.append(systemId);
            }
            sb.append(':');
            sb.append(ex.getLineNumber());
            sb.append(':');
            sb.append(ex.getColumnNumber());
            sb.append(": ");
            sb.append(ex.getMessage());
            return sb.toString();
        }
    }
}

