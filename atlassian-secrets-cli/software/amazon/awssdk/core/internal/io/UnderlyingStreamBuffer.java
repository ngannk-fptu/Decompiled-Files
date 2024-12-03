/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.io;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
class UnderlyingStreamBuffer {
    private static final Logger log = Logger.loggerFor(UnderlyingStreamBuffer.class);
    private byte[] bufferArray;
    private int maxBufferSize;
    private int byteBuffered;
    private int pos = -1;
    private boolean bufferSizeOverflow;

    UnderlyingStreamBuffer(int maxBufferSize) {
        this.bufferArray = new byte[maxBufferSize];
        this.maxBufferSize = maxBufferSize;
    }

    public void buffer(byte read) {
        this.pos = -1;
        if (this.byteBuffered >= this.maxBufferSize) {
            log.debug(() -> "Buffer size " + this.maxBufferSize + " has been exceeded and the input stream will not be repeatable. Freeing buffer memory");
            this.bufferSizeOverflow = true;
        } else {
            this.bufferArray[this.byteBuffered++] = read;
        }
    }

    public void buffer(byte[] src, int srcPos, int length) {
        this.pos = -1;
        if (this.byteBuffered + length > this.maxBufferSize) {
            log.debug(() -> "Buffer size " + this.maxBufferSize + " has been exceeded and the input stream will not be repeatable. Freeing buffer memory");
            this.bufferSizeOverflow = true;
        } else {
            System.arraycopy(src, srcPos, this.bufferArray, this.byteBuffered, length);
            this.byteBuffered += length;
        }
    }

    public boolean hasNext() {
        return this.pos != -1 && this.pos < this.byteBuffered;
    }

    public byte next() {
        return this.bufferArray[this.pos++];
    }

    public void startReadBuffer() {
        if (this.bufferSizeOverflow) {
            throw SdkClientException.builder().message("The input stream is not repeatable since the buffer size " + this.maxBufferSize + " has been exceeded.").build();
        }
        this.pos = 0;
    }
}

