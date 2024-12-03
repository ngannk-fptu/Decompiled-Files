/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.filter.ContainerListener;
import java.io.IOException;
import java.io.OutputStream;

class ReportingOutputStream
extends OutputStream {
    private final OutputStream outputStream;
    private final ContainerListener listener;
    private long totalBytes = 0L;

    public ReportingOutputStream(OutputStream outputStream, ContainerListener listener) {
        this.outputStream = outputStream;
        this.listener = listener;
    }

    private void report(long bytes) {
        this.totalBytes += bytes;
        this.listener.onSent(bytes, this.totalBytes);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.outputStream.write(b);
        this.report(b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.outputStream.write(b, off, len);
        this.report(len);
    }

    @Override
    public void write(int b) throws IOException {
        this.outputStream.write(b);
        this.report(1L);
    }

    @Override
    public void flush() throws IOException {
        this.outputStream.flush();
    }
}

