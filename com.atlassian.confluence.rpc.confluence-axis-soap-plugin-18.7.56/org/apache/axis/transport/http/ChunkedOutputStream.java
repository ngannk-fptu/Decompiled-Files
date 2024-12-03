/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChunkedOutputStream
extends FilterOutputStream {
    boolean eos = false;
    static final byte[] CRLF = "\r\n".getBytes();
    static final byte[] LAST_TOKEN = "0\r\n\r\n".getBytes();

    private ChunkedOutputStream() {
        super(null);
    }

    public ChunkedOutputStream(OutputStream os) {
        super(os);
    }

    public void write(int b) throws IOException {
        this.write(new byte[]{(byte)b}, 0, 1);
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        this.out.write(Integer.toHexString(len).getBytes());
        this.out.write(CRLF);
        this.out.write(b, off, len);
        this.out.write(CRLF);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void eos() throws IOException {
        ChunkedOutputStream chunkedOutputStream = this;
        synchronized (chunkedOutputStream) {
            if (this.eos) {
                return;
            }
            this.eos = true;
        }
        this.out.write(LAST_TOKEN);
        this.out.flush();
    }

    public void close() throws IOException {
        this.eos();
        this.out.close();
    }
}

