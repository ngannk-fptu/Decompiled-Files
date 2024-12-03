/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import org.bedework.util.servlet.io.ByteArrayServletStream;
import org.bedework.util.servlet.io.PooledBufferedOutputStream;

public class ByteArrayPrintWriter {
    private PooledBufferedOutputStream pbos = new PooledBufferedOutputStream();
    private PrintWriter pw;
    private ServletOutputStream sos;

    public PrintWriter getWriter() {
        if (this.pw == null) {
            this.pw = new PrintWriter(this.pbos);
        }
        return this.pw;
    }

    public ServletOutputStream getStream() {
        if (this.sos == null) {
            this.sos = new ByteArrayServletStream(this.pbos);
        }
        return this.sos;
    }

    public synchronized InputStream getInputStream() throws IOException {
        return this.pbos.getInputStream();
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        this.pbos.writeTo(out);
    }

    public synchronized int size() {
        return this.pbos.size();
    }

    byte[] toByteArray() {
        return this.pbos.toByteArray();
    }

    public void release() throws IOException {
        if (this.pbos != null) {
            try {
                this.pbos.release();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    void close() {
        if (this.pbos != null) {
            try {
                this.pbos.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.pw != null) {
            try {
                this.pw.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.sos != null) {
            try {
                this.sos.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.pbos = null;
        this.pw = null;
        this.sos = null;
    }
}

