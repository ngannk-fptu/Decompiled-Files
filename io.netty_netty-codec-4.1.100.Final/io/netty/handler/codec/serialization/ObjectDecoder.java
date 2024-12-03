/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.channel.ChannelHandlerContext
 */
package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.CompactObjectInputStream;
import java.io.InputStream;

@Deprecated
public class ObjectDecoder
extends LengthFieldBasedFrameDecoder {
    private final ClassResolver classResolver;

    public ObjectDecoder(ClassResolver classResolver) {
        this(0x100000, classResolver);
    }

    public ObjectDecoder(int maxObjectSize, ClassResolver classResolver) {
        super(maxObjectSize, 0, 4, 0, 4);
        this.classResolver = classResolver;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf)super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        CompactObjectInputStream ois = new CompactObjectInputStream((InputStream)new ByteBufInputStream(frame, true), this.classResolver);
        try {
            Object object = ois.readObject();
            return object;
        }
        finally {
            ois.close();
        }
    }
}

