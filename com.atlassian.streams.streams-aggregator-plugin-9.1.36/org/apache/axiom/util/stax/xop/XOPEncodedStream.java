/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.xop;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.xop.MimePartProvider;

public class XOPEncodedStream {
    private final XMLStreamReader reader;
    private final MimePartProvider mimePartProvider;

    XOPEncodedStream(XMLStreamReader reader, MimePartProvider mimePartProvider) {
        this.reader = reader;
        this.mimePartProvider = mimePartProvider;
    }

    public XMLStreamReader getReader() {
        return this.reader;
    }

    public MimePartProvider getMimePartProvider() {
        return this.mimePartProvider;
    }
}

