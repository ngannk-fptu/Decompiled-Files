/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.ContentHandler;

public class EmbeddedContentHandler
extends ContentHandlerDecorator {
    public EmbeddedContentHandler(ContentHandler handler) {
        super(handler);
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }
}

