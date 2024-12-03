/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FlushingStreamWriter
extends OutputStreamWriter {
    public FlushingStreamWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        super.write(cbuf, off, len);
        this.flush();
    }

    @Override
    public void write(int c) throws IOException {
        super.write(c);
        this.flush();
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        super.write(str, off, len);
        this.flush();
    }
}

