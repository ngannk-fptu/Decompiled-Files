/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.ResponseConsumedWatcher;

class AutoCloseInputStream
extends FilterInputStream {
    private boolean streamOpen = true;
    private boolean selfClosed = false;
    private ResponseConsumedWatcher watcher = null;

    public AutoCloseInputStream(InputStream in, ResponseConsumedWatcher watcher) {
        super(in);
        this.watcher = watcher;
    }

    @Override
    public int read() throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            l = super.read();
            this.checkClose(l);
        }
        return l;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            l = super.read(b, off, len);
            this.checkClose(l);
        }
        return l;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            l = super.read(b);
            this.checkClose(l);
        }
        return l;
    }

    @Override
    public int available() throws IOException {
        int a = 0;
        if (this.isReadAllowed()) {
            a = super.available();
        }
        return a;
    }

    @Override
    public void close() throws IOException {
        if (!this.selfClosed) {
            this.selfClosed = true;
            this.notifyWatcher();
        }
    }

    private void checkClose(int readResult) throws IOException {
        if (readResult == -1) {
            this.notifyWatcher();
        }
    }

    private boolean isReadAllowed() throws IOException {
        if (!this.streamOpen && this.selfClosed) {
            throw new IOException("Attempted read on closed stream.");
        }
        return this.streamOpen;
    }

    private void notifyWatcher() throws IOException {
        if (this.streamOpen) {
            super.close();
            this.streamOpen = false;
            if (this.watcher != null) {
                this.watcher.responseConsumed();
            }
        }
    }
}

