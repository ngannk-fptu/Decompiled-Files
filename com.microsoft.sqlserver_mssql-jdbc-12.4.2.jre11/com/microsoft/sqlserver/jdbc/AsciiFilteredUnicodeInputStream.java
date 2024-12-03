/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

final class AsciiFilteredUnicodeInputStream
extends InputStream {
    private final Reader containedReader;
    private final Charset asciiCharSet;
    private final byte[] bSingleByte = new byte[1];

    static AsciiFilteredUnicodeInputStream makeAsciiFilteredUnicodeInputStream(BaseInputStream strm, Reader rd) {
        if (BaseInputStream.logger.isLoggable(Level.FINER)) {
            BaseInputStream.logger.finer(strm.toString() + " wrapping in AsciiFilteredInputStream");
        }
        return new AsciiFilteredUnicodeInputStream(rd);
    }

    private AsciiFilteredUnicodeInputStream(Reader rd) {
        this.containedReader = rd;
        this.asciiCharSet = StandardCharsets.US_ASCII;
    }

    @Override
    public void close() throws IOException {
        this.containedReader.close();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.containedReader.skip(n);
    }

    @Override
    public int available() throws IOException {
        return 0;
    }

    @Override
    public int read() throws IOException {
        int bytesRead = this.read(this.bSingleByte);
        return -1 == bytesRead ? -1 : this.bSingleByte[0] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int offset, int maxBytes) throws IOException {
        char[] tempBufferToHoldCharDataForConversion = new char[maxBytes];
        int charsRead = this.containedReader.read(tempBufferToHoldCharDataForConversion);
        if (charsRead > 0) {
            if (charsRead < maxBytes) {
                maxBytes = charsRead;
            }
            ByteBuffer encodedBuff = this.asciiCharSet.encode(CharBuffer.wrap(tempBufferToHoldCharDataForConversion));
            encodedBuff.get(b, offset, maxBytes);
        }
        return charsRead;
    }

    @Override
    public boolean markSupported() {
        return this.containedReader.markSupported();
    }

    @Override
    public void mark(int readLimit) {
        try {
            this.containedReader.mark(readLimit);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void reset() throws IOException {
        this.containedReader.reset();
    }
}

