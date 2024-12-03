/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.util.Assert;

public class NettyDataBufferFactory
implements DataBufferFactory {
    private final ByteBufAllocator byteBufAllocator;

    public NettyDataBufferFactory(ByteBufAllocator byteBufAllocator) {
        Assert.notNull((Object)byteBufAllocator, "'byteBufAllocator' must not be null");
        this.byteBufAllocator = byteBufAllocator;
    }

    public ByteBufAllocator getByteBufAllocator() {
        return this.byteBufAllocator;
    }

    @Override
    public NettyDataBuffer allocateBuffer() {
        ByteBuf byteBuf = this.byteBufAllocator.buffer();
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override
    public NettyDataBuffer allocateBuffer(int initialCapacity) {
        ByteBuf byteBuf = this.byteBufAllocator.buffer(initialCapacity);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override
    public NettyDataBuffer wrap(ByteBuffer byteBuffer) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override
    public DataBuffer wrap(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        return new NettyDataBuffer(byteBuf, this);
    }

    @Override
    public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
        Assert.notNull(dataBuffers, "'dataBuffers' must not be null");
        CompositeByteBuf composite = this.byteBufAllocator.compositeBuffer(dataBuffers.size());
        for (DataBuffer dataBuffer : dataBuffers) {
            Assert.isInstanceOf(NettyDataBuffer.class, dataBuffer);
            NettyDataBuffer nettyDataBuffer = (NettyDataBuffer)dataBuffer;
            composite.addComponent(true, nettyDataBuffer.getNativeBuffer());
        }
        return new NettyDataBuffer(composite, this);
    }

    public NettyDataBuffer wrap(ByteBuf byteBuf) {
        return new NettyDataBuffer(byteBuf, this);
    }

    public static ByteBuf toByteBuf(DataBuffer buffer) {
        if (buffer instanceof NettyDataBuffer) {
            return ((NettyDataBuffer)buffer).getNativeBuffer();
        }
        return Unpooled.wrappedBuffer(buffer.asByteBuffer());
    }

    public String toString() {
        return "NettyDataBufferFactory (" + this.byteBufAllocator + ")";
    }
}

