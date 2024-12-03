/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.txw2.TypedXmlWriter
 */
package com.sun.xml.ws.wsdl.writer;

import com.sun.xml.txw2.TypedXmlWriter;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TXWContentHandler
implements ContentHandler {
    Stack<TypedXmlWriter> stack = new Stack();

    public TXWContentHandler(TypedXmlWriter txw) {
        this.stack.push(txw);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        TypedXmlWriter txw = this.stack.peek()._element(uri, localName, TypedXmlWriter.class);
        this.stack.push(txw);
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); ++i) {
                String auri = atts.getURI(i);
                if ("http://www.w3.org/2000/xmlns/".equals(auri)) {
                    if ("xmlns".equals(atts.getLocalName(i))) {
                        txw._namespace(atts.getValue(i), "");
                        continue;
                    }
                    txw._namespace(atts.getValue(i), atts.getLocalName(i));
                    continue;
                }
                if ("schemaLocation".equals(atts.getLocalName(i)) && "".equals(atts.getValue(i))) continue;
                txw._attribute(auri, atts.getLocalName(i), (Object)atts.getValue(i));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.stack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }
}

