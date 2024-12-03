/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import software.amazon.ion.impl.bin.BlockAllocator;

abstract class BlockAllocatorProvider {
    BlockAllocatorProvider() {
    }

    public abstract BlockAllocator vendAllocator(int var1);
}

