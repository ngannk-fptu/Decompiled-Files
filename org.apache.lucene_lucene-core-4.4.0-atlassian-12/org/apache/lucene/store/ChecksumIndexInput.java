/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.lucene.store.IndexInput;

public class ChecksumIndexInput
extends IndexInput {
    IndexInput main;
    Checksum digest;

    public ChecksumIndexInput(IndexInput main) {
        super("ChecksumIndexInput(" + main + ")");
        this.main = main;
        this.digest = new CRC32();
    }

    @Override
    public byte readByte() throws IOException {
        byte b = this.main.readByte();
        this.digest.update(b);
        return b;
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        this.main.readBytes(b, offset, len);
        this.digest.update(b, offset, len);
    }

    public long getChecksum() {
        return this.digest.getValue();
    }

    @Override
    public void close() throws IOException {
        this.main.close();
    }

    @Override
    public long getFilePointer() {
        return this.main.getFilePointer();
    }

    @Override
    public void seek(long pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long length() {
        return this.main.length();
    }
}

