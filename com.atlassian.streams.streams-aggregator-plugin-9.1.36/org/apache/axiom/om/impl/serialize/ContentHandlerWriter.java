/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.serialize;

import java.io.IOException;
import java.io.Writer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class ContentHandlerWriter
extends Writer {
    private final ContentHandler contentHandler;

    public ContentHandlerWriter(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            this.contentHandler.characters(cbuf, off, len);
        }
        catch (SAXException ex) {
            IOException ioException = new IOException();
            ioException.initCause(ex);
            throw ioException;
        }
    }

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }
}

