/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.util.AsciiString
 *  io.netty.util.CharsetUtil
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

public class HttpRequestEncoder
extends HttpObjectEncoder<HttpRequest> {
    private static final char SLASH = '/';
    private static final char QUESTION_MARK = '?';
    private static final int SLASH_AND_SPACE_SHORT = 12064;
    private static final int SPACE_SLASH_AND_SPACE_MEDIUM = 0x202F20;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && !(msg instanceof HttpResponse);
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpRequest request) throws Exception {
        ByteBufUtil.copy((AsciiString)request.method().asciiName(), (ByteBuf)buf);
        String uri = request.uri();
        if (uri.isEmpty()) {
            ByteBufUtil.writeMediumBE((ByteBuf)buf, (int)0x202F20);
        } else {
            CharSequence uriCharSequence = uri;
            boolean needSlash = false;
            int start = uri.indexOf("://");
            if (start != -1 && uri.charAt(0) != '/') {
                int index = uri.indexOf(63, start += 3);
                if (index == -1) {
                    if (uri.lastIndexOf(47) < start) {
                        needSlash = true;
                    }
                } else if (uri.lastIndexOf(47, index) < start) {
                    uriCharSequence = new StringBuilder(uri).insert(index, '/');
                }
            }
            buf.writeByte(32).writeCharSequence(uriCharSequence, CharsetUtil.UTF_8);
            if (needSlash) {
                ByteBufUtil.writeShortBE((ByteBuf)buf, (int)12064);
            } else {
                buf.writeByte(32);
            }
        }
        request.protocolVersion().encode(buf);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
    }
}

