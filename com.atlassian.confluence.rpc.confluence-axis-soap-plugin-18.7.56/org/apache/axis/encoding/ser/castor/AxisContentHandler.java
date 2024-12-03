/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser.castor;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.SerializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AxisContentHandler
extends DefaultHandler {
    private SerializationContext context;

    public AxisContentHandler(SerializationContext context) {
        this.setContext(context);
    }

    public SerializationContext getContext() {
        return this.context;
    }

    public void setContext(SerializationContext context) {
        this.context = context;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            this.context.startElement(new QName(uri, localName), attributes);
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            this.context.endElement();
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            this.context.writeChars(ch, start, length);
        }
        catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }
}

