/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.codehaus.stax2.io.Stax2BlockSource;

public class Stax2ByteArraySource
extends Stax2BlockSource {
    private static final String DEFAULT_ENCODING = "UTF-8";
    final byte[] mBuffer;
    final int mStart;
    final int mLength;

    public Stax2ByteArraySource(byte[] byArray, int n, int n2) {
        this.mBuffer = byArray;
        this.mStart = n;
        this.mLength = n2;
    }

    public Reader constructReader() throws IOException {
        String string = this.getEncoding();
        InputStream inputStream = this.constructInputStream();
        if (string == null || string.length() == 0) {
            string = DEFAULT_ENCODING;
        }
        return new InputStreamReader(inputStream, string);
    }

    public InputStream constructInputStream() throws IOException {
        return new ByteArrayInputStream(this.mBuffer, this.mStart, this.mLength);
    }

    public byte[] getBuffer() {
        return this.mBuffer;
    }

    public int getBufferStart() {
        return this.mStart;
    }

    public int getBufferLength() {
        return this.mLength;
    }

    public int getBufferEnd() {
        int n = this.mStart;
        if (this.mLength > 0) {
            n += this.mLength;
        }
        return n;
    }
}

