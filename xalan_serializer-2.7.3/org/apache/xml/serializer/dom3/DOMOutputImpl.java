/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer.dom3;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.ls.LSOutput;

final class DOMOutputImpl
implements LSOutput {
    private Writer fCharStream = null;
    private OutputStream fByteStream = null;
    private String fSystemId = null;
    private String fEncoding = null;

    DOMOutputImpl() {
    }

    @Override
    public Writer getCharacterStream() {
        return this.fCharStream;
    }

    @Override
    public void setCharacterStream(Writer characterStream) {
        this.fCharStream = characterStream;
    }

    @Override
    public OutputStream getByteStream() {
        return this.fByteStream;
    }

    @Override
    public void setByteStream(OutputStream byteStream) {
        this.fByteStream = byteStream;
    }

    @Override
    public String getSystemId() {
        return this.fSystemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.fSystemId = systemId;
    }

    @Override
    public String getEncoding() {
        return this.fEncoding;
    }

    @Override
    public void setEncoding(String encoding) {
        this.fEncoding = encoding;
    }
}

