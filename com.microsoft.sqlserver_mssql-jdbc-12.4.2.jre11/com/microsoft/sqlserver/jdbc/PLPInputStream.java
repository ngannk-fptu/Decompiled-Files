/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.ServerDTVImpl;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;
import java.io.IOException;

class PLPInputStream
extends BaseInputStream {
    static final long PLP_NULL = -1L;
    static final long UNKNOWN_PLP_LEN = -2L;
    private static final byte[] EMPTY_PLP_BYTES = new byte[0];
    private static final int PLP_EOS = -1;
    private int currentChunkRemain;
    private int markedChunkRemain;
    private int leftOverReadLimit = 0;
    private byte[] oneByteArray = new byte[1];

    static final boolean isNull(TDSReader tdsReader) throws SQLServerException {
        TDSReaderMark mark = tdsReader.mark();
        try {
            boolean bl = null == PLPInputStream.makeTempStream(tdsReader, false, null);
            return bl;
        }
        finally {
            tdsReader.reset(mark);
        }
    }

    static final PLPInputStream makeTempStream(TDSReader tdsReader, boolean discardValue, ServerDTVImpl dtv) throws SQLServerException {
        return PLPInputStream.makeStream(tdsReader, discardValue, discardValue, dtv);
    }

    static final PLPInputStream makeStream(TDSReader tdsReader, InputStreamGetterArgs getterArgs, ServerDTVImpl dtv) throws SQLServerException {
        PLPInputStream is = PLPInputStream.makeStream(tdsReader, getterArgs.isAdaptive, getterArgs.isStreaming, dtv);
        if (null != is) {
            is.setLoggingInfo(getterArgs.logContext);
        }
        return is;
    }

    private static PLPInputStream makeStream(TDSReader tdsReader, boolean isAdaptive, boolean isStreaming, ServerDTVImpl dtv) throws SQLServerException {
        long payloadLength = tdsReader.readLong();
        if (-1L == payloadLength) {
            return null;
        }
        return new PLPInputStream(tdsReader, payloadLength, isAdaptive, isStreaming, dtv);
    }

    PLPInputStream(TDSReader tdsReader, long statedPayloadLength, boolean isAdaptive, boolean isStreaming, ServerDTVImpl dtv) {
        super(tdsReader, isAdaptive, isStreaming, dtv);
        this.payloadLength = -2L != statedPayloadLength ? (int)statedPayloadLength : -1;
        this.markedChunkRemain = 0;
        this.currentChunkRemain = 0;
    }

    @Override
    byte[] getBytes() throws SQLServerException {
        byte[] value;
        this.readBytesInternal(null, 0, 0);
        if (-1 == this.currentChunkRemain) {
            value = EMPTY_PLP_BYTES;
        } else {
            value = new byte[-1 != this.payloadLength ? this.payloadLength : this.currentChunkRemain];
            int bytesRead = 0;
            while (-1 != this.currentChunkRemain) {
                if (value.length == bytesRead) {
                    byte[] newValue = new byte[bytesRead + this.currentChunkRemain];
                    System.arraycopy(value, 0, newValue, 0, bytesRead);
                    value = newValue;
                }
                bytesRead += this.readBytesInternal(value, bytesRead, this.currentChunkRemain);
            }
        }
        try {
            this.close();
        }
        catch (IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return value;
    }

    @Override
    public long skip(long n) throws IOException {
        long bytesread;
        this.checkClosed();
        if (n < 0L) {
            return 0L;
        }
        if (n > Integer.MAX_VALUE) {
            n = Integer.MAX_VALUE;
        }
        if (-1L == (bytesread = (long)this.readBytes(null, 0, (int)n))) {
            return 0L;
        }
        return bytesread;
    }

    @Override
    public int available() throws IOException {
        this.checkClosed();
        try {
            if (0 == this.currentChunkRemain) {
                this.readBytesInternal(null, 0, 0);
            }
            if (-1 == this.currentChunkRemain) {
                return 0;
            }
            int available = this.tdsReader.available();
            if (available > this.currentChunkRemain) {
                available = this.currentChunkRemain;
            }
            return available;
        }
        catch (SQLServerException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (-1 != this.readBytes(this.oneByteArray, 0, 1)) {
            return this.oneByteArray[0] & 0xFF;
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (null == b) {
            throw new NullPointerException();
        }
        this.checkClosed();
        return this.readBytes(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int offset, int maxBytes) throws IOException {
        if (null == b) {
            throw new NullPointerException();
        }
        if (offset < 0 || maxBytes < 0 || offset + maxBytes > b.length) {
            throw new IndexOutOfBoundsException();
        }
        this.checkClosed();
        return this.readBytes(b, offset, maxBytes);
    }

    int readBytes(byte[] b, int offset, int maxBytes) throws IOException {
        if (0 == maxBytes) {
            return 0;
        }
        try {
            return this.readBytesInternal(b, offset, maxBytes);
        }
        catch (SQLServerException e) {
            throw new IOException(e.getMessage());
        }
    }

    private int readBytesInternal(byte[] b, int offset, int maxBytes) throws SQLServerException {
        if (-1 == this.currentChunkRemain) {
            return -1;
        }
        int bytesRead = 0;
        while (true) {
            if (0 == this.currentChunkRemain) {
                this.currentChunkRemain = (int)this.tdsReader.readUnsignedInt();
                assert (this.currentChunkRemain >= 0);
                if (0 == this.currentChunkRemain) {
                    this.currentChunkRemain = -1;
                    break;
                }
            }
            if (bytesRead == maxBytes) break;
            int bytesToRead = maxBytes - bytesRead;
            if (bytesToRead > this.currentChunkRemain) {
                bytesToRead = this.currentChunkRemain;
            }
            if (null == b) {
                this.tdsReader.skip(bytesToRead);
            } else {
                this.tdsReader.readBytes(b, offset + bytesRead, bytesToRead);
            }
            bytesRead += bytesToRead;
            this.currentChunkRemain -= bytesToRead;
        }
        if (bytesRead > 0) {
            if (this.isReadLimitSet && this.leftOverReadLimit > 0) {
                this.leftOverReadLimit -= bytesRead;
                if (this.leftOverReadLimit < 0) {
                    this.clearCurrentMark();
                }
            }
            return bytesRead;
        }
        if (-1 == this.currentChunkRemain) {
            return -1;
        }
        return 0;
    }

    @Override
    public void mark(int readLimit) {
        if (null != this.tdsReader && readLimit > 0) {
            this.currentMark = this.tdsReader.mark();
            this.markedChunkRemain = this.currentChunkRemain;
            this.leftOverReadLimit = readLimit;
            this.setReadLimit(readLimit);
        }
    }

    @Override
    public void close() throws IOException {
        if (null == this.tdsReader) {
            return;
        }
        while (this.skip(this.tdsReader.getConnection().getTDSPacketSize()) != 0L) {
        }
        this.closeHelper();
    }

    @Override
    public void reset() throws IOException {
        this.resetHelper();
        this.leftOverReadLimit = this.readLimit;
        this.currentChunkRemain = this.markedChunkRemain;
    }
}

