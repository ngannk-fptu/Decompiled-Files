/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.google.common.base.Function
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.google.common.base.Function;
import java.io.IOException;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class InputStreamExceptionDecorator
extends InputStream {
    private final InputStream delegate;
    private final java.util.function.Function<IOException, @NonNull IOException> exceptionDecorator;

    @Deprecated
    public InputStreamExceptionDecorator(InputStream delegate, Function<IOException, @NonNull IOException> exceptionDecorator) {
        this.delegate = delegate;
        this.exceptionDecorator = exceptionDecorator;
    }

    public InputStreamExceptionDecorator(InputStream delegate, java.util.function.Function<IOException, @NonNull IOException> exceptionDecorator) {
        this.delegate = delegate;
        this.exceptionDecorator = exceptionDecorator;
    }

    @Override
    public int read() throws IOException {
        try {
            return this.delegate.read();
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        try {
            return this.delegate.read(b);
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            return this.delegate.read(b, off, len);
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        try {
            return this.delegate.skip(n);
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return this.delegate.available();
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.delegate.close();
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    @Override
    public void reset() throws IOException {
        try {
            this.delegate.reset();
        }
        catch (IOException e) {
            throw this.exceptionDecorator.apply(e);
        }
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(@Nullable Object obj) {
        return this.delegate.equals(obj);
    }

    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public void mark(int readlimit) {
        this.delegate.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return this.delegate.markSupported();
    }
}

