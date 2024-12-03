/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import java.io.InputStream;
import java.io.Reader;

public class XMLInputSource {
    private String publicId_;
    private String systemId_;
    private String baseSystemId_;
    private InputStream byteStream_;
    private Reader charStream_;
    private String encoding_;

    public XMLInputSource(String publicId, String systemId, String baseSystemId) {
        this.publicId_ = publicId;
        this.systemId_ = systemId;
        this.baseSystemId_ = baseSystemId;
    }

    public XMLInputSource(String publicId, String systemId, String baseSystemId, InputStream byteStream, String encoding) {
        this.publicId_ = publicId;
        this.systemId_ = systemId;
        this.baseSystemId_ = baseSystemId;
        this.byteStream_ = byteStream;
        this.encoding_ = encoding;
    }

    public XMLInputSource(String publicId, String systemId, String baseSystemId, Reader charStream, String encoding) {
        this.publicId_ = publicId;
        this.systemId_ = systemId;
        this.baseSystemId_ = baseSystemId;
        this.charStream_ = charStream;
        this.encoding_ = encoding;
    }

    public void setPublicId(String publicId) {
        this.publicId_ = publicId;
    }

    public String getPublicId() {
        return this.publicId_;
    }

    public void setSystemId(String systemId) {
        this.systemId_ = systemId;
    }

    public String getSystemId() {
        return this.systemId_;
    }

    public void setBaseSystemId(String baseSystemId) {
        this.baseSystemId_ = baseSystemId;
    }

    public String getBaseSystemId() {
        return this.baseSystemId_;
    }

    public void setByteStream(InputStream byteStream) {
        this.byteStream_ = byteStream;
    }

    public InputStream getByteStream() {
        return this.byteStream_;
    }

    public void setCharacterStream(Reader charStream) {
        this.charStream_ = charStream;
    }

    public Reader getCharacterStream() {
        return this.charStream_;
    }

    public void setEncoding(String encoding) {
        this.encoding_ = encoding;
    }

    public String getEncoding() {
        return this.encoding_;
    }
}

