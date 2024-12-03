/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.pdfbox.io.RandomAccessWrite;

public class RandomAccessOutputStream
extends OutputStream {
    private final RandomAccessWrite writer;

    public RandomAccessOutputStream(RandomAccessWrite writer) {
        this.writer = writer;
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException {
        this.writer.write(b, offset, length);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.writer.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        this.writer.write(b);
    }
}

