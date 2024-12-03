/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.IndexInput;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ChecksumIndexInput
extends IndexInput {
    IndexInput main;
    Checksum digest;

    public ChecksumIndexInput(IndexInput main) {
        super("ChecksumIndexInput(" + main + ")");
        this.main = main;
        this.digest = new CRC32();
    }

    public byte readByte() throws IOException {
        byte b = this.main.readByte();
        this.digest.update(b);
        return b;
    }

    public void readBytes(byte[] b, int offset, int len) throws IOException {
        this.main.readBytes(b, offset, len);
        this.digest.update(b, offset, len);
    }

    public long getChecksum() {
        return this.digest.getValue();
    }

    public void close() throws IOException {
        this.main.close();
    }

    public long getFilePointer() {
        return this.main.getFilePointer();
    }

    public void seek(long pos) {
        throw new UnsupportedOperationException();
    }

    public long length() {
        return this.main.length();
    }
}

