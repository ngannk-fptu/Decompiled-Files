/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseInputStream;
import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.ServerDTVImpl;
import com.microsoft.sqlserver.jdbc.TDSReader;
import java.io.IOException;
import java.util.logging.Level;

final class SimpleInputStream
extends BaseInputStream {
    private byte[] bSingleByte;

    SimpleInputStream(TDSReader tdsReader, int payLoadLength, InputStreamGetterArgs getterArgs, ServerDTVImpl dtv) {
        super(tdsReader, getterArgs.isAdaptive, getterArgs.isStreaming, dtv);
        this.setLoggingInfo(getterArgs.logContext);
        this.payloadLength = payLoadLength;
    }

    @Override
    public void close() throws IOException {
        if (null == this.tdsReader) {
            return;
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + "Enter Closing SimpleInputStream.");
        }
        this.skip((long)this.payloadLength - (long)this.streamPos);
        this.closeHelper();
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + "Exit Closing SimpleInputStream.");
        }
    }

    private boolean isEOS() {
        assert (this.streamPos <= this.payloadLength);
        return this.streamPos == this.payloadLength;
    }

    @Override
    public long skip(long n) throws IOException {
        this.checkClosed();
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " Skipping :" + n);
        }
        if (n < 0L) {
            return 0L;
        }
        if (this.isEOS()) {
            return 0L;
        }
        int skipAmount = (long)this.streamPos + n > (long)this.payloadLength ? this.payloadLength - this.streamPos : (int)n;
        try {
            this.tdsReader.skip(skipAmount);
        }
        catch (SQLServerException e) {
            throw new IOException(e.getMessage());
        }
        this.streamPos += skipAmount;
        if (this.isReadLimitSet && this.streamPos - this.markedStreamPos > this.readLimit) {
            this.clearCurrentMark();
        }
        return skipAmount;
    }

    @Override
    public int available() throws IOException {
        this.checkClosed();
        assert (this.streamPos <= this.payloadLength);
        int available = this.payloadLength - this.streamPos;
        if (this.tdsReader.available() < available) {
            available = this.tdsReader.available();
        }
        return available;
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (null == this.bSingleByte) {
            this.bSingleByte = new byte[1];
        }
        if (this.isEOS()) {
            return -1;
        }
        int bytesRead = this.read(this.bSingleByte, 0, 1);
        return 0 == bytesRead ? -1 : this.bSingleByte[0] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        this.checkClosed();
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int offset, int maxBytes) throws IOException {
        this.checkClosed();
        if (logger.isLoggable(Level.FINER)) {
            logger.finer(this.toString() + " Reading " + maxBytes + " from stream offset " + this.streamPos + " payload length " + this.payloadLength);
        }
        if (offset < 0 || maxBytes < 0 || offset + maxBytes > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (0 == maxBytes) {
            return 0;
        }
        if (this.isEOS()) {
            return -1;
        }
        int readAmount = this.streamPos + maxBytes > this.payloadLength ? this.payloadLength - this.streamPos : maxBytes;
        try {
            this.tdsReader.readBytes(b, offset, readAmount);
        }
        catch (SQLServerException e) {
            throw new IOException(e.getMessage());
        }
        this.streamPos += readAmount;
        if (this.isReadLimitSet && this.streamPos - this.markedStreamPos > this.readLimit) {
            this.clearCurrentMark();
        }
        return readAmount;
    }

    @Override
    public void mark(int readLimit) {
        if (null != this.tdsReader && readLimit > 0) {
            this.currentMark = this.tdsReader.mark();
            this.markedStreamPos = this.streamPos;
            this.setReadLimit(readLimit);
        }
    }

    @Override
    public void reset() throws IOException {
        this.resetHelper();
        this.streamPos = this.markedStreamPos;
    }

    @Override
    final byte[] getBytes() throws SQLServerException {
        assert (0 == this.streamPos);
        byte[] value = new byte[this.payloadLength];
        try {
            this.read(value);
            this.close();
        }
        catch (IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return value;
    }
}

