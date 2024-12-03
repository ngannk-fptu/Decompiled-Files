/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax;

import java.io.InputStream;
import java.io.Reader;

public class InputSource {
    private String publicId;
    private String systemId;
    private InputStream byteStream;
    private String encoding;
    private Reader characterStream;

    public InputSource() {
    }

    public InputSource(String string) {
        this.setSystemId(string);
    }

    public InputSource(InputStream inputStream) {
        this.setByteStream(inputStream);
    }

    public InputSource(Reader reader) {
        this.setCharacterStream(reader);
    }

    public void setPublicId(String string) {
        this.publicId = string;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public void setSystemId(String string) {
        this.systemId = string;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setByteStream(InputStream inputStream) {
        this.byteStream = inputStream;
    }

    public InputStream getByteStream() {
        return this.byteStream;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setCharacterStream(Reader reader) {
        this.characterStream = reader;
    }

    public Reader getCharacterStream() {
        return this.characterStream;
    }
}

