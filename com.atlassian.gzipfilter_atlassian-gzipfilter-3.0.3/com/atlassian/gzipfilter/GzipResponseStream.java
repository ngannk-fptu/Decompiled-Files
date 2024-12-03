/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.gzipfilter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public final class GzipResponseStream
extends ServletOutputStream {
    final GZIPOutputStream gzipstream;
    final HttpServletResponse response;
    private static final int DEFAULT_BUFFER_SIZE_BYTES = 1024;

    private static <T> void invoke(Callable<T> method) throws IOException {
        try {
            method.call();
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    public GzipResponseStream(HttpServletResponse response) throws IOException {
        this.response = response;
        this.gzipstream = new GZIPOutputStream((OutputStream)response.getOutputStream(), 1024, true);
    }

    public void close() throws IOException {
        GzipResponseStream.invoke(() -> {
            this.gzipstream.close();
            return null;
        });
    }

    public void flush() throws IOException {
        GzipResponseStream.invoke(() -> {
            this.gzipstream.flush();
            return null;
        });
    }

    public void write(int b) throws IOException {
        GzipResponseStream.invoke(() -> {
            this.gzipstream.write(b);
            return null;
        });
    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        GzipResponseStream.invoke(() -> {
            this.gzipstream.write(b, off, len);
            return null;
        });
    }
}

