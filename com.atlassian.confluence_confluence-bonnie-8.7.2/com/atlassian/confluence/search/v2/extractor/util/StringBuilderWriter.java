/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.extractor.util;

import com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder;
import java.io.IOException;
import java.io.Writer;

public class StringBuilderWriter
extends Writer {
    private final AbstractLengthLimitedStringBuilder sb;

    public StringBuilderWriter(AbstractLengthLimitedStringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.sb.append(cbuf, off, len);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    public String toString() {
        return this.sb.toString();
    }
}

