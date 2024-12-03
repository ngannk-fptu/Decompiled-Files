/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.TaggedSAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TaggedContentHandler
extends ContentHandlerDecorator {
    public TaggedContentHandler(ContentHandler proxy) {
        super(proxy);
    }

    public boolean isCauseOf(SAXException exception) {
        if (exception instanceof TaggedSAXException) {
            TaggedSAXException tagged = (TaggedSAXException)exception;
            return this == tagged.getTag();
        }
        return false;
    }

    public void throwIfCauseOf(Exception exception) throws SAXException {
        TaggedSAXException tagged;
        if (exception instanceof TaggedSAXException && this == (tagged = (TaggedSAXException)exception).getTag()) {
            throw tagged.getCause();
        }
    }

    @Override
    protected void handleException(SAXException e) throws SAXException {
        throw new TaggedSAXException(e, this);
    }
}

