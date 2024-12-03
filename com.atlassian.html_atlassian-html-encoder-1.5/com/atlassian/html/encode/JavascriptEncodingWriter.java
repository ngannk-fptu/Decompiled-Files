/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.html.encode;

import com.atlassian.annotations.PublicApi;
import com.atlassian.html.encode.JavascriptEncoder;
import java.io.IOException;
import java.io.Writer;

@PublicApi
public class JavascriptEncodingWriter
extends Writer {
    private final Writer writer;

    public JavascriptEncodingWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(char[] chars, int off, int len) throws IOException {
        JavascriptEncoder.escape(this.writer, chars, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}

