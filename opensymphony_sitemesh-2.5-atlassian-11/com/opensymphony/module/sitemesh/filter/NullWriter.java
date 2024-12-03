/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.filter;

import java.io.IOException;
import java.io.Writer;

public class NullWriter
extends Writer {
    public void write(char[] cbuf, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void flush() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }
}

