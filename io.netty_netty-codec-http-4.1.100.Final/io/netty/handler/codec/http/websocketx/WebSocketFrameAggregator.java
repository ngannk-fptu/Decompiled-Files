/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelPipeline
 *  io.netty.handler.codec.MessageAggregator
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketFrameAggregator
extends MessageAggregator<WebSocketFrame, WebSocketFrame, ContinuationWebSocketFrame, WebSocketFrame> {
    public WebSocketFrameAggregator(int maxContentLength) {
        super(maxContentLength);
    }

    protected boolean isStartMessage(WebSocketFrame msg) throws Exception {
        return msg instanceof TextWebSocketFrame || msg instanceof BinaryWebSocketFrame;
    }

    protected boolean isContentMessage(WebSocketFrame msg) throws Exception {
        return msg instanceof ContinuationWebSocketFrame;
    }

    protected boolean isLastContentMessage(ContinuationWebSocketFrame msg) throws Exception {
        return this.isContentMessage(msg) && msg.isFinalFragment();
    }

    protected boolean isAggregated(WebSocketFrame msg) throws Exception {
        if (msg.isFinalFragment()) {
            return !this.isContentMessage(msg);
        }
        return !this.isStartMessage(msg) && !this.isContentMessage(msg);
    }

    protected boolean isContentLengthInvalid(WebSocketFrame start, int maxContentLength) {
        return false;
    }

    protected Object newContinueResponse(WebSocketFrame start, int maxContentLength, ChannelPipeline pipeline) {
        return null;
    }

    protected boolean closeAfterContinueResponse(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected boolean ignoreContentAfterContinueResponse(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected WebSocketFrame beginAggregation(WebSocketFrame start, ByteBuf content) throws Exception {
        if (start instanceof TextWebSocketFrame) {
            return new TextWebSocketFrame(true, start.rsv(), content);
        }
        if (start instanceof BinaryWebSocketFrame) {
            return new BinaryWebSocketFrame(true, start.rsv(), content);
        }
        throw new Error();
    }
}

