/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.internal.SdkInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Deprecated
public class RepeatableInputStream
extends SdkInputStream {
    private static final Log log = LogFactory.getLog(RepeatableInputStream.class);
    private InputStream is;
    private int bufferSize;
    private int bufferOffset;
    private long bytesReadPastMark;
    private byte[] buffer;
    private boolean hasWarnedBufferOverflow;

    public RepeatableInputStream(InputStream inputStream, int bufferSize) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        this.is = inputStream;
        this.bufferSize = bufferSize;
        this.buffer = new byte[this.bufferSize];
        if (log.isDebugEnabled()) {
            log.debug((Object)("Underlying input stream will be repeatable up to " + this.buffer.length + " bytes"));
        }
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        if (this.bytesReadPastMark <= (long)this.bufferSize) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Reset after reading " + this.bytesReadPastMark + " bytes."));
            }
        } else {
            throw new IOException("Input stream cannot be reset as " + this.bytesReadPastMark + " bytes have been written, exceeding the available buffer size of " + this.bufferSize);
        }
        this.bufferOffset = 0;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Input stream marked at " + this.bytesReadPastMark + " bytes"));
        }
        if (this.bytesReadPastMark <= (long)this.bufferSize && this.buffer != null) {
            byte[] newBuffer = new byte[this.bufferSize];
            System.arraycopy(this.buffer, this.bufferOffset, newBuffer, 0, (int)(this.bytesReadPastMark - (long)this.bufferOffset));
            this.buffer = newBuffer;
            this.bytesReadPastMark -= (long)this.bufferOffset;
            this.bufferOffset = 0;
        } else {
            this.bufferOffset = 0;
            this.bytesReadPastMark = 0L;
            this.buffer = new byte[this.bufferSize];
        }
    }

    @Override
    public int available() throws IOException {
        this.abortIfNeeded();
        return this.is.available();
    }

    @Override
    public void close() throws IOException {
        this.is.close();
        this.abortIfNeeded();
    }

    @Override
    public int read(byte[] out, int outOffset, int outLength) throws IOException {
        this.abortIfNeeded();
        if ((long)this.bufferOffset < this.bytesReadPastMark && this.buffer != null) {
            int bytesFromBuffer = outLength;
            if ((long)(this.bufferOffset + bytesFromBuffer) > this.bytesReadPastMark) {
                bytesFromBuffer = (int)this.bytesReadPastMark - this.bufferOffset;
            }
            System.arraycopy(this.buffer, this.bufferOffset, out, outOffset, bytesFromBuffer);
            this.bufferOffset += bytesFromBuffer;
            return bytesFromBuffer;
        }
        int count = this.is.read(out, outOffset, outLength);
        if (count <= 0) {
            return count;
        }
        if (this.bytesReadPastMark + (long)count <= (long)this.bufferSize) {
            System.arraycopy(out, outOffset, this.buffer, (int)this.bytesReadPastMark, count);
            this.bufferOffset += count;
        } else {
            if (!this.hasWarnedBufferOverflow) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Buffer size " + this.bufferSize + " has been exceeded and the input stream will not be repeatable until the next mark. Freeing buffer memory"));
                }
                this.hasWarnedBufferOverflow = true;
            }
            this.buffer = null;
        }
        this.bytesReadPastMark += (long)count;
        return count;
    }

    @Override
    public int read() throws IOException {
        this.abortIfNeeded();
        byte[] tmp = new byte[1];
        int count = this.read(tmp);
        if (count != -1) {
            int unsignedByte = tmp[0] & 0xFF;
            return unsignedByte;
        }
        return count;
    }

    @Override
    public InputStream getWrappedInputStream() {
        return this.is;
    }
}

