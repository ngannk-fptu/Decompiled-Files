/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.largeobject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.largeobject.LargeObject;

public class BlobInputStream
extends InputStream {
    private @Nullable LargeObject lo;
    private final ResourceLock lock = new ResourceLock();
    private long apos;
    private byte @Nullable [] buffer;
    private int bpos;
    private int bsize;
    private long mpos = 0L;
    private long limit = -1L;

    public BlobInputStream(LargeObject lo) {
        this(lo, 1024);
    }

    public BlobInputStream(LargeObject lo, int bsize) {
        this(lo, bsize, -1L);
    }

    public BlobInputStream(LargeObject lo, int bsize, long limit) {
        this.lo = lo;
        this.buffer = null;
        this.bpos = 0;
        this.apos = 0L;
        this.bsize = bsize;
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        LargeObject lo = this.getLo();
        try {
            if (this.limit > 0L && this.apos >= this.limit) {
                return -1;
            }
            if (this.buffer == null || this.bpos >= this.buffer.length) {
                this.buffer = lo.read(this.bsize);
                this.bpos = 0;
            }
            if (this.buffer == null || this.bpos >= this.buffer.length) {
                return -1;
            }
            int ret = this.buffer[this.bpos] & 0x7F;
            if ((this.buffer[this.bpos] & 0x80) == 128) {
                ret |= 0x80;
            }
            ++this.bpos;
            ++this.apos;
            return ret;
        }
        catch (SQLException se) {
            throw new IOException(se.toString());
        }
    }

    @Override
    public void close() throws IOException {
        if (this.lo != null) {
            try {
                this.lo.close();
                this.lo = null;
            }
            catch (SQLException se) {
                throw new IOException(se.toString());
            }
        }
    }

    @Override
    public void mark(int readlimit) {
        try (ResourceLock ignore = this.lock.obtain();){
            this.mpos = this.apos;
        }
    }

    @Override
    public void reset() throws IOException {
        try (ResourceLock ignore = this.lock.obtain();){
            LargeObject lo = this.getLo();
            try {
                if (this.mpos <= Integer.MAX_VALUE) {
                    lo.seek((int)this.mpos);
                } else {
                    lo.seek64(this.mpos, 0);
                }
                this.buffer = null;
                this.apos = this.mpos;
            }
            catch (SQLException se) {
                throw new IOException(se.toString());
            }
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    private LargeObject getLo() throws IOException {
        if (this.lo == null) {
            throw new IOException("BlobOutputStream is closed");
        }
        return this.lo;
    }
}

