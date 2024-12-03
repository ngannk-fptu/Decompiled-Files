/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.PlatformDependent
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ReadOnlyByteBufferBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class ReadOnlyUnsafeDirectByteBuf
extends ReadOnlyByteBufferBuf {
    private final long memoryAddress;

    ReadOnlyUnsafeDirectByteBuf(ByteBufAllocator allocator, ByteBuffer byteBuffer) {
        super(allocator, byteBuffer);
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)this.buffer);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte(this.addr(index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort(this.addr(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium(this.addr(index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt(this.addr(index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong(this.addr(index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkIndex(index, length);
        ObjectUtil.checkNotNull((Object)dst, (String)"dst");
        if (dstIndex < 0 || dstIndex > dst.capacity() - length) {
            throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
        }
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory((long)this.addr(index), (long)(dst.memoryAddress() + (long)dstIndex), (long)length);
        } else if (dst.hasArray()) {
            PlatformDependent.copyMemory((long)this.addr(index), (byte[])dst.array(), (int)(dst.arrayOffset() + dstIndex), (long)length);
        } else {
            dst.setBytes(dstIndex, this, index, length);
        }
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkIndex(index, length);
        ObjectUtil.checkNotNull((Object)dst, (String)"dst");
        if (dstIndex < 0 || dstIndex > dst.length - length) {
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dst.length));
        }
        if (length != 0) {
            PlatformDependent.copyMemory((long)this.addr(index), (byte[])dst, (int)dstIndex, (long)length);
        }
        return this;
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        ByteBuf copy = this.alloc().directBuffer(length, this.maxCapacity());
        if (length != 0) {
            if (copy.hasMemoryAddress()) {
                PlatformDependent.copyMemory((long)this.addr(index), (long)copy.memoryAddress(), (long)length);
                copy.setIndex(0, length);
            } else {
                copy.writeBytes(this, index, length);
            }
        }
        return copy;
    }

    @Override
    public boolean hasMemoryAddress() {
        return true;
    }

    @Override
    public long memoryAddress() {
        return this.memoryAddress;
    }

    private long addr(int index) {
        return this.memoryAddress + (long)index;
    }
}

