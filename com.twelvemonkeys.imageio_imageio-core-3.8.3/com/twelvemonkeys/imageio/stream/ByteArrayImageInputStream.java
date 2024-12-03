/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.stream;

import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import javax.imageio.stream.ImageInputStreamImpl;

public final class ByteArrayImageInputStream
extends ImageInputStreamImpl {
    private final byte[] data;
    private final int dataOffset;
    private final int dataLength;

    public ByteArrayImageInputStream(byte[] byArray) {
        this(byArray, 0, byArray != null ? byArray.length : -1);
    }

    public ByteArrayImageInputStream(byte[] byArray, int n, int n2) {
        this.data = (byte[])Validate.notNull((Object)byArray, (String)"data");
        this.dataOffset = ByteArrayImageInputStream.isBetween(0, byArray.length, n, "offset");
        this.dataLength = ByteArrayImageInputStream.isBetween(0, byArray.length - n, n2, "length");
    }

    private static int isBetween(int n, int n2, int n3, String string) {
        return (Integer)Validate.isTrue((n3 >= n && n3 <= n2 ? 1 : 0) != 0, (Object)n3, (String)String.format("%s out of range [%d, %d]: %d", string, n, n2, n3));
    }

    @Override
    public int read() throws IOException {
        if (this.streamPos >= (long)this.dataLength) {
            return -1;
        }
        this.bitOffset = 0;
        return this.data[(int)this.streamPos++ + this.dataOffset] & 0xFF;
    }

    @Override
    public int read(byte[] byArray, int n, int n2) throws IOException {
        if (this.streamPos >= (long)this.dataLength) {
            return -1;
        }
        int n3 = (int)Math.min((long)this.dataLength - this.streamPos, (long)n2);
        this.bitOffset = 0;
        System.arraycopy(this.data, (int)this.streamPos + this.dataOffset, byArray, n, n3);
        this.streamPos += (long)n3;
        return n3;
    }

    @Override
    public long length() {
        return this.dataLength;
    }

    @Override
    public boolean isCached() {
        return true;
    }

    @Override
    public boolean isCachedMemory() {
        return true;
    }
}

