/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import software.amazon.ion.impl.bin.Block;
import software.amazon.ion.impl.bin.BlockAllocator;
import software.amazon.ion.impl.bin.BlockAllocatorProvider;

final class PooledBlockAllocatorProvider
extends BlockAllocatorProvider {
    private final ConcurrentMap<Integer, BlockAllocator> allocators = new ConcurrentHashMap<Integer, BlockAllocator>();

    public BlockAllocator vendAllocator(int blockSize) {
        if (blockSize <= 0) {
            throw new IllegalArgumentException("Invalid block size: " + blockSize);
        }
        BlockAllocator allocator = (BlockAllocator)this.allocators.get(blockSize);
        if (allocator == null) {
            allocator = new PooledBlockAllocator(blockSize);
            BlockAllocator existingAllocator = this.allocators.putIfAbsent(blockSize, allocator);
            if (existingAllocator != null) {
                allocator = existingAllocator;
            }
        }
        return allocator;
    }

    private final class PooledBlockAllocator
    extends BlockAllocator {
        private final int blockSize;
        private final ConcurrentLinkedQueue<Block> freeBlocks;

        public PooledBlockAllocator(int blockSize) {
            this.blockSize = blockSize;
            this.freeBlocks = new ConcurrentLinkedQueue();
        }

        public Block allocateBlock() {
            Block block = this.freeBlocks.poll();
            if (block == null) {
                block = new Block(new byte[this.blockSize]){

                    public void close() {
                        this.reset();
                        PooledBlockAllocator.this.freeBlocks.add(this);
                    }
                };
            }
            return block;
        }

        public int getBlockSize() {
            return this.blockSize;
        }

        public void close() {
        }
    }
}

