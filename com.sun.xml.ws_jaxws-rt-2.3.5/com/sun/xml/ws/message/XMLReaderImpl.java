/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.message;

import com.sun.xml.ws.api.message.Message;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class XMLReaderImpl
extends XMLFilterImpl {
    private final Message msg;
    private static final ContentHandler DUMMY = new DefaultHandler();
    protected static final InputSource THE_SOURCE = new InputSource();

    XMLReaderImpl(Message msg) {
        this.msg = msg;
    }

    @Override
    public void parse(String systemId) {
        this.reportError();
    }

    private void reportError() {
        throw new IllegalStateException("This is a special XMLReader implementation that only works with the InputSource given in SAXSource.");
    }

    @Override
    public void parse(InputSource input) throws SAXException {
        if (input != THE_SOURCE) {
            this.reportError();
        }
        this.msg.writeTo(this, this);
    }

    @Override
    public ContentHandler getContentHandler() {
        if (super.getContentHandler() == DUMMY) {
            return null;
        }
        return super.getContentHandler();
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        if (contentHandler == null) {
            contentHandler = DUMMY;
        }
        super.setContentHandler(contentHandler);
    }
}

