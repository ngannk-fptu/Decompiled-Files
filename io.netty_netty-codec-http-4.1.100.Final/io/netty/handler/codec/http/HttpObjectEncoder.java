/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufUtil
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.channel.FileRegion
 *  io.netty.handler.codec.EncoderException
 *  io.netty.handler.codec.MessageToMessageEncoder
 *  io.netty.util.CharsetUtil
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.concurrent.PromiseCombiner
 *  io.netty.util.internal.StringUtil
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeadersEncoder;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HttpObjectEncoder<H extends HttpMessage>
extends MessageToMessageEncoder<Object> {
    static final int CRLF_SHORT = 3338;
    private static final int ZERO_CRLF_MEDIUM = 3149066;
    private static final byte[] ZERO_CRLF_CRLF = new byte[]{48, 13, 10, 13, 10};
    private static final ByteBuf CRLF_BUF = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)2).writeByte(13).writeByte(10)).asReadOnly();
    private static final ByteBuf ZERO_CRLF_CRLF_BUF = Unpooled.unreleasableBuffer((ByteBuf)Unpooled.directBuffer((int)ZERO_CRLF_CRLF.length).writeBytes(ZERO_CRLF_CRLF)).asReadOnly();
    private static final float HEADERS_WEIGHT_NEW = 0.2f;
    private static final float HEADERS_WEIGHT_HISTORICAL = 0.8f;
    private static final float TRAILERS_WEIGHT_NEW = 0.2f;
    private static final float TRAILERS_WEIGHT_HISTORICAL = 0.8f;
    private static final int ST_INIT = 0;
    private static final int ST_CONTENT_NON_CHUNK = 1;
    private static final int ST_CONTENT_CHUNK = 2;
    private static final int ST_CONTENT_ALWAYS_EMPTY = 3;
    private int state = 0;
    private float headersEncodedSizeAccumulator = 256.0f;
    private float trailersEncodedSizeAccumulator = 256.0f;
    private final List<Object> out = new ArrayList<Object>();

    private static boolean checkContentState(int state) {
        return state == 2 || state == 1 || state == 3;
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (this.acceptOutboundMessage(msg)) {
                this.encode(ctx, msg, this.out);
                if (this.out.isEmpty()) {
                    throw new EncoderException(StringUtil.simpleClassName((Object)((Object)this)) + " must produce at least one message.");
                }
            } else {
                ctx.write(msg, promise);
            }
        }
        catch (EncoderException e) {
            throw e;
        }
        catch (Throwable t) {
            throw new EncoderException(t);
        }
        finally {
            HttpObjectEncoder.writeOutList(ctx, this.out, promise);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void writeOutList(ChannelHandlerContext ctx, List<Object> out, ChannelPromise promise) {
        int size = out.size();
        try {
            if (size == 1) {
                ctx.write(out.get(0), promise);
            } else if (size > 1) {
                if (promise == ctx.voidPromise()) {
                    HttpObjectEncoder.writeVoidPromise(ctx, out);
                } else {
                    HttpObjectEncoder.writePromiseCombiner(ctx, out, promise);
                }
            }
        }
        finally {
            out.clear();
        }
    }

    private static void writeVoidPromise(ChannelHandlerContext ctx, List<Object> out) {
        ChannelPromise voidPromise = ctx.voidPromise();
        for (int i = 0; i < out.size(); ++i) {
            ctx.write(out.get(i), voidPromise);
        }
    }

    private static void writePromiseCombiner(ChannelHandlerContext ctx, List<Object> out, ChannelPromise promise) {
        PromiseCombiner combiner = new PromiseCombiner(ctx.executor());
        for (int i = 0; i < out.size(); ++i) {
            combiner.add((Future)ctx.write(out.get(i)));
        }
        combiner.finish((Promise)promise);
    }

    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg == Unpooled.EMPTY_BUFFER) {
            out.add(Unpooled.EMPTY_BUFFER);
            return;
        }
        if (msg instanceof FullHttpMessage) {
            this.encodeFullHttpMessage(ctx, msg, out);
            return;
        }
        if (msg instanceof HttpMessage) {
            HttpMessage m;
            try {
                m = (HttpMessage)msg;
            }
            catch (Exception rethrow) {
                ReferenceCountUtil.release((Object)msg);
                throw rethrow;
            }
            if (m instanceof LastHttpContent) {
                this.encodeHttpMessageLastContent(ctx, m, out);
            } else if (m instanceof HttpContent) {
                this.encodeHttpMessageNotLastContent(ctx, m, out);
            } else {
                this.encodeJustHttpMessage(ctx, m, out);
            }
        } else {
            this.encodeNotHttpMessageContentTypes(ctx, msg, out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeJustHttpMessage(ChannelHandlerContext ctx, H m, List<Object> out) throws Exception {
        assert (!(m instanceof HttpContent));
        try {
            if (this.state != 0) {
                HttpObjectEncoder.throwUnexpectedMessageTypeEx(m, this.state);
            }
            ByteBuf buf = this.encodeInitHttpMessage(ctx, m);
            assert (HttpObjectEncoder.checkContentState(this.state));
            out.add(buf);
        }
        finally {
            ReferenceCountUtil.release(m);
        }
    }

    private void encodeByteBufHttpContent(int state, ChannelHandlerContext ctx, ByteBuf buf, ByteBuf content, HttpHeaders trailingHeaders, List<Object> out) {
        switch (state) {
            case 1: {
                if (HttpObjectEncoder.encodeContentNonChunk(out, buf, content)) break;
            }
            case 3: {
                out.add(buf);
                break;
            }
            case 2: {
                out.add(buf);
                this.encodeChunkedHttpContent(ctx, content, trailingHeaders, out);
                break;
            }
            default: {
                throw new Error();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeHttpMessageNotLastContent(ChannelHandlerContext ctx, H m, List<Object> out) throws Exception {
        assert (m instanceof HttpContent);
        assert (!(m instanceof LastHttpContent));
        HttpContent httpContent = (HttpContent)m;
        try {
            if (this.state != 0) {
                HttpObjectEncoder.throwUnexpectedMessageTypeEx(m, this.state);
            }
            ByteBuf buf = this.encodeInitHttpMessage(ctx, m);
            assert (HttpObjectEncoder.checkContentState(this.state));
            this.encodeByteBufHttpContent(this.state, ctx, buf, httpContent.content(), null, out);
        }
        finally {
            httpContent.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeHttpMessageLastContent(ChannelHandlerContext ctx, H m, List<Object> out) throws Exception {
        assert (m instanceof LastHttpContent);
        LastHttpContent httpContent = (LastHttpContent)m;
        try {
            if (this.state != 0) {
                HttpObjectEncoder.throwUnexpectedMessageTypeEx(m, this.state);
            }
            ByteBuf buf = this.encodeInitHttpMessage(ctx, m);
            assert (HttpObjectEncoder.checkContentState(this.state));
            this.encodeByteBufHttpContent(this.state, ctx, buf, httpContent.content(), httpContent.trailingHeaders(), out);
            this.state = 0;
        }
        finally {
            httpContent.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeNotHttpMessageContentTypes(ChannelHandlerContext ctx, Object msg, List<Object> out) {
        assert (!(msg instanceof HttpMessage));
        if (this.state == 0) {
            try {
                if (msg instanceof ByteBuf && HttpObjectEncoder.bypassEncoderIfEmpty((ByteBuf)msg, out)) {
                    return;
                }
                HttpObjectEncoder.throwUnexpectedMessageTypeEx(msg, 0);
            }
            finally {
                ReferenceCountUtil.release((Object)msg);
            }
        }
        if (msg == LastHttpContent.EMPTY_LAST_CONTENT) {
            this.state = HttpObjectEncoder.encodeEmptyLastHttpContent(this.state, out);
            return;
        }
        if (msg instanceof LastHttpContent) {
            this.encodeLastHttpContent(ctx, (LastHttpContent)msg, out);
            return;
        }
        if (msg instanceof HttpContent) {
            this.encodeHttpContent(ctx, (HttpContent)msg, out);
            return;
        }
        if (msg instanceof ByteBuf) {
            this.encodeByteBufContent(ctx, (ByteBuf)msg, out);
            return;
        }
        if (msg instanceof FileRegion) {
            this.encodeFileRegionContent(ctx, (FileRegion)msg, out);
            return;
        }
        try {
            HttpObjectEncoder.throwUnexpectedMessageTypeEx(msg, this.state);
        }
        finally {
            ReferenceCountUtil.release((Object)msg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeFullHttpMessage(ChannelHandlerContext ctx, Object o, List<Object> out) throws Exception {
        assert (o instanceof FullHttpMessage);
        FullHttpMessage msg = (FullHttpMessage)o;
        try {
            if (this.state != 0) {
                HttpObjectEncoder.throwUnexpectedMessageTypeEx(o, this.state);
            }
            HttpMessage m = (HttpMessage)o;
            ByteBuf buf = ctx.alloc().buffer((int)this.headersEncodedSizeAccumulator);
            this.encodeInitialLine(buf, m);
            int state = this.isContentAlwaysEmpty(m) ? 3 : (HttpUtil.isTransferEncodingChunked(m) ? 2 : 1);
            this.sanitizeHeadersBeforeEncode(m, state == 3);
            this.encodeHeaders(m.headers(), buf);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
            this.headersEncodedSizeAccumulator = 0.2f * (float)HttpObjectEncoder.padSizeForAccumulation(buf.readableBytes()) + 0.8f * this.headersEncodedSizeAccumulator;
            this.encodeByteBufHttpContent(state, ctx, buf, msg.content(), msg.trailingHeaders(), out);
        }
        finally {
            msg.release();
        }
    }

    private static boolean encodeContentNonChunk(List<Object> out, ByteBuf buf, ByteBuf content) {
        int contentLength = content.readableBytes();
        if (contentLength > 0) {
            if (buf.writableBytes() >= contentLength) {
                buf.writeBytes(content);
                out.add(buf);
            } else {
                out.add(buf);
                out.add(content.retain());
            }
            return true;
        }
        return false;
    }

    private static void throwUnexpectedMessageTypeEx(Object msg, int state) {
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName((Object)msg) + ", state: " + state);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void encodeFileRegionContent(ChannelHandlerContext ctx, FileRegion msg, List<Object> out) {
        try {
            assert (this.state != 0);
            switch (this.state) {
                case 1: {
                    if (msg.count() > 0L) {
                        out.add(msg.retain());
                        return;
                    }
                }
                case 3: {
                    out.add(Unpooled.EMPTY_BUFFER);
                    return;
                }
                case 2: {
                    HttpObjectEncoder.encodedChunkedFileRegionContent(ctx, msg, out);
                    return;
                }
                default: {
                    throw new Error();
                }
            }
        }
        finally {
            msg.release();
        }
    }

    private static boolean bypassEncoderIfEmpty(ByteBuf msg, List<Object> out) {
        if (!msg.isReadable()) {
            out.add(msg.retain());
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeByteBufContent(ChannelHandlerContext ctx, ByteBuf content, List<Object> out) {
        try {
            assert (this.state != 0);
            if (HttpObjectEncoder.bypassEncoderIfEmpty(content, out)) {
                return;
            }
            this.encodeByteBufAndTrailers(this.state, ctx, out, content, null);
        }
        finally {
            content.release();
        }
    }

    private static int encodeEmptyLastHttpContent(int state, List<Object> out) {
        assert (state != 0);
        switch (state) {
            case 1: 
            case 3: {
                out.add(Unpooled.EMPTY_BUFFER);
                break;
            }
            case 2: {
                out.add(ZERO_CRLF_CRLF_BUF.duplicate());
                break;
            }
            default: {
                throw new Error();
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeLastHttpContent(ChannelHandlerContext ctx, LastHttpContent msg, List<Object> out) {
        assert (this.state != 0);
        assert (!(msg instanceof HttpMessage));
        try {
            this.encodeByteBufAndTrailers(this.state, ctx, out, msg.content(), msg.trailingHeaders());
            this.state = 0;
        }
        finally {
            msg.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encodeHttpContent(ChannelHandlerContext ctx, HttpContent msg, List<Object> out) {
        assert (this.state != 0);
        assert (!(msg instanceof HttpMessage));
        assert (!(msg instanceof LastHttpContent));
        try {
            this.encodeByteBufAndTrailers(this.state, ctx, out, msg.content(), null);
        }
        finally {
            msg.release();
        }
    }

    private void encodeByteBufAndTrailers(int state, ChannelHandlerContext ctx, List<Object> out, ByteBuf content, HttpHeaders trailingHeaders) {
        switch (state) {
            case 1: {
                if (content.isReadable()) {
                    out.add(content.retain());
                    break;
                }
            }
            case 3: {
                out.add(Unpooled.EMPTY_BUFFER);
                break;
            }
            case 2: {
                this.encodeChunkedHttpContent(ctx, content, trailingHeaders, out);
                break;
            }
            default: {
                throw new Error();
            }
        }
    }

    private void encodeChunkedHttpContent(ChannelHandlerContext ctx, ByteBuf content, HttpHeaders trailingHeaders, List<Object> out) {
        int contentLength = content.readableBytes();
        if (contentLength > 0) {
            HttpObjectEncoder.addEncodedLengthHex(ctx, contentLength, out);
            out.add(content.retain());
            out.add(CRLF_BUF.duplicate());
        }
        if (trailingHeaders != null) {
            this.encodeTrailingHeaders(ctx, trailingHeaders, out);
        } else if (contentLength == 0) {
            out.add(content.retain());
        }
    }

    private void encodeTrailingHeaders(ChannelHandlerContext ctx, HttpHeaders trailingHeaders, List<Object> out) {
        if (trailingHeaders.isEmpty()) {
            out.add(ZERO_CRLF_CRLF_BUF.duplicate());
        } else {
            ByteBuf buf = ctx.alloc().buffer((int)this.trailersEncodedSizeAccumulator);
            ByteBufUtil.writeMediumBE((ByteBuf)buf, (int)3149066);
            this.encodeHeaders(trailingHeaders, buf);
            ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
            this.trailersEncodedSizeAccumulator = 0.2f * (float)HttpObjectEncoder.padSizeForAccumulation(buf.readableBytes()) + 0.8f * this.trailersEncodedSizeAccumulator;
            out.add(buf);
        }
    }

    private ByteBuf encodeInitHttpMessage(ChannelHandlerContext ctx, H m) throws Exception {
        assert (this.state == 0);
        ByteBuf buf = ctx.alloc().buffer((int)this.headersEncodedSizeAccumulator);
        this.encodeInitialLine(buf, m);
        this.state = this.isContentAlwaysEmpty(m) ? 3 : (HttpUtil.isTransferEncodingChunked(m) ? 2 : 1);
        this.sanitizeHeadersBeforeEncode(m, this.state == 3);
        this.encodeHeaders(m.headers(), buf);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
        this.headersEncodedSizeAccumulator = 0.2f * (float)HttpObjectEncoder.padSizeForAccumulation(buf.readableBytes()) + 0.8f * this.headersEncodedSizeAccumulator;
        return buf;
    }

    protected void encodeHeaders(HttpHeaders headers, ByteBuf buf) {
        Iterator<Map.Entry<CharSequence, CharSequence>> iter = headers.iteratorCharSequence();
        while (iter.hasNext()) {
            Map.Entry<CharSequence, CharSequence> header = iter.next();
            HttpHeadersEncoder.encoderHeader(header.getKey(), header.getValue(), buf);
        }
    }

    private static void encodedChunkedFileRegionContent(ChannelHandlerContext ctx, FileRegion msg, List<Object> out) {
        long contentLength = msg.count();
        if (contentLength > 0L) {
            HttpObjectEncoder.addEncodedLengthHex(ctx, contentLength, out);
            out.add(msg.retain());
            out.add(CRLF_BUF.duplicate());
        } else if (contentLength == 0L) {
            out.add(msg.retain());
        }
    }

    private static void addEncodedLengthHex(ChannelHandlerContext ctx, long contentLength, List<Object> out) {
        String lengthHex = Long.toHexString(contentLength);
        ByteBuf buf = ctx.alloc().buffer(lengthHex.length() + 2);
        buf.writeCharSequence((CharSequence)lengthHex, CharsetUtil.US_ASCII);
        ByteBufUtil.writeShortBE((ByteBuf)buf, (int)3338);
        out.add(buf);
    }

    protected void sanitizeHeadersBeforeEncode(H msg, boolean isAlwaysEmpty) {
    }

    protected boolean isContentAlwaysEmpty(H msg) {
        return false;
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg == Unpooled.EMPTY_BUFFER || msg == LastHttpContent.EMPTY_LAST_CONTENT || msg instanceof FullHttpMessage || msg instanceof HttpMessage || msg instanceof LastHttpContent || msg instanceof HttpContent || msg instanceof ByteBuf || msg instanceof FileRegion;
    }

    private static int padSizeForAccumulation(int readableBytes) {
        return (readableBytes << 2) / 3;
    }

    @Deprecated
    protected static void encodeAscii(String s, ByteBuf buf) {
        buf.writeCharSequence((CharSequence)s, CharsetUtil.US_ASCII);
    }

    protected abstract void encodeInitialLine(ByteBuf var1, H var2) throws Exception;
}

