/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import java.io.IOException;
import java.io.OutputStream;

public abstract class CommittingOutputStream
extends OutputStream {
    private OutputStream adaptedOutput;
    private boolean isCommitted;

    public CommittingOutputStream() {
    }

    public CommittingOutputStream(OutputStream adaptedOutput) {
        if (adaptedOutput == null) {
            throw new IllegalArgumentException();
        }
        this.adaptedOutput = adaptedOutput;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.commitStream();
        this.adaptedOutput.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.commitStream();
        this.adaptedOutput.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        this.commitStream();
        this.adaptedOutput.write(b);
    }

    @Override
    public void flush() throws IOException {
        if (this.isCommitted) {
            this.adaptedOutput.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.isCommitted) {
            this.adaptedOutput.close();
        }
    }

    private void commitStream() throws IOException {
        if (!this.isCommitted) {
            this.commit();
            if (this.adaptedOutput == null) {
                this.adaptedOutput = this.getOutputStream();
                if (this.adaptedOutput == null) {
                    throw new NullPointerException();
                }
            }
            this.isCommitted = true;
        }
    }

    protected OutputStream getOutputStream() throws IOException {
        throw new IllegalStateException();
    }

    protected abstract void commit() throws IOException;
}

