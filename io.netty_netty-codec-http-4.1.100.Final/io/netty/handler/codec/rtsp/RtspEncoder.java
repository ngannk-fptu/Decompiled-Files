/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.handler.codec.UnsupportedMessageTypeException
 *  io.netty.util.AsciiString
 *  io.netty.util.CharsetUtil
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

public class RtspEncoder
extends HttpObjectEncoder<HttpMessage> {
    private static final int CRLF_SHORT = 3338;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && (msg instanceof HttpRequest || msg instanceof HttpResponse);
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpMessage message) throws Exception {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)message;
            ByteBufUtil.copy((AsciiString)request.method().asciiName(), (ByteBuf)buf);
            buf.writeByte(32);
            buf.writeCharSequence((CharSequence)request.uri(), CharsetUtil.UTF_8);
            buf.writeByte(32);
            buf.writeCharSequence((CharSequence)request.protocolVersion().toString(), CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
        } else if (message instanceof HttpResponse) {
            HttpResponse response = (HttpResponse)message;
            buf.writeCharSequence((CharSequence)response.protocolVersion().toString(), CharsetUtil.US_ASCII);
            buf.writeByte(32);
            ByteBufUtil.copy((AsciiString)response.status().codeAsText(), (ByteBuf)buf);
            buf.writeByte(32);
            buf.writeCharSequence((CharSequence)response.status().reasonPhrase(), CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
        } else {
            throw new UnsupportedMessageTypeException("Unsupported type " + StringUtil.simpleClassName((Object)message));
        }
    }
}

