/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

public class Base64EncodingOutputStream
extends AbstractBase64EncodingOutputStream {
    private final OutputStream parent;
    private final byte[] buffer;
    private int len;

    public Base64EncodingOutputStream(OutputStream parent, int bufferSize) {
        this.parent = parent;
        this.buffer = new byte[bufferSize];
    }

    public Base64EncodingOutputStream(OutputStream parent) {
        this(parent, 4096);
    }

    protected void doWrite(byte[] b) throws IOException {
        if (this.buffer.length - this.len < 4) {
            this.flushBuffer();
        }
        System.arraycopy(b, 0, this.buffer, this.len, 4);
        this.len += 4;
    }

    protected void flushBuffer() throws IOException {
        this.parent.write(this.buffer, 0, this.len);
        this.len = 0;
    }

    protected void doFlush() throws IOException {
        this.parent.flush();
    }

    protected void doClose() throws IOException {
        this.parent.close();
    }
}

