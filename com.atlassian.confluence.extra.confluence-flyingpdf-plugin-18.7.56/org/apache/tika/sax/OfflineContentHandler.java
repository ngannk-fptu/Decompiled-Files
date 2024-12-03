/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.io.ClosedInputStream;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

public class OfflineContentHandler
extends ContentHandlerDecorator {
    public OfflineContentHandler(ContentHandler handler) {
        super(handler);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(new ClosedInputStream());
    }
}

