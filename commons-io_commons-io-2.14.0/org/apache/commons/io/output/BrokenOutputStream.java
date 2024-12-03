/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

public class BrokenOutputStream
extends OutputStream {
    public static final BrokenOutputStream INSTANCE = new BrokenOutputStream();
    private final Supplier<IOException> exceptionSupplier;

    public BrokenOutputStream() {
        this(() -> new IOException("Broken output stream"));
    }

    public BrokenOutputStream(IOException exception) {
        this(() -> exception);
    }

    public BrokenOutputStream(Supplier<IOException> exceptionSupplier) {
        this.exceptionSupplier = exceptionSupplier;
    }

    @Override
    public void close() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public void flush() throws IOException {
        throw this.exceptionSupplier.get();
    }

    @Override
    public void write(int b) throws IOException {
        throw this.exceptionSupplier.get();
    }
}

