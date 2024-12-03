/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.bin;

import software.amazon.ion.impl.bin.Block;
import software.amazon.ion.impl.bin.BlockAllocator;
import software.amazon.ion.impl.bin.BlockAllocatorProvider;

final class BlockAllocatorProviders {
    private static final BlockAllocatorProvider BASIC_PROVIDER = new BasicBlockAllocatorProvider();

    private BlockAllocatorProviders() {
    }

    public static BlockAllocatorProvider basicProvider() {
        return BASIC_PROVIDER;
    }

    private static final class BasicBlockAllocatorProvider
    extends BlockAllocatorProvider {
        private BasicBlockAllocatorProvider() {
        }

        public BlockAllocator vendAllocator(final int blockSize) {
            return new BlockAllocator(){

                public Block allocateBlock() {
                    return new Block(new byte[blockSize]){

                        public void close() {
                        }
                    };
                }

                public int getBlockSize() {
                    return blockSize;
                }

                public void close() {
                }
            };
        }
    }
}

