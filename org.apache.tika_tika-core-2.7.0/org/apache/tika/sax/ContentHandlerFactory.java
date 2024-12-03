/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.xml.sax.ContentHandler;

public interface ContentHandlerFactory
extends Serializable {
    public ContentHandler getNewContentHandler();

    @Deprecated
    public ContentHandler getNewContentHandler(OutputStream var1, String var2) throws UnsupportedEncodingException;

    public ContentHandler getNewContentHandler(OutputStream var1, Charset var2);
}

