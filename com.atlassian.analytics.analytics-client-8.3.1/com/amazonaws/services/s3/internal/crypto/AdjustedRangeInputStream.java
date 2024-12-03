/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto;

import com.amazonaws.internal.SdkInputStream;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;

public class AdjustedRangeInputStream
extends SdkInputStream {
    private InputStream decryptedContents;
    private long virtualAvailable;
    private boolean closed;

    public AdjustedRangeInputStream(InputStream objectContents, long rangeBeginning, long rangeEnd) throws IOException {
        this.decryptedContents = objectContents;
        this.closed = false;
        this.initializeForRead(rangeBeginning, rangeEnd);
    }

    private void initializeForRead(long rangeBeginning, long rangeEnd) throws IOException {
        int numBytesToSkip;
        if (rangeBeginning < 16L) {
            numBytesToSkip = (int)rangeBeginning;
        } else {
            int offsetIntoBlock = (int)(rangeBeginning % 16L);
            numBytesToSkip = 16 + offsetIntoBlock;
        }
        if (numBytesToSkip != 0) {
            while (numBytesToSkip > 0) {
                this.decryptedContents.read();
                --numBytesToSkip;
            }
        }
        this.virtualAvailable = rangeEnd - rangeBeginning + 1L;
    }

    @Override
    public int read() throws IOException {
        this.abortIfNeeded();
        int result = this.virtualAvailable <= 0L ? -1 : this.decryptedContents.read();
        if (result != -1) {
            --this.virtualAvailable;
        } else {
            this.virtualAvailable = 0L;
            this.close();
        }
        return result;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        int numBytesRead;
        this.abortIfNeeded();
        if (this.virtualAvailable <= 0L) {
            numBytesRead = -1;
        } else {
            if ((long)length > this.virtualAvailable) {
                length = this.virtualAvailable < Integer.MAX_VALUE ? (int)this.virtualAvailable : Integer.MAX_VALUE;
            }
            numBytesRead = this.decryptedContents.read(buffer, offset, length);
        }
        if (numBytesRead != -1) {
            this.virtualAvailable -= (long)numBytesRead;
        } else {
            this.virtualAvailable = 0L;
            this.close();
        }
        return numBytesRead;
    }

    @Override
    public int available() throws IOException {
        this.abortIfNeeded();
        int available = this.decryptedContents.available();
        if ((long)available < this.virtualAvailable) {
            return available;
        }
        return (int)this.virtualAvailable;
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (this.virtualAvailable == 0L) {
                IOUtils.drainInputStream(this.decryptedContents);
            }
            this.decryptedContents.close();
        }
        this.abortIfNeeded();
    }

    @Override
    protected InputStream getWrappedInputStream() {
        return this.decryptedContents;
    }
}

