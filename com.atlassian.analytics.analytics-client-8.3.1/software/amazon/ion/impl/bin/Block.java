/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.Closeable;

abstract class Block
implements Closeable {
    public final byte[] data;
    public int limit;

    Block(byte[] data) {
        this.data = data;
        this.limit = 0;
    }

    public final void reset() {
        this.limit = 0;
    }

    public final int remaining() {
        return this.data.length - this.limit;
    }

    public final int capacity() {
        return this.data.length;
    }

    public abstract void close();
}

