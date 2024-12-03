/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 */
package com.sun.xml.ws.message.jaxb;

import com.sun.xml.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class JAXBBridgeSource
extends SAXSource {
    private final XMLBridge bridge;
    private final Object contentObject;
    private final XMLReader pseudoParser = new XMLFilterImpl(){
        private LexicalHandler lexicalHandler;

        @Override
        public boolean getFeature(String name) throws SAXNotRecognizedException {
            if (name.equals("http://xml.org/sax/features/namespaces")) {
                return true;
            }
            if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
                return false;
            }
            throw new SAXNotRecognizedException(name);
        }

        @Override
        public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
            if (name.equals("http://xml.org/sax/features/namespaces") && value) {
                return;
            }
            if (name.equals("http://xml.org/sax/features/namespace-prefixes") && !value) {
                return;
            }
            throw new SAXNotRecognizedException(name);
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
        public void parse(InputSource input) throws SAXException {
            this.parse();
        }

        @Override
        public void parse(String systemId) throws SAXException {
            this.parse();
        }

        public void parse() throws SAXException {
            try {
                this.startDocument();
                JAXBBridgeSource.this.bridge.marshal(JAXBBridgeSource.this.contentObject, this, null);
                this.endDocument();
            }
            catch (JAXBException e) {
                SAXParseException se = new SAXParseException(e.getMessage(), null, null, -1, -1, (Exception)((Object)e));
                this.fatalError(se);
                throw se;
            }
        }
    };

    public JAXBBridgeSource(XMLBridge bridge, Object contentObject) {
        this.bridge = bridge;
        this.contentObject = contentObject;
        super.setXMLReader(this.pseudoParser);
        super.setInputSource(new InputSource());
    }
}

