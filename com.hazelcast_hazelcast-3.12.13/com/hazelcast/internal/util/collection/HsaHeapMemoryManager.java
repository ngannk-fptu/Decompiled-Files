/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.collection;

import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.internal.memory.MemoryManager;

public class HsaHeapMemoryManager
implements MemoryManager {
    private static final int BLOCK_INDEX_BIT = 62;
    private static final int ALIGNMENT_BITS = 7;
    private static final int ADDR_TO_ARRAY_INDEX_SHIFT = 3;
    private static final int LOWEST_ADDRESS = 8;
    private final long[][] blocks = new long[2][];
    private final Allocator malloc = new Allocator();
    private final Accessor mem = new Accessor();

    @Override
    public MemoryAllocator getAllocator() {
        return this.malloc;
    }

    @Override
    public MemoryAccessor getAccessor() {
        return this.mem;
    }

    @Override
    public void dispose() {
        this.malloc.dispose();
    }

    public long getUsedMemory() {
        long used = 0L;
        for (long[] block : this.blocks) {
            used += (long)(block != null ? block.length : 0);
        }
        return used;
    }

    final long[] addrToBlock(long address) {
        long[] block = this.blocks[HsaHeapMemoryManager.addrToBlockIndex(address)];
        assert (block != null) : "Attempt to access non-allocated address " + address;
        return block;
    }

    static boolean isAligned(long address) {
        return (address & 7L) == 0L;
    }

    static int addrToBlockIndex(long address) {
        assert (address >= 8L && HsaHeapMemoryManager.isAligned(address)) : "Invalid address " + address;
        return (int)(address - 8L >> 62);
    }

    static int addrToArrayIndex(long address) {
        return (int)(address - 8L >> 3);
    }

    private final class Accessor
    implements MemoryAccessor {
        private Accessor() {
        }

        @Override
        public boolean isBigEndian() {
            return false;
        }

        @Override
        public long getLong(long address) {
            return HsaHeapMemoryManager.this.addrToBlock(address)[HsaHeapMemoryManager.addrToArrayIndex(address)];
        }

        @Override
        public void putLong(long address, long x) {
            HsaHeapMemoryManager.this.addrToBlock((long)address)[HsaHeapMemoryManager.addrToArrayIndex((long)address)] = x;
        }

        @Override
        public void copyMemory(long srcAddress, long destAddress, long lengthBytes) {
            assert (HsaHeapMemoryManager.isAligned(srcAddress | destAddress | lengthBytes)) : String.format("Unaligned copyMemory(%x, %x, %x)", srcAddress, destAddress, lengthBytes);
            long[] srcArray = HsaHeapMemoryManager.this.addrToBlock(srcAddress);
            long[] destArray = HsaHeapMemoryManager.this.addrToBlock(destAddress);
            int srcIndexBase = HsaHeapMemoryManager.addrToArrayIndex(srcAddress);
            int destIndexBase = HsaHeapMemoryManager.addrToArrayIndex(destAddress);
            System.arraycopy(srcArray, srcIndexBase, destArray, destIndexBase, (int)(lengthBytes >> 3));
        }

        @Override
        public void copyFromByteArray(byte[] source, int offset, long destAddress, int length) {
            throw new UnsupportedOperationException("HsaHeapMemoryManager.Accessor.copyFromByteArray");
        }

        @Override
        public void copyToByteArray(long srcAddress, byte[] destination, int offset, int length) {
            throw new UnsupportedOperationException("HsaHeapMemoryManager.Accessor.copyToByteArray");
        }

        @Override
        public void setMemory(long address, long lengthBytes, byte value) {
            throw new UnsupportedOperationException("HsaHeapMemoryManager.Accessor.setMemory");
        }

        @Override
        public boolean getBoolean(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putBoolean(long address, boolean x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putByte(long address, byte x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char getChar(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putChar(long address, char x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putShort(long address, short x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putInt(long address, int x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putFloat(long address, float x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(long address) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putDouble(long address, double x) {
            throw new UnsupportedOperationException();
        }
    }

    private final class Allocator
    implements MemoryAllocator {
        private Allocator() {
        }

        @Override
        public long allocate(long size) {
            assert (size > 0L && size <= Integer.MAX_VALUE && HsaHeapMemoryManager.isAligned(size)) : "HsaHeapAllocator.allocate(" + size + ")";
            int emptyBlockIndex = this.findEmptyBlockIndex();
            ((HsaHeapMemoryManager)HsaHeapMemoryManager.this).blocks[emptyBlockIndex] = new long[(int)size];
            return ((long)emptyBlockIndex << 62) + 8L;
        }

        @Override
        public long reallocate(long address, long currentSize, long newSize) {
            throw new UnsupportedOperationException("HsaHeapAllocator.reallocate()");
        }

        @Override
        public void free(long address, long size) {
            int blockIndex = HsaHeapMemoryManager.addrToBlockIndex(address);
            long[] block = HsaHeapMemoryManager.this.blocks[blockIndex];
            assert (HsaHeapMemoryManager.addrToArrayIndex(address) == 0 && block != null && (long)block.length == size) : String.format("Misplaced HsaHeapAllocator.free(%x, %,d)", address, size);
            ((HsaHeapMemoryManager)HsaHeapMemoryManager.this).blocks[blockIndex] = null;
        }

        @Override
        public void dispose() {
            ((HsaHeapMemoryManager)HsaHeapMemoryManager.this).blocks[0] = null;
            ((HsaHeapMemoryManager)HsaHeapMemoryManager.this).blocks[1] = null;
        }

        private int findEmptyBlockIndex() {
            int emptySlot;
            int n = HsaHeapMemoryManager.this.blocks[0] == null ? 0 : (emptySlot = HsaHeapMemoryManager.this.blocks[1] == null ? 1 : -1);
            assert (emptySlot >= 0) : "Attempted to allocate a third block from HsaHeapAllocator";
            return emptySlot;
        }
    }
}

