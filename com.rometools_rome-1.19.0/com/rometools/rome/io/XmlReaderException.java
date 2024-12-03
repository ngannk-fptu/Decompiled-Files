/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io;

import java.io.IOException;
import java.io.InputStream;

public class XmlReaderException
extends IOException {
    private static final long serialVersionUID = 1L;
    private final String bomEncoding;
    private final String xmlGuessEncoding;
    private final String xmlEncoding;
    private final String contentTypeMime;
    private final String contentTypeEncoding;
    private final InputStream is;

    public XmlReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
        this(msg, null, null, bomEnc, xmlGuessEnc, xmlEnc, is);
    }

    public XmlReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
        super(msg);
        this.contentTypeMime = ctMime;
        this.contentTypeEncoding = ctEnc;
        this.bomEncoding = bomEnc;
        this.xmlGuessEncoding = xmlGuessEnc;
        this.xmlEncoding = xmlEnc;
        this.is = is;
    }

    public String getBomEncoding() {
        return this.bomEncoding;
    }

    public String getXmlGuessEncoding() {
        return this.xmlGuessEncoding;
    }

    public String getXmlEncoding() {
        return this.xmlEncoding;
    }

    public String getContentTypeMime() {
        return this.contentTypeMime;
    }

    public String getContentTypeEncoding() {
        return this.contentTypeEncoding;
    }

    public InputStream getInputStream() {
        return this.is;
    }
}

