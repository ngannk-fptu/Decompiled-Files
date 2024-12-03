/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.codehaus.stax2.io.Stax2BlockSource;

public class Stax2CharArraySource
extends Stax2BlockSource {
    final char[] mBuffer;
    final int mStart;
    final int mLength;

    public Stax2CharArraySource(char[] buf, int start, int len) {
        this.mBuffer = buf;
        this.mStart = start;
        this.mLength = len;
    }

    @Override
    public Reader constructReader() throws IOException {
        return new CharArrayReader(this.mBuffer, this.mStart, this.mLength);
    }

    @Override
    public InputStream constructInputStream() throws IOException {
        return null;
    }

    public char[] getBuffer() {
        return this.mBuffer;
    }

    public int getBufferStart() {
        return this.mStart;
    }

    public int getBufferLength() {
        return this.mLength;
    }
}

