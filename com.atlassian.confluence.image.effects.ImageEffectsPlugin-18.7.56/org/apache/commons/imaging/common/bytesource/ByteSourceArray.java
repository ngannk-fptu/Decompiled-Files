/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.bytesource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.common.bytesource.ByteSource;

public class ByteSourceArray
extends ByteSource {
    private final byte[] bytes;

    public ByteSourceArray(String fileName, byte[] bytes) {
        super(fileName);
        this.bytes = bytes;
    }

    public ByteSourceArray(byte[] bytes) {
        this(null, bytes);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public byte[] getBlock(long startLong, int length) throws IOException {
        int start = (int)startLong;
        if (start < 0 || length < 0 || start + length < 0 || start + length > this.bytes.length) {
            throw new IOException("Could not read block (block start: " + start + ", block length: " + length + ", data length: " + this.bytes.length + ").");
        }
        byte[] result = new byte[length];
        System.arraycopy(this.bytes, start, result, 0, length);
        return result;
    }

    @Override
    public long getLength() {
        return this.bytes.length;
    }

    @Override
    public byte[] getAll() throws IOException {
        return this.bytes;
    }

    @Override
    public String getDescription() {
        return this.bytes.length + " byte array";
    }
}

