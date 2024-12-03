/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class BrokenInputStream
extends InputStream {
    public static final BrokenInputStream INSTANCE = new BrokenInputStream();
    private final Supplier<IOException> exceptionSupplier;

    public BrokenInputStream() {
        this(() -> new IOException("Broken input stream"));
    }

    public BrokenInputStream(IOException exception) {
        this(() -> exception);
    }

    public BrokenInputStream(Supplier<IOException> exceptionSupplier) {
        this.exceptionSupplier = exceptionSupplier;
    }

    @Override
    public int available() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public void close() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public int read() throws IOException {
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

