/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.IndexOutput;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ChecksumIndexOutput
extends IndexOutput {
    IndexOutput main;
    Checksum digest;

    public ChecksumIndexOutput(IndexOutput main) {
        this.main = main;
        this.digest = new CRC32();
    }

    public void writeByte(byte b) throws IOException {
        this.digest.update(b);
        this.main.writeByte(b);
    }

    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        this.digest.update(b, offset, length);
        this.main.writeBytes(b, offset, length);
    }

    public long getChecksum() {
        return this.digest.getValue();
    }

    public void flush() throws IOException {
        this.main.flush();
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

    public void prepareCommit() throws IOException {
        long checksum = this.getChecksum();
        long pos = this.main.getFilePointer();
        this.main.writeLong(checksum - 1L);
        this.main.flush();
        this.main.seek(pos);
    }

    public void finishCommit() throws IOException {
        this.main.writeLong(this.getChecksum());
    }

    public long length() throws IOException {
        return this.main.length();
    }
}

