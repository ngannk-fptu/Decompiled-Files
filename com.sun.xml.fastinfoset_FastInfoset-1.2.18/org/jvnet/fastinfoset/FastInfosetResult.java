/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class FastInfosetResult
extends SAXResult {
    OutputStream _outputStream;

    public FastInfosetResult(OutputStream outputStream) {
        this._outputStream = outputStream;
    }

    @Override
    public ContentHandler getHandler() {
        ContentHandler handler = super.getHandler();
        if (handler == null) {
            handler = new SAXDocumentSerializer();
            this.setHandler(handler);
        }
        ((SAXDocumentSerializer)handler).setOutputStream(this._outputStream);
        return handler;
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        return (LexicalHandler)((Object)this.getHandler());
    }

    public OutputStream getOutputStream() {
        return this._outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this._outputStream = outputStream;
    }
}

