/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.IOException;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

public class Base64EncodingStringBufferOutputStream
extends AbstractBase64EncodingOutputStream {
    private final Appendable buffer;

    public Base64EncodingStringBufferOutputStream(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public Base64EncodingStringBufferOutputStream(StringBuilder buffer) {
        this.buffer = buffer;
    }

    protected void doWrite(byte[] b) throws IOException {
        for (int i = 0; i < 4; ++i) {
            this.buffer.append((char)(b[i] & 0xFF));
        }
    }

    protected void flushBuffer() throws IOException {
    }

    protected void doClose() throws IOException {
    }

    protected void doFlush() throws IOException {
    }
}

