/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 */
package com.atlassian.plugin.servlet.cache.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

class CacheableResponseStream
extends ServletOutputStream {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

    CacheableResponseStream() {
    }

    public void close() throws IOException {
        this.outputStream.close();
    }

    public void flush() throws IOException {
        this.outputStream.flush();
    }

    public void write(int data) {
        this.outputStream.write((byte)data);
    }

    public void write(@Nonnull byte[] data, int offset, int length) {
        this.outputStream.write(data, offset, length);
    }

    public void write(@Nonnull byte[] data) {
        this.write(data, 0, data.length);
    }

    @Nonnull
    public byte[] getCopy() {
        return this.outputStream.toByteArray();
    }

    public boolean isReady() {
        return false;
    }

    public void setWriteListener(WriteListener writeListener) {
    }
}

