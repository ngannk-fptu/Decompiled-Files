/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.apache.xmlbeans.impl.common.PushedInputStream;

public class ReaderInputStream
extends PushedInputStream {
    public static final int defaultBufferSize = 2048;
    private final Reader reader;
    private final Writer writer;
    private final char[] buf;

    public ReaderInputStream(Reader reader, String encoding) throws UnsupportedEncodingException {
        this(reader, encoding, 2048);
    }

    public ReaderInputStream(Reader reader, String encoding, int bufferSize) throws UnsupportedEncodingException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.reader = reader;
        this.writer = new OutputStreamWriter(this.getOutputStream(), encoding);
        this.buf = new char[bufferSize];
    }

    @Override
    public void fill(int requestedBytes) throws IOException {
        do {
            int chars;
            if ((chars = this.reader.read(this.buf)) < 0) {
                return;
            }
            this.writer.write(this.buf, 0, chars);
            this.writer.flush();
        } while (this.available() <= 0);
    }
}

