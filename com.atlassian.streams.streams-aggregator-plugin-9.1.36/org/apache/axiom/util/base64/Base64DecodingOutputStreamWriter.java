/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.axiom.util.base64.AbstractBase64DecodingWriter;

public class Base64DecodingOutputStreamWriter
extends AbstractBase64DecodingWriter {
    private final OutputStream stream;

    public Base64DecodingOutputStreamWriter(OutputStream stream) {
        this.stream = stream;
    }

    protected void doWrite(byte[] b, int len) throws IOException {
        this.stream.write(b, 0, len);
    }

    public void flush() throws IOException {
        this.stream.flush();
    }

    public void close() throws IOException {
        this.stream.close();
    }
}

