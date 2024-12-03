/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationAttributes;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class DomHelper {
    private static final Logger LOG = LogManager.getLogger(DomHelper.class);

    public static Location getLocationObject(Element element) {
        return LocationAttributes.getLocation(element);
    }

    public static Document parse(InputSource inputSource) {
        return DomHelper.parse(inputSource, null);
    }

    public static Document parse(InputSource inputSource, Map<String, String> dtdMappings) {
        SAXParser parser;
        SAXParserFactory factory = null;
        String parserProp = System.getProperty("xwork.saxParserFactory");
        if (parserProp != null) {
            try {
                ObjectFactory objectFactory = ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
                Class clazz = objectFactory.getClassInstance(parserProp);
                factory = (SAXParserFactory)clazz.newInstance();
            }
            catch (Exception e) {
                LOG.error("Unable to load saxParserFactory set by system property 'xwork.saxParserFactory': {}", (Object)parserProp, (Object)e);
            }
        }
        if (factory == null) {
            factory = SAXParserFactory.newInstance();
        }
        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new StrutsException("Unable to disable resolving external entities!", e);
        }
        factory.setValidating(dtdMappings != null);
        factory.setNamespaceAware(true);
        try {
            parser = factory.newSAXParser();
        }
        catch (Exception ex) {
            throw new StrutsException("Unable to create SAX parser", ex);
        }
        DOMBuilder builder = new DOMBuilder();
        LocationAttributes.Pipe locationHandler = new LocationAttributes.Pipe(builder);
        try {
            parser.parse(inputSource, (DefaultHandler)new StartHandler(locationHandler, dtdMappings));
        }
        catch (Exception ex) {
            throw new StrutsException(ex);
        }
        return builder.getDocument();
    }

    public static class StartHandler
    extends DefaultHandler {
        private final ContentHandler nextHandler;
        private final Map<String, String> dtdMappings;

        public StartHandler(ContentHandler next, Map<String, String> dtdMappings) {
            this.nextHandler = next;
            this.dtdMappings = dtdMappings;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.nextHandler.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            this.nextHandler.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            this.nextHandler.endDocument();
        }

        @Override
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            this.nextHandler.startElement(uri, loc, raw, attrs);
        }

        @Override
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            this.nextHandler.endElement(arg0, arg1, arg2);
        }

        @Override
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            this.nextHandler.startPrefixMapping(arg0, arg1);
        }

        @Override
        public void endPrefixMapping(String arg0) throws SAXException {
            this.nextHandler.endPrefixMapping(arg0);
        }

        @Override
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            this.nextHandler.characters(arg0, arg1, arg2);
        }

        @Override
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            this.nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        @Override
        public void processingInstruction(String arg0, String arg1) throws SAXException {
            this.nextHandler.processingInstruction(arg0, arg1);
        }

        @Override
        public void skippedEntity(String arg0) throws SAXException {
            this.nextHandler.skippedEntity(arg0);
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            if (this.dtdMappings != null && this.dtdMappings.containsKey(publicId)) {
                String dtdFile = this.dtdMappings.get(publicId);
                return new InputSource(ClassLoaderUtil.getResourceAsStream(dtdFile, DomHelper.class));
            }
            LOG.warn("Local DTD is missing for publicID: {} - defined mappings: {}", (Object)publicId, this.dtdMappings);
            return null;
        }

        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            LOG.error("{} at ({}:{}:{})", (Object)exception.getMessage(), (Object)exception.getPublicId(), (Object)exception.getLineNumber(), (Object)exception.getColumnNumber(), (Object)exception);
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            LOG.fatal("{} at ({}:{}:{})", (Object)exception.getMessage(), (Object)exception.getPublicId(), (Object)exception.getLineNumber(), (Object)exception.getColumnNumber(), (Object)exception);
            throw exception;
        }
    }

    public static class DOMBuilder
    implements ContentHandler {
        protected static SAXTransformerFactory FACTORY;
        protected SAXTransformerFactory factory;
        protected DOMResult result;
        protected Node parentNode;
        protected ContentHandler nextHandler;

        public DOMBuilder() {
            this((Node)null);
        }

        public DOMBuilder(SAXTransformerFactory factory) {
            this(factory, null);
        }

        public DOMBuilder(Node parentNode) {
            this(null, parentNode);
        }

        public DOMBuilder(SAXTransformerFactory factory, Node parentNode) {
            this.factory = factory == null ? FACTORY : factory;
            this.parentNode = parentNode;
            this.setup();
        }

        private void setup() {
            try {
                TransformerHandler handler = this.factory.newTransformerHandler();
                this.nextHandler = handler;
                this.result = this.parentNode != null ? new DOMResult(this.parentNode) : new DOMResult();
                handler.setResult(this.result);
            }
            catch (TransformerException local) {
                throw new StrutsException("Fatal-Error: Unable to get transformer handler", local);
            }
        }

        public Document getDocument() {
            if (this.result == null || this.result.getNode() == null) {
                return null;
            }
            if (this.result.getNode().getNodeType() == 9) {
                return (Document)this.result.getNode();
            }
            return this.result.getNode().getOwnerDocument();
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.nextHandler.setDocumentLocator(locator);
        }

        @Override
        public void startDocument() throws SAXException {
            this.nextHandler.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            this.nextHandler.endDocument();
        }

        @Override
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            this.nextHandler.startElement(uri, loc, raw, attrs);
        }

        @Override
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            this.nextHandler.endElement(arg0, arg1, arg2);
        }

        @Override
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            this.nextHandler.startPrefixMapping(arg0, arg1);
        }

        @Override
        public void endPrefixMapping(String arg0) throws SAXException {
            this.nextHandler.endPrefixMapping(arg0);
        }

        @Override
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            this.nextHandler.characters(arg0, arg1, arg2);
        }

        @Override
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            this.nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        @Override
        public void processingInstruction(String arg0, String arg1) throws SAXException {
            this.nextHandler.processingInstruction(arg0, arg1);
        }

        @Override
        public void skippedEntity(String arg0) throws SAXException {
            this.nextHandler.skippedEntity(arg0);
        }

        static {
            String parserProp = System.getProperty("xwork.saxTransformerFactory");
            if (parserProp != null) {
                try {
                    ObjectFactory objectFactory = ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
                    Class clazz = objectFactory.getClassInstance(parserProp);
                    FACTORY = (SAXTransformerFactory)clazz.newInstance();
                }
                catch (Exception e) {
                    LOG.error("Unable to load SAXTransformerFactory set by system property 'xwork.saxTransformerFactory': {}", (Object)parserProp, (Object)e);
                }
            }
            if (FACTORY == null) {
                FACTORY = (SAXTransformerFactory)TransformerFactory.newInstance();
            }
        }
    }
}

