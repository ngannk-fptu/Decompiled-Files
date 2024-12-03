/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.io;

import java.io.Writer;

public class NullWriter
extends Writer {
    public static final NullWriter DEFAULT = new NullWriter();

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
    }
}

