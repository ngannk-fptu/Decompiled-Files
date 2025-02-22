/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.impl.conn.Wire;

class LoggingOutputStream
extends OutputStream {
    private final OutputStream out;
    private final Wire wire;

    public LoggingOutputStream(OutputStream out, Wire wire) {
        this.out = out;
        this.wire = wire;
    }

    @Override
    public void write(int b) throws IOException {
        try {
            this.wire.output(b);
        }
        catch (IOException ex) {
            this.wire.output("[write] I/O error: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            this.wire.output(b);
            this.out.write(b);
        }
        catch (IOException ex) {
            this.wire.output("[write] I/O error: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            this.wire.output(b, off, len);
            this.out.write(b, off, len);
        }
        catch (IOException ex) {
            this.wire.output("[write] I/O error: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void flush() throws IOException {
        try {
            this.out.flush();
        }
        catch (IOException ex) {
            this.wire.output("[flush] I/O error: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.out.close();
        }
        catch (IOException ex) {
            this.wire.output("[close] I/O error: " + ex.getMessage());
            throw ex;
        }
    }
}

