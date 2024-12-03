/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.io.Closeable;
import software.amazon.ion.impl.bin.Block;

abstract class BlockAllocator
implements Closeable {
    BlockAllocator() {
    }

    public abstract Block allocateBlock();

    public abstract int getBlockSize();

    public abstract void close();
}

