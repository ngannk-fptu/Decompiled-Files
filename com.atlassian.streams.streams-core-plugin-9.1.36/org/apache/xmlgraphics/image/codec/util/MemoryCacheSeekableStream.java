/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.codec.util.SeekableStream;

public final class MemoryCacheSeekableStream
extends SeekableStream {
    private InputStream src;
    private long pointer;
    private static final int SECTOR_SHIFT = 9;
    private static final int SECTOR_SIZE = 512;
    private static final int SECTOR_MASK = 511;
    private List data = new ArrayList();
    int length;
    boolean foundEOS;

    public MemoryCacheSeekableStream(InputStream src) {
        this.src = src;
    }

    private long readUntil(long pos) throws IOException {
        int startSector;
        if (pos < (long)this.length) {
            return pos;
        }
        if (this.foundEOS) {
            return this.length;
        }
        int sector = (int)(pos >> 9);
        for (int i = startSector = this.length >> 9; i <= sector; ++i) {
            byte[] buf = new byte[512];
            this.data.add(buf);
            int len = 512;
            int off = 0;
            while (len > 0) {
                int nbytes = this.src.read(buf, off, len);
                if (nbytes == -1) {
                    this.foundEOS = true;
                    return this.length;
                }
                off += nbytes;
                len -= nbytes;
                this.length += nbytes;
            }
        }
        return this.length;
    }

    @Override
    public boolean canSeekBackwards() {
        return true;
    }

    @Override
    public long getFilePointer() {
        return this.pointer;
    }

    @Override
    public void seek(long pos) throws IOException {
        if (pos < 0L) {
            throw new IOException(PropertyUtil.getString("MemoryCacheSeekableStream0"));
        }
        this.pointer = pos;
    }

    @Override
    public int read() throws IOException {
        long next = this.pointer + 1L;
        long pos = this.readUntil(next);
        if (pos >= next) {
            byte[] buf = (byte[])this.data.get((int)(this.pointer >> 9));
            return buf[(int)(this.pointer++ & 0x1FFL)] & 0xFF;
        }
        return -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        long pos = this.readUntil(this.pointer + (long)len);
        if (pos <= this.pointer) {
            return -1;
        }
        byte[] buf = (byte[])this.data.get((int)(this.pointer >> 9));
        int nbytes = Math.min(len, 512 - (int)(this.pointer & 0x1FFL));
        System.arraycopy(buf, (int)(this.pointer & 0x1FFL), b, off, nbytes);
        this.pointer += (long)nbytes;
        return nbytes;
    }
}

