/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.bedework.util.servlet.io.ByteArrayPrintWriter;
import org.bedework.util.servlet.io.WrappedResponse;

public class ByteArrayWrappedResponse
extends WrappedResponse {
    ByteArrayPrintWriter pw = new ByteArrayPrintWriter();

    public ByteArrayWrappedResponse(HttpServletResponse response) {
        super(response);
    }

    public ByteArrayWrappedResponse(HttpServletResponse response, Logger log) {
        super(response, log);
    }

    public PrintWriter getWriter() {
        if (this.debug) {
            this.getLogger().debug("getWriter called");
        }
        return this.pw.getWriter();
    }

    public ServletOutputStream getOutputStream() {
        if (this.debug) {
            this.getLogger().debug("getOutputStream called");
        }
        return this.pw.getStream();
    }

    public synchronized int size() {
        return this.pw.size();
    }

    public synchronized InputStream getInputStream() throws IOException {
        return this.pw.getInputStream();
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        this.pw.writeTo(out);
    }

    public byte[] toByteArray() {
        if (this.pw == null) {
            return null;
        }
        return this.pw.toByteArray();
    }

    public void release() throws IOException {
        if (this.pw != null) {
            try {
                this.pw.release();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    public void close() {
        if (this.pw != null) {
            try {
                this.pw.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.pw = null;
        super.close();
    }
}

