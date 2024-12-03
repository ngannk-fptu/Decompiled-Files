/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class BufferingInputOutputStream
extends OutputStream {
    private final Queue<byte[]> buffers = new LinkedList<byte[]>();

    @Override
    public void write(int b) throws IOException {
        byte[] buffer = new byte[]{(byte)b};
        this.buffers.add(buffer);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.buffers.add(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len > 0) {
            byte[] buffer = new byte[len];
            System.arraycopy(b, off, buffer, 0, len);
            this.buffers.add(buffer);
        }
    }

    public byte[] nextBytes() {
        return this.buffers.poll();
    }

    public int available() {
        if (this.buffers.isEmpty()) {
            return 0;
        }
        return this.buffers.peek().length;
    }
}

