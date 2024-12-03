/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.apache.commons.io.build.AbstractStreamBuilder;

public class UnsynchronizedByteArrayInputStream
extends InputStream {
    public static final int END_OF_STREAM = -1;
    private final byte[] data;
    private final int eod;
    private int offset;
    private int markedOffset;

    public static Builder builder() {
        return new Builder();
    }

    @Deprecated
    public UnsynchronizedByteArrayInputStream(byte[] data) {
        this.data = Objects.requireNonNull(data, "data");
        this.offset = 0;
        this.eod = data.length;
        this.markedOffset = this.offset;
    }

    @Deprecated
    public UnsynchronizedByteArrayInputStream(byte[] data, int offset) {
        Objects.requireNonNull(data, "data");
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        this.data = data;
        this.offset = Math.min(offset, data.length > 0 ? data.length : offset);
        this.eod = data.length;
        this.markedOffset = this.offset;
    }

    @Deprecated
    public UnsynchronizedByteArrayInputStream(byte[] data, int offset, int length) {
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
        if (length < 0) {
            throw new IllegalArgumentException("length cannot be negative");
        }
        this.data = Objects.requireNonNull(data, "data");
        this.offset = Math.min(offset, data.length > 0 ? data.length : offset);
        this.eod = Math.min(this.offset + length, data.length);
        this.markedOffset = this.offset;
    }

    @Override
    public int available() {
        return this.offset < this.eod ? this.eod - this.offset : 0;
    }

    @Override
    public void mark(int readlimit) {
        this.markedOffset = this.offset;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() {
        return this.offset < this.eod ? this.data[this.offset++] & 0xFF : -1;
    }

    @Override
    public int read(byte[] dest) {
        Objects.requireNonNull(dest, "dest");
        return this.read(dest, 0, dest.length);
    }

    @Override
    public int read(byte[] dest, int off, int len) {
        Objects.requireNonNull(dest, "dest");
        if (off < 0 || len < 0 || off + len > dest.length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.offset >= this.eod) {
            return -1;
        }
        int actualLen = this.eod - this.offset;
        if (len < actualLen) {
            actualLen = len;
        }
        if (actualLen <= 0) {
            return 0;
        }
        System.arraycopy(this.data, this.offset, dest, off, actualLen);
        this.offset += actualLen;
        return actualLen;
    }

    @Override
    public void reset() {
        this.offset = this.markedOffset;
    }

    @Override
    public long skip(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Skipping backward is not supported");
        }
        long actualSkip = this.eod - this.offset;
        if (n < actualSkip) {
            actualSkip = n;
        }
        this.offset = Math.addExact(this.offset, Math.toIntExact(n));
        return actualSkip;
    }

    public static class Builder
    extends AbstractStreamBuilder<UnsynchronizedByteArrayInputStream, Builder> {
        private int offset;
        private int length;

        @Override
        public UnsynchronizedByteArrayInputStream get() throws IOException {
            return new UnsynchronizedByteArrayInputStream(this.checkOrigin().getByteArray(), this.offset, this.length);
        }

        @Override
        public Builder setByteArray(byte[] origin) {
            this.length = Objects.requireNonNull(origin, "origin").length;
            return (Builder)super.setByteArray(origin);
        }

        public Builder setLength(int length) {
            if (length < 0) {
                throw new IllegalArgumentException("length cannot be negative");
            }
            this.length = length;
            return this;
        }

        public Builder setOffset(int offset) {
            if (offset < 0) {
                throw new IllegalArgumentException("offset cannot be negative");
            }
            this.offset = offset;
            return this;
        }
    }
}

