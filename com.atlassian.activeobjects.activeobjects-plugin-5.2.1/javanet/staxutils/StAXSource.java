/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.StAXReaderToContentHandler;
import javanet.staxutils.XMLEventReaderToContentHandler;
import javanet.staxutils.XMLStreamReaderToContentHandler;
import javanet.staxutils.helpers.XMLFilterImplEx;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class StAXSource
extends SAXSource {
    private final StAXReaderToContentHandler reader;
    private XMLFilterImplEx repeater = new XMLFilterImplEx();
    private final XMLReader pseudoParser = new XMLReader(){
        private EntityResolver entityResolver;
        private DTDHandler dtdHandler;
        private ErrorHandler errorHandler;

        public boolean getFeature(String name) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
                return StAXSource.this.repeater.getNamespacePrefixes();
            }
            if ("http://xml.org/sax/features/external-general-entities".equals(name)) {
                return true;
            }
            if ("http://xml.org/sax/features/external-parameter-entities".equals(name)) {
                return true;
            }
            throw new SAXNotRecognizedException(name);
        }

        public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
                StAXSource.this.repeater.setNamespacePrefixes(value);
            } else if (!"http://xml.org/sax/features/external-general-entities".equals(name) && !"http://xml.org/sax/features/external-parameter-entities".equals(name)) {
                throw new SAXNotRecognizedException(name);
            }
        }

        public Object getProperty(String name) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                return StAXSource.this.repeater.getLexicalHandler();
            }
            throw new SAXNotRecognizedException(name);
        }

        public void setProperty(String name, Object value) throws SAXNotRecognizedException {
            if (!"http://xml.org/sax/properties/lexical-handler".equals(name)) {
                throw new SAXNotRecognizedException(name);
            }
            StAXSource.this.repeater.setLexicalHandler((LexicalHandler)value);
        }

        public void setEntityResolver(EntityResolver resolver) {
            this.entityResolver = resolver;
        }

        public EntityResolver getEntityResolver() {
            return this.entityResolver;
        }

        public void setDTDHandler(DTDHandler handler) {
            this.dtdHandler = handler;
        }

        public DTDHandler getDTDHandler() {
            return this.dtdHandler;
        }

        public void setContentHandler(ContentHandler handler) {
            StAXSource.this.repeater.setContentHandler(handler);
        }

        public ContentHandler getContentHandler() {
            return StAXSource.this.repeater.getContentHandler();
        }

        public void setErrorHandler(ErrorHandler handler) {
            this.errorHandler = handler;
        }

        public ErrorHandler getErrorHandler() {
            return this.errorHandler;
        }

        public void parse(InputSource input) throws SAXException {
            this.parse();
        }

        public void parse(String systemId) throws SAXException {
            this.parse();
        }

        public void parse() throws SAXException {
            try {
                StAXSource.this.reader.bridge();
            }
            catch (XMLStreamException e) {
                SAXParseException se = new SAXParseException(e.getMessage(), null, null, e.getLocation().getLineNumber(), e.getLocation().getColumnNumber(), e);
                if (this.errorHandler != null) {
                    this.errorHandler.fatalError(se);
                }
                throw se;
            }
        }
    };

    public StAXSource(XMLStreamReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }
        int eventType = reader.getEventType();
        if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException();
        }
        this.reader = new XMLStreamReaderToContentHandler(reader, this.repeater);
        super.setXMLReader(this.pseudoParser);
        super.setInputSource(new InputSource());
    }

    public StAXSource(XMLEventReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }
        this.reader = new XMLEventReaderToContentHandler(reader, this.repeater);
        super.setXMLReader(this.pseudoParser);
        super.setInputSource(new InputSource());
    }
}

