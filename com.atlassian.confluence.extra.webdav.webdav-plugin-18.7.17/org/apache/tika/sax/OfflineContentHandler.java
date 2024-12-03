/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.ClosedInputStream
 */
package org.apache.tika.sax;

import java.io.InputStream;
import org.apache.commons.io.input.ClosedInputStream;
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
        return new InputSource((InputStream)new ClosedInputStream());
    }
}

