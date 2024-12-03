/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.jcip.annotations.NotThreadSafe
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import net.jcip.annotations.NotThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;

@NotThreadSafe
public class CharacterCountingWriter
extends Writer {
    private final Writer delegate;
    private long characterCount = 0L;

    public CharacterCountingWriter(Writer delegate) {
        this.delegate = (Writer)Preconditions.checkNotNull((Object)delegate);
    }

    public long getCharacterCount() {
        return this.characterCount;
    }

    @Override
    public void write(int c) throws IOException {
        this.delegate.write(c);
        ++this.characterCount;
    }

    @Override
    public void write(@NonNull char[] cbuf) throws IOException {
        this.delegate.write(cbuf);
        this.characterCount += (long)cbuf.length;
    }

    @Override
    public void write(@NonNull char[] cbuf, int off, int len) throws IOException {
        this.delegate.write(cbuf, off, len);
        this.characterCount += (long)len;
    }

    @Override
    public void write(@NonNull String str) throws IOException {
        this.delegate.write(str);
        this.characterCount += (long)str.length();
    }

    @Override
    public void write(@NonNull String str, int off, int len) throws IOException {
        this.delegate.write(str, off, len);
        this.characterCount += (long)len;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        try {
            Writer writer = this.delegate.append(csq);
            return writer;
        }
        finally {
            this.characterCount += (long)csq.length();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        try {
            Writer writer = this.delegate.append(csq, start, end);
            return writer;
        }
        finally {
            this.characterCount += (long)(end - start);
        }
    }

    @Override
    public Writer append(char c) throws IOException {
        try {
            Writer writer = this.delegate.append(c);
            return writer;
        }
        finally {
            ++this.characterCount;
        }
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}

