/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.catalina.tribes.io.XByteBuffer;

public class DirectByteArrayOutputStream
extends OutputStream {
    private final XByteBuffer buffer;

    public DirectByteArrayOutputStream(int size) {
        this.buffer = new XByteBuffer(size, false);
    }

    @Override
    public void write(int b) throws IOException {
        this.buffer.append((byte)b);
    }

    public int size() {
        return this.buffer.getLength();
    }

    public byte[] getArrayDirect() {
        return this.buffer.getBytesDirect();
    }

    public byte[] getArray() {
        return this.buffer.getBytes();
    }
}

