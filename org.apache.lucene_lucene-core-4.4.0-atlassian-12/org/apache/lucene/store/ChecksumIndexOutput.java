/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.lucene.store.IndexOutput;

public class ChecksumIndexOutput
extends IndexOutput {
    IndexOutput main;
    Checksum digest;

    public ChecksumIndexOutput(IndexOutput main) {
        this.main = main;
        this.digest = new CRC32();
    }

    @Override
    public void writeByte(byte b) throws IOException {
        this.digest.update(b);
        this.main.writeByte(b);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        this.digest.update(b, offset, length);
        this.main.writeBytes(b, offset, length);
    }

    public long getChecksum() {
        return this.digest.getValue();
    }

    @Override
    public void flush() throws IOException {
        this.main.flush();
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

    public void finishCommit() throws IOException {
        this.main.writeLong(this.getChecksum());
    }

    @Override
    public long length() throws IOException {
        return this.main.length();
    }
}

