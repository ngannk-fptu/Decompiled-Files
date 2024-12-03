/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.data;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoldingWriter
extends FilterWriter {
    public static final int REDUCED_FOLD_LENGTH = 73;
    public static final int MAX_FOLD_LENGTH = 75;
    private static final char[] FOLD_PATTERN = new char[]{'\r', '\n', ' '};
    private final Logger log = LoggerFactory.getLogger(FoldingWriter.class);
    private int lineLength;
    private final int foldLength;

    public FoldingWriter(Writer writer, int foldLength) {
        super(writer);
        this.foldLength = Math.min(foldLength, 75);
    }

    public FoldingWriter(Writer writer) {
        this(writer, 73);
    }

    @Override
    public final void write(int c) throws IOException {
        this.write(new char[]{(char)c}, 0, 1);
    }

    @Override
    public final void write(char[] buffer, int offset, int length) throws IOException {
        int maxIndex = offset + length - 1;
        for (int i = offset; i <= maxIndex; ++i) {
            if (this.log.isTraceEnabled()) {
                this.log.trace("char [" + buffer[i] + "], line length [" + this.lineLength + "]");
            }
            if (this.lineLength >= this.foldLength) {
                super.write(FOLD_PATTERN, 0, FOLD_PATTERN.length);
                this.lineLength = 1;
            }
            super.write(buffer[i]);
            if (buffer[i] == '\r' || buffer[i] == '\n') {
                this.lineLength = 0;
                continue;
            }
            ++this.lineLength;
        }
    }

    @Override
    public final void write(String str, int off, int len) throws IOException {
        this.write(str.toCharArray(), off, len);
    }
}

