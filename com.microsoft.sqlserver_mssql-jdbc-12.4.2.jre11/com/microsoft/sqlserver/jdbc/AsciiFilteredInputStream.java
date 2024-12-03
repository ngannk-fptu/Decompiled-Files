/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.logging.Level;

final class AsciiFilteredInputStream
extends InputStream {
    private final InputStream containedStream;
    private static final byte[] ASCII_FILTER;

    AsciiFilteredInputStream(BaseInputStream containedStream) {
        if (BaseInputStream.logger.isLoggable(Level.FINER)) {
            BaseInputStream.logger.finer(containedStream.toString() + " wrapping in AsciiFilteredInputStream");
        }
        this.containedStream = containedStream;
    }

    @Override
    public void close() throws IOException {
        this.containedStream.close();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.containedStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return this.containedStream.available();
    }

    @Override
    public int read() throws IOException {
        int value = this.containedStream.read();
        if (value >= 0 && value <= 255) {
            return ASCII_FILTER[value];
        }
        return value;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int bytesRead = this.containedStream.read(b);
        if (bytesRead > 0) {
            if (bytesRead > b.length) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
                throw new IOException(form.format(new Object[]{b.length, bytesRead}));
            }
            for (int i = 0; i < bytesRead; ++i) {
                b[i] = ASCII_FILTER[b[i] & 0xFF];
            }
        }
        return bytesRead;
    }

    @Override
    public int read(byte[] b, int offset, int maxBytes) throws IOException {
        int bytesRead = this.containedStream.read(b, offset, maxBytes);
        if (bytesRead > 0) {
            if (offset + bytesRead > b.length) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
                throw new IOException(form.format(new Object[]{b.length, bytesRead}));
            }
            for (int i = 0; i < bytesRead; ++i) {
                b[offset + i] = ASCII_FILTER[b[offset + i] & 0xFF];
            }
        }
        return bytesRead;
    }

    @Override
    public boolean markSupported() {
        return this.containedStream.markSupported();
    }

    @Override
    public void mark(int readLimit) {
        this.containedStream.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        this.containedStream.reset();
    }

    static {
        int i;
        ASCII_FILTER = new byte[256];
        for (i = 0; i < 128; ++i) {
            AsciiFilteredInputStream.ASCII_FILTER[i] = (byte)i;
        }
        for (i = 128; i < 256; ++i) {
            AsciiFilteredInputStream.ASCII_FILTER[i] = 63;
        }
    }
}

