/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EndDocumentShieldingContentHandler
extends ContentHandlerDecorator {
    private boolean endDocumentCalled = false;

    public EndDocumentShieldingContentHandler(ContentHandler handler) {
        super(handler);
    }

    @Override
    public void endDocument() throws SAXException {
        this.endDocumentCalled = true;
    }

    public void reallyEndDocument() throws SAXException {
        super.endDocument();
    }

    public boolean isEndDocumentWasCalled() {
        return this.endDocumentCalled;
    }
}

