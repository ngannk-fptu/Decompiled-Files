/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.Writer;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

public class Base64EncodingWriterOutputStream
extends AbstractBase64EncodingOutputStream {
    private final Writer writer;
    private final char[] buffer;
    private int len;

    public Base64EncodingWriterOutputStream(Writer writer, int bufferSize) {
        this.writer = writer;
        this.buffer = new char[bufferSize];
    }

    public Base64EncodingWriterOutputStream(Writer writer) {
        this(writer, 4096);
    }

    protected void doWrite(byte[] b) throws IOException {
        if (this.buffer.length - this.len < 4) {
            this.flushBuffer();
        }
        for (int i = 0; i < 4; ++i) {
            this.buffer[this.len++] = (char)(b[i] & 0xFF);
        }
    }

    protected void flushBuffer() throws IOException {
        this.writer.write(this.buffer, 0, this.len);
        this.len = 0;
    }

    protected void doFlush() throws IOException {
        this.writer.flush();
    }

    protected void doClose() throws IOException {
        this.writer.close();
    }
}

