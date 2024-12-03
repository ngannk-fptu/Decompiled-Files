/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.store.IndexOutput;
import java.io.Closeable;
import java.io.IOException;

public abstract class IndexInput
extends DataInput
implements Cloneable,
Closeable {
    private final String resourceDescription;

    @Deprecated
    public void skipChars(int length) throws IOException {
        for (int i = 0; i < length; ++i) {
            byte b = this.readByte();
            if ((b & 0x80) == 0) continue;
            if ((b & 0xE0) != 224) {
                this.readByte();
                continue;
            }
            this.readByte();
            this.readByte();
        }
    }

    @Deprecated
    protected IndexInput() {
        this("anonymous IndexInput");
    }

    protected IndexInput(String resourceDescription) {
        if (resourceDescription == null) {
            throw new IllegalArgumentException("resourceDescription must not be null");
        }
        this.resourceDescription = resourceDescription;
    }

    public abstract void close() throws IOException;

    public abstract long getFilePointer();

    public abstract void seek(long var1) throws IOException;

    public abstract long length();

    public void copyBytes(IndexOutput out, long numBytes) throws IOException {
        assert (numBytes >= 0L) : "numBytes=" + numBytes;
        byte[] copyBuf = new byte[1024];
        while (numBytes > 0L) {
            int toCopy = (int)(numBytes > (long)copyBuf.length ? (long)copyBuf.length : numBytes);
            this.readBytes(copyBuf, 0, toCopy);
            out.writeBytes(copyBuf, 0, toCopy);
            numBytes -= (long)toCopy;
        }
    }

    public String toString() {
        return this.resourceDescription;
    }
}

