/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class SAXHelper {
    private static final Logger LOG = LogManager.getLogger(SAXHelper.class);
    private static long lastLog;
    public static final EntityResolver IGNORING_ENTITY_RESOLVER;

    private SAXHelper() {
    }

    public static XMLReader newXMLReader(XmlOptions options) throws SAXException, ParserConfigurationException {
        XMLReader xmlReader = SAXHelper.saxFactory(options).newSAXParser().getXMLReader();
        xmlReader.setEntityResolver(IGNORING_ENTITY_RESOLVER);
        SAXHelper.trySetSAXFeature(xmlReader, "http://javax.xml.XMLConstants/feature/secure-processing");
        SAXHelper.trySetXercesSecurityManager(xmlReader, options);
        return xmlReader;
    }

    static SAXParserFactory saxFactory() {
        return SAXHelper.saxFactory(new XmlOptions());
    }

    static SAXParserFactory saxFactory(XmlOptions options) {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(false);
        saxFactory.setNamespaceAware(true);
        SAXHelper.trySetSAXFeature(saxFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        SAXHelper.trySetSAXFeature(saxFactory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", options.isLoadDTDGrammar());
        SAXHelper.trySetSAXFeature(saxFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", options.isLoadExternalDTD());
        SAXHelper.trySetSAXFeature(saxFactory, "http://apache.org/xml/features/disallow-doctype-decl", options.disallowDocTypeDeclaration());
        return saxFactory;
    }

    private static void trySetSAXFeature(SAXParserFactory spf, String feature, boolean flag) {
        try {
            spf.setFeature(feature, flag);
        }
        catch (Exception e) {
            LOG.atWarn().withThrowable(e).log("SAX Feature unsupported: {}", (Object)feature);
        }
        catch (AbstractMethodError ame) {
            LOG.atWarn().withThrowable(ame).log("Cannot set SAX feature {} because outdated XML parser in classpath", (Object)feature);
        }
    }

    private static void trySetSAXFeature(XMLReader xmlReader, String feature) {
        try {
            xmlReader.setFeature(feature, true);
        }
        catch (Exception e) {
            LOG.atWarn().withThrowable(e).log("SAX Feature unsupported: {}", (Object)feature);
        }
        catch (AbstractMethodError ame) {
            LOG.atWarn().withThrowable(ame).log("Cannot set SAX feature {} because outdated XML parser in classpath", (Object)feature);
        }
    }

    private static void trySetXercesSecurityManager(XMLReader xmlReader, XmlOptions options) {
        block7: {
            for (String securityManagerClassName : new String[]{"org.apache.xerces.util.SecurityManager"}) {
                Class<?> clazz;
                try {
                    clazz = Class.forName(securityManagerClassName);
                }
                catch (Throwable e) {
                    continue;
                }
                try {
                    Object mgr = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    Method setLimit = clazz.getMethod("setEntityExpansionLimit", Integer.TYPE);
                    setLimit.invoke(mgr, options.getEntityExpansionLimit());
                    xmlReader.setProperty("http://apache.org/xml/properties/security-manager", mgr);
                    return;
                }
                catch (Throwable e) {
                    if (System.currentTimeMillis() <= lastLog + TimeUnit.MINUTES.toMillis(5L)) continue;
                    LOG.atWarn().withThrowable(e).log("SAX Security Manager could not be setup [log suppressed for 5 minutes]");
                    lastLog = System.currentTimeMillis();
                }
            }
            try {
                xmlReader.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", options.getEntityExpansionLimit());
            }
            catch (SAXException e) {
                if (System.currentTimeMillis() <= lastLog + TimeUnit.MINUTES.toMillis(5L)) break block7;
                LOG.atWarn().withThrowable(e).log("SAX Security Manager could not be setup [log suppressed for 5 minutes]");
                lastLog = System.currentTimeMillis();
            }
        }
    }

    static {
        IGNORING_ENTITY_RESOLVER = (publicId, systemId) -> new InputSource(new StringReader(""));
    }
}

