/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.util.CharsetUtil
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class LineEncoder
extends MessageToMessageEncoder<CharSequence> {
    private final Charset charset;
    private final byte[] lineSeparator;

    public LineEncoder() {
        this(LineSeparator.DEFAULT, CharsetUtil.UTF_8);
    }

    public LineEncoder(LineSeparator lineSeparator) {
        this(lineSeparator, CharsetUtil.UTF_8);
    }

    public LineEncoder(Charset charset) {
        this(LineSeparator.DEFAULT, charset);
    }

    public LineEncoder(LineSeparator lineSeparator, Charset charset) {
        this.charset = (Charset)ObjectUtil.checkNotNull((Object)charset, (String)"charset");
        this.lineSeparator = ((LineSeparator)ObjectUtil.checkNotNull((Object)lineSeparator, (String)"lineSeparator")).value().getBytes(charset);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        ByteBuf buffer = ByteBufUtil.encodeString((ByteBufAllocator)ctx.alloc(), (CharBuffer)CharBuffer.wrap(msg), (Charset)this.charset, (int)this.lineSeparator.length);
        buffer.writeBytes(this.lineSeparator);
        out.add(buffer);
    }
}

