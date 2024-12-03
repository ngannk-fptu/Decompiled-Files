/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.internal.SdkFilterInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Deprecated
public abstract class AbstractRepeatableCipherInputStream<T>
extends SdkFilterInputStream {
    private final T cipherFactory;
    private final InputStream unencryptedDataStream;
    private boolean hasBeenAccessed;

    protected AbstractRepeatableCipherInputStream(InputStream input, FilterInputStream cipherInputStream, T cipherFactory) {
        super(cipherInputStream);
        this.unencryptedDataStream = input;
        this.cipherFactory = cipherFactory;
    }

    @Override
    public boolean markSupported() {
        this.abortIfNeeded();
        return this.unencryptedDataStream.markSupported();
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        if (this.hasBeenAccessed) {
            throw new UnsupportedOperationException("Marking is only supported before your first call to read or skip.");
        }
        this.unencryptedDataStream.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        this.unencryptedDataStream.reset();
        this.in = this.createCipherInputStream(this.unencryptedDataStream, this.cipherFactory);
        this.hasBeenAccessed = false;
    }

    @Override
    public int read() throws IOException {
        this.hasBeenAccessed = true;
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        this.hasBeenAccessed = true;
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.hasBeenAccessed = true;
        return super.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        this.hasBeenAccessed = true;
        return super.skip(n);
    }

    protected abstract FilterInputStream createCipherInputStream(InputStream var1, T var2);
}

