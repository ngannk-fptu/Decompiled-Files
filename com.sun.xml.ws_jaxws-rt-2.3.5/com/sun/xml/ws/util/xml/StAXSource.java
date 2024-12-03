/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.SAXParseException2
 *  com.sun.istack.XMLStreamReaderToContentHandler
 */
package com.sun.xml.ws.util.xml;

import com.sun.istack.NotNull;
import com.sun.istack.SAXParseException2;
import com.sun.istack.XMLStreamReaderToContentHandler;
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
import org.xml.sax.helpers.XMLFilterImpl;

public class StAXSource
extends SAXSource {
    private final XMLStreamReaderToContentHandler reader;
    private final XMLStreamReader staxReader;
    private final XMLFilterImpl repeater = new XMLFilterImpl();
    private final XMLReader pseudoParser = new XMLReader(){
        private LexicalHandler lexicalHandler;
        private EntityResolver entityResolver;
        private DTDHandler dtdHandler;
        private ErrorHandler errorHandler;

        @Override
        public boolean getFeature(String name) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(name);
        }

        @Override
        public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
            if (!(name.equals("http://xml.org/sax/features/namespaces") && value || name.equals("http://xml.org/sax/features/namespace-prefixes") && !value)) {
                throw new SAXNotRecognizedException(name);
            }
        }

        @Override
        public Object getProperty(String name) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                return this.lexicalHandler;
            }
            throw new SAXNotRecognizedException(name);
        }

        @Override
        public void setProperty(String name, Object value) throws SAXNotRecognizedException {
            if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
                this.lexicalHandler = (LexicalHandler)value;
                return;
            }
            throw new SAXNotRecognizedException(name);
        }

        @Override
        public void setEntityResolver(EntityResolver resolver) {
            this.entityResolver = resolver;
        }

        @Override
        public EntityResolver getEntityResolver() {
            return this.entityResolver;
        }

        @Override
        public void setDTDHandler(DTDHandler handler) {
            this.dtdHandler = handler;
        }

        @Override
        public DTDHandler getDTDHandler() {
            return this.dtdHandler;
        }

        @Override
        public void setContentHandler(ContentHandler handler) {
            StAXSource.this.repeater.setContentHandler(handler);
        }

        @Override
        public ContentHandler getContentHandler() {
            return StAXSource.this.repeater.getContentHandler();
        }

        @Override
        public void setErrorHandler(ErrorHandler handler) {
            this.errorHandler = handler;
        }

        @Override
        public ErrorHandler getErrorHandler() {
            return this.errorHandler;
        }

        @Override
        public void parse(InputSource input) throws SAXException {
            this.parse();
        }

        @Override
        public void parse(String systemId) throws SAXException {
            this.parse();
        }

        public void parse() throws SAXException {
            try {
                StAXSource.this.reader.bridge();
            }
            catch (XMLStreamException e) {
                SAXParseException2 se = new SAXParseException2(e.getMessage(), null, null, e.getLocation() == null ? -1 : e.getLocation().getLineNumber(), e.getLocation() == null ? -1 : e.getLocation().getColumnNumber(), (Exception)e);
                if (this.errorHandler != null) {
                    this.errorHandler.fatalError((SAXParseException)se);
                }
                throw se;
            }
            finally {
                try {
                    StAXSource.this.staxReader.close();
                }
                catch (XMLStreamException xMLStreamException) {}
            }
        }
    };

    public StAXSource(XMLStreamReader reader, boolean eagerQuit) {
        this(reader, eagerQuit, new String[0]);
    }

    public StAXSource(XMLStreamReader reader, boolean eagerQuit, @NotNull String[] inscope) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }
        this.staxReader = reader;
        int eventType = reader.getEventType();
        if (eventType != 7 && eventType != 1) {
            throw new IllegalStateException();
        }
        this.reader = new XMLStreamReaderToContentHandler(reader, (ContentHandler)this.repeater, eagerQuit, false, inscope);
        super.setXMLReader(this.pseudoParser);
        super.setInputSource(new InputSource());
    }
}

