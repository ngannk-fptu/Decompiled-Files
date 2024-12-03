/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Supplier;

public class BrokenReader
extends Reader {
    public static final BrokenReader INSTANCE = new BrokenReader();
    private final Supplier<IOException> exceptionSupplier;

    public BrokenReader() {
        this(() -> new IOException("Broken reader"));
    }

    public BrokenReader(IOException exception) {
        this(() -> exception);
    }

    public BrokenReader(Supplier<IOException> exceptionSupplier) {
        this.exceptionSupplier = exceptionSupplier;
    }

    @Override
    public void close() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public boolean ready() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public synchronized void reset() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public long skip(long n) throws IOException {
        throw this.exceptionSupplier.get();
    }
}

