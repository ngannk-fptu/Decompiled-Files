/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.output;

import java.io.Writer;

public class NullWriter
extends Writer {
    public static final NullWriter INSTANCE;
    @Deprecated
    public static final NullWriter NULL_WRITER;

    @Deprecated
    public NullWriter() {
    }

    @Override
    public Writer append(char c) {
        return this;
    }

    @Override
    public Writer append(CharSequence csq) {
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) {
        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(char[] chr) {
    }

    @Override
    public void write(char[] chr, int st, int end) {
    }

    @Override
    public void write(int idx) {
    }

    @Override
    public void write(String str) {
    }

    @Override
    public void write(String str, int st, int end) {
    }

    static {
        NULL_WRITER = INSTANCE = new NullWriter();
    }
}

