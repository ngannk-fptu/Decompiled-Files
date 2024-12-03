/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.io;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
class ChunkContentIterator {
    private final byte[] bytes;
    private int pos;

    ChunkContentIterator(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean hasNext() {
        return this.pos < this.bytes.length;
    }

    public int read(byte[] output, int offset, int length) {
        if (length == 0) {
            return 0;
        }
        if (!this.hasNext()) {
            return -1;
        }
        int remaingBytesNum = this.bytes.length - this.pos;
        int bytesToRead = Math.min(remaingBytesNum, length);
        System.arraycopy(this.bytes, this.pos, output, offset, bytesToRead);
        this.pos += bytesToRead;
        return bytesToRead;
    }
}

