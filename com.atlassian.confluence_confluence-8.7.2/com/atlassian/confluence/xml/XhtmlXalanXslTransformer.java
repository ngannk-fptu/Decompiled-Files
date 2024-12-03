/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xml;

import com.atlassian.confluence.xml.XalanXslTransformer;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XhtmlXalanXslTransformer
extends XalanXslTransformer {
    private static final Logger log = LoggerFactory.getLogger(XhtmlXalanXslTransformer.class);
    private final EntityResolver entityResolver;
    private final SAXParserFactory saxParserFactory;

    public XhtmlXalanXslTransformer(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
        this.saxParserFactory = XhtmlXalanXslTransformer.createSAXParserFactory();
    }

    @Override
    protected Source createInputSource(Reader xhtml) {
        try {
            SAXParser sp = this.saxParserFactory.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            xmlReader.setEntityResolver(this.entityResolver);
            return new SAXSource(xmlReader, new InputSource(xhtml));
        }
        catch (ParserConfigurationException ex) {
            log.warn("Could not create a SAXParser due to configuration problems.", (Throwable)ex);
        }
        catch (SAXException ex) {
            log.warn("Exception occurred while trying to create a SAXSource for the supplied XHTML Stream.", (Throwable)ex);
        }
        return null;
    }

    private static SAXParserFactory createSAXParserFactory() {
        SAXParserFactory spf = null;
        try {
            spf = SAXParserFactory.newInstance();
            spf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setValidating(false);
            spf.setNamespaceAware(true);
        }
        catch (ParserConfigurationException ex) {
            log.warn("Could not create a SAXParserFactory due to configuration problems.", (Throwable)ex);
        }
        catch (SAXException ex) {
            log.warn("Exception occurred while trying to create a SAXParserFactory.", (Throwable)ex);
        }
        return spf;
    }
}

