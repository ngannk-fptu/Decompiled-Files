/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;

public class ASCIIReader
extends Reader {
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    protected InputStream fInputStream;
    protected byte[] fBuffer;

    public ASCIIReader(InputStream inputStream, int size) {
        this.fInputStream = inputStream;
        this.fBuffer = new byte[size];
    }

    @Override
    public int read() throws IOException {
        int b0 = this.fInputStream.read();
        if (b0 > 128) {
            throw new IOException(Localizer.getMessage("jsp.error.xml.invalidASCII", Integer.toString(b0)));
        }
        return b0;
    }

    @Override
    public int read(char[] ch, int offset, int length) throws IOException {
        if (length > this.fBuffer.length) {
            length = this.fBuffer.length;
        }
        int count = this.fInputStream.read(this.fBuffer, 0, length);
        for (int i = 0; i < count; ++i) {
            byte b0 = this.fBuffer[i];
            if (b0 > 128) {
                throw new IOException(Localizer.getMessage("jsp.error.xml.invalidASCII", Integer.toString(b0)));
            }
            ch[offset + i] = (char)b0;
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        return this.fInputStream.skip(n);
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }

    @Override
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        this.fInputStream.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        this.fInputStream.reset();
    }

    @Override
    public void close() throws IOException {
        this.fInputStream.close();
    }
}

