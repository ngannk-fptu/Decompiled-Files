/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.output;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Supplier;

public class BrokenWriter
extends Writer {
    public static final BrokenWriter INSTANCE = new BrokenWriter();
    private final Supplier<IOException> exceptionSupplier;

    public BrokenWriter() {
        this(() -> new IOException("Broken writer"));
    }

    public BrokenWriter(IOException exception) {
        this(() -> exception);
    }

    public BrokenWriter(Supplier<IOException> exceptionSupplier) {
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
    public void write(char[] cbuf, int off, int len) throws IOException {
        throw this.exceptionSupplier.get();
    }
}

