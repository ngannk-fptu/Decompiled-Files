/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.ls.LSOutput;

public class DOMOutputImpl
implements LSOutput {
    protected Writer fCharStream = null;
    protected OutputStream fByteStream = null;
    protected String fSystemId = null;
    protected String fEncoding = null;

    @Override
    public Writer getCharacterStream() {
        return this.fCharStream;
    }

    @Override
    public void setCharacterStream(Writer writer) {
        this.fCharStream = writer;
    }

    @Override
    public OutputStream getByteStream() {
        return this.fByteStream;
    }

    @Override
    public void setByteStream(OutputStream outputStream) {
        this.fByteStream = outputStream;
    }

    @Override
    public String getSystemId() {
        return this.fSystemId;
    }

    @Override
    public void setSystemId(String string) {
        this.fSystemId = string;
    }

    @Override
    public String getEncoding() {
        return this.fEncoding;
    }

    @Override
    public void setEncoding(String string) {
        this.fEncoding = string;
    }
}

