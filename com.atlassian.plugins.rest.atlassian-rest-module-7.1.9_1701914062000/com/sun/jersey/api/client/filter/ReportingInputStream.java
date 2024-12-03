/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.filter.ContainerListener;
import java.io.IOException;
import java.io.InputStream;

class ReportingInputStream
extends InputStream {
    private final InputStream inputStream;
    private final ContainerListener listener;
    private int markPosition = 0;
    private long totalBytes = 0L;
    private boolean finished = false;

    public ReportingInputStream(InputStream inputStream, ContainerListener listener) {
        this.inputStream = inputStream;
        this.listener = listener;
    }

    private void report(long bytes) {
        if (bytes == -1L) {
            this.finished = true;
            this.listener.onFinish();
        } else {
            this.totalBytes += bytes;
            this.listener.onReceived(bytes, this.totalBytes);
        }
    }

    @Override
    public int read() throws IOException {
        int readBytes = this.inputStream.read();
        if (readBytes == -1) {
            this.report(-1L);
        } else {
            this.report(1L);
        }
        return readBytes;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int readBytes = this.inputStream.read(b);
        this.report(readBytes);
        return readBytes;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readBytes = this.inputStream.read(b, off, len);
        this.report(readBytes);
        return readBytes;
    }

    @Override
    public long skip(long n) throws IOException {
        this.report(n);
        return this.inputStream.skip(n);
    }

    @Override
    public void close() throws IOException {
        if (!this.finished) {
            this.listener.onFinish();
        }
        this.inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.markPosition = readlimit;
        this.inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.totalBytes = this.markPosition;
        this.inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }
}

