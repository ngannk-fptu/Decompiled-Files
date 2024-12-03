/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;

class ReaderInputStream
extends InputStream {
    private final Reader reader;
    private final Charset charset;
    private final long readerLength;
    private long readerCharsRead = 0L;
    private boolean atEndOfStream = false;
    private CharBuffer rawChars = null;
    private static final int MAX_CHAR_BUFFER_SIZE = 4000;
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);
    private ByteBuffer encodedChars = EMPTY_BUFFER;
    private final byte[] oneByte = new byte[1];

    ReaderInputStream(Reader reader, Charset charset, long readerLength) {
        assert (reader != null);
        assert (charset != null);
        assert (-1L == readerLength || readerLength >= 0L);
        this.reader = reader;
        this.charset = charset;
        this.readerLength = readerLength;
    }

    @Override
    public int available() throws IOException {
        assert (null != this.reader);
        assert (null != this.encodedChars);
        if (0L == this.readerLength) {
            return 0;
        }
        if (this.encodedChars.remaining() > 0) {
            return this.encodedChars.remaining();
        }
        if (this.reader.ready()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int read() throws IOException {
        return -1 == this.readInternal(this.oneByte, 0, this.oneByte.length) ? -1 : this.oneByte[0];
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.readInternal(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.readInternal(b, off, len);
    }

    private int readInternal(byte[] b, int off, int len) throws IOException {
        int bytesRead;
        int bytesToRead;
        assert (null != b);
        assert (0 <= off && off <= b.length);
        assert (0 <= len && len <= b.length);
        assert (off <= b.length - len);
        if (0 == len) {
            return 0;
        }
        for (bytesRead = 0; bytesRead < len && this.encodeChars(); bytesRead += bytesToRead) {
            bytesToRead = this.encodedChars.remaining();
            if (bytesToRead > len - bytesRead) {
                bytesToRead = len - bytesRead;
            }
            assert (bytesToRead > 0);
            this.encodedChars.get(b, off + bytesRead, bytesToRead);
        }
        return 0 == bytesRead && this.atEndOfStream ? -1 : bytesRead;
    }

    private boolean encodeChars() throws IOException {
        if (this.atEndOfStream) {
            return false;
        }
        if (this.encodedChars.hasRemaining()) {
            return true;
        }
        if (null == this.rawChars || !this.rawChars.hasRemaining()) {
            if (null == this.rawChars) {
                this.rawChars = CharBuffer.allocate(-1L == this.readerLength || this.readerLength > 4000L ? 4000 : Math.max((int)this.readerLength, 1));
            } else {
                ((Buffer)this.rawChars).clear();
            }
            while (this.rawChars.hasRemaining()) {
                int lastPosition = this.rawChars.position();
                int charsRead = 0;
                try {
                    charsRead = this.reader.read(this.rawChars);
                }
                catch (Exception e) {
                    String detailMessage = e.getMessage();
                    if (null == detailMessage) {
                        detailMessage = SQLServerException.getErrString("R_streamReadReturnedInvalidValue");
                    }
                    IOException ioException = new IOException(detailMessage);
                    ioException.initCause(e);
                    throw ioException;
                }
                if (charsRead < -1 || 0 == charsRead) {
                    throw new IOException(SQLServerException.getErrString("R_streamReadReturnedInvalidValue"));
                }
                if (-1 == charsRead) {
                    if (this.rawChars.position() != lastPosition) {
                        throw new IOException(SQLServerException.getErrString("R_streamReadReturnedInvalidValue"));
                    }
                    if (-1L != this.readerLength && 0L != this.readerLength - this.readerCharsRead) {
                        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
                        throw new IOException(form.format(new Object[]{this.readerLength, this.readerCharsRead}));
                    }
                    if (0 != this.rawChars.position()) break;
                    this.rawChars = null;
                    this.atEndOfStream = true;
                    return false;
                }
                assert (charsRead > 0);
                if (charsRead != this.rawChars.position() - lastPosition) {
                    throw new IOException(SQLServerException.getErrString("R_streamReadReturnedInvalidValue"));
                }
                if (-1L != this.readerLength && (long)charsRead > this.readerLength - this.readerCharsRead) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
                    throw new IOException(form.format(new Object[]{this.readerLength, this.readerCharsRead}));
                }
                this.readerCharsRead += (long)charsRead;
            }
            ((Buffer)this.rawChars).flip();
        }
        if (!this.rawChars.hasRemaining()) {
            return false;
        }
        this.encodedChars = this.charset.encode(this.rawChars);
        return true;
    }
}

