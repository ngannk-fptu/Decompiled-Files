/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.itu_t4;

import java.io.IOException;
import java.io.InputStream;

class BitInputStreamFlexible
extends InputStream {
    private final InputStream is;
    private int cache;
    private int cacheBitsRemaining;
    private long bytesRead;

    BitInputStreamFlexible(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        if (this.cacheBitsRemaining > 0) {
            throw new IOException("BitInputStream: incomplete bit read");
        }
        return this.is.read();
    }

    public final int readBits(int count) throws IOException {
        if (count <= 32) {
            int result = 0;
            if (this.cacheBitsRemaining > 0) {
                if (count >= this.cacheBitsRemaining) {
                    result = (1 << this.cacheBitsRemaining) - 1 & this.cache;
                    count -= this.cacheBitsRemaining;
                    this.cacheBitsRemaining = 0;
                } else {
                    this.cacheBitsRemaining -= count;
                    result = (1 << count) - 1 & this.cache >> this.cacheBitsRemaining;
                    count = 0;
                }
            }
            while (count >= 8) {
                this.cache = this.is.read();
                if (this.cache < 0) {
                    throw new IOException("couldn't read bits");
                }
                ++this.bytesRead;
                result = result << 8 | 0xFF & this.cache;
                count -= 8;
            }
            if (count > 0) {
                this.cache = this.is.read();
                if (this.cache < 0) {
                    throw new IOException("couldn't read bits");
                }
                ++this.bytesRead;
                this.cacheBitsRemaining = 8 - count;
                result = result << count | (1 << count) - 1 & this.cache >> this.cacheBitsRemaining;
                count = 0;
            }
            return result;
        }
        throw new IOException("BitInputStream: unknown error");
    }

    public void flushCache() {
        this.cacheBitsRemaining = 0;
    }

    public long getBytesRead() {
        return this.bytesRead;
    }
}

