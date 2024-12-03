/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.io.InputStream;
import org.apache.fontbox.ttf.TTFDataStream;

class TTCDataStream
extends TTFDataStream {
    private final TTFDataStream stream;

    TTCDataStream(TTFDataStream stream) {
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        return this.stream.read();
    }

    @Override
    public long readLong() throws IOException {
        return this.stream.readLong();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.stream.readUnsignedShort();
    }

    @Override
    public short readSignedShort() throws IOException {
        return this.stream.readSignedShort();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void seek(long pos) throws IOException {
        this.stream.seek(pos);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.stream.read(b, off, len);
    }

    @Override
    public long getCurrentPosition() throws IOException {
        return this.stream.getCurrentPosition();
    }

    @Override
    public InputStream getOriginalData() throws IOException {
        return this.stream.getOriginalData();
    }

    @Override
    public long getOriginalDataSize() {
        return this.stream.getOriginalDataSize();
    }
}

