/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRRuntimeException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class FSCatalog {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map parseCatalog(String catalogURI) {
        HashMap map = null;
        InputStream s = null;
        try {
            URL url = FSCatalog.class.getClassLoader().getResource(catalogURI);
            s = new BufferedInputStream(url.openStream());
            map = this.parseCatalog(new InputSource(s));
        }
        catch (Exception ex) {
            XRLog.xmlEntities(Level.WARNING, "Could not open XML catalog from URI '" + catalogURI + "'", ex);
            map = new HashMap();
        }
        finally {
            try {
                if (s != null) {
                    s.close();
                }
            }
            catch (IOException iOException) {}
        }
        return map;
    }

    public Map parseCatalog(InputSource inputSource) {
        XMLReader xmlReader = XMLResource.newXMLReader();
        CatalogContentHandler ch = new CatalogContentHandler();
        this.addHandlers(xmlReader, ch);
        this.setFeature(xmlReader, "http://xml.org/sax/features/validation", false);
        try {
            xmlReader.parse(inputSource);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed on configuring SAX to DOM transformer.", ex);
        }
        return ch.getEntityMap();
    }

    private void addHandlers(XMLReader xmlReader, ContentHandler ch) {
        try {
            xmlReader.setContentHandler(ch);
            xmlReader.setErrorHandler(new ErrorHandler(){

                @Override
                public void error(SAXParseException ex) {
                    if (XRLog.isLoggingEnabled()) {
                        XRLog.xmlEntities(Level.WARNING, ex.getMessage());
                    }
                }

                @Override
                public void fatalError(SAXParseException ex) {
                    if (XRLog.isLoggingEnabled()) {
                        XRLog.xmlEntities(Level.WARNING, ex.getMessage());
                    }
                }

                @Override
                public void warning(SAXParseException ex) {
                    if (XRLog.isLoggingEnabled()) {
                        XRLog.xmlEntities(Level.WARNING, ex.getMessage());
                    }
                }
            });
        }
        catch (Exception ex) {
            throw new XRRuntimeException("Failed on configuring SAX parser/XMLReader.", ex);
        }
    }

    private void setFeature(XMLReader xmlReader, String featureUri, boolean value) {
        try {
            xmlReader.setFeature(featureUri, value);
            XRLog.xmlEntities(Level.FINE, "SAX Parser feature: " + featureUri.substring(featureUri.lastIndexOf("/")) + " set to " + xmlReader.getFeature(featureUri));
        }
        catch (SAXNotSupportedException ex) {
            XRLog.xmlEntities(Level.WARNING, "SAX feature not supported on this XMLReader: " + featureUri);
        }
        catch (SAXNotRecognizedException ex) {
            XRLog.xmlEntities(Level.WARNING, "SAX feature not recognized on this XMLReader: " + featureUri + ". Feature may be properly named, but not recognized by this parser.");
        }
    }

    private static class CatalogContentHandler
    extends DefaultHandler {
        private Map entityMap = new HashMap();

        public Map getEntityMap() {
            return this.entityMap;
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            if (localName.equalsIgnoreCase("public") || localName.equals("") && qName.equalsIgnoreCase("public")) {
                this.entityMap.put(atts.getValue("publicId"), atts.getValue("uri"));
            }
        }
    }
}

