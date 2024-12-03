/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.support;

import java.io.IOException;
import java.io.OutputStream;

public class LineCountingProxyOutputStream
extends OutputStream {
    private final OutputStream out;
    private int linesCount = 0;

    public LineCountingProxyOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.linesCount == 0) {
            ++this.linesCount;
        }
        if (b == 10) {
            ++this.linesCount;
        }
        this.out.write(b);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

    public int getLinesCount() {
        return this.linesCount;
    }
}

