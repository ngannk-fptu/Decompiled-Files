/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.handler.codec.DecoderResult
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;

final class ComposedLastHttpContent
implements LastHttpContent {
    private final HttpHeaders trailingHeaders;
    private DecoderResult result;

    ComposedLastHttpContent(HttpHeaders trailingHeaders) {
        this.trailingHeaders = trailingHeaders;
    }

    ComposedLastHttpContent(HttpHeaders trailingHeaders, DecoderResult result) {
        this(trailingHeaders);
        this.result = result;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public LastHttpContent copy() {
        DefaultLastHttpContent content = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        content.trailingHeaders().set(this.trailingHeaders());
        return content;
    }

    @Override
    public LastHttpContent duplicate() {
        return this.copy();
    }

    @Override
    public LastHttpContent retainedDuplicate() {
        return this.copy();
    }

    @Override
    public LastHttpContent replace(ByteBuf content) {
        DefaultLastHttpContent dup = new DefaultLastHttpContent(content);
        dup.trailingHeaders().setAll(this.trailingHeaders());
        return dup;
    }

    @Override
    public LastHttpContent retain(int increment) {
        return this;
    }

    @Override
    public LastHttpContent retain() {
        return this;
    }

    @Override
    public LastHttpContent touch() {
        return this;
    }

    @Override
    public LastHttpContent touch(Object hint) {
        return this;
    }

    public ByteBuf content() {
        return Unpooled.EMPTY_BUFFER;
    }

    public DecoderResult decoderResult() {
        return this.result;
    }

    @Override
    public DecoderResult getDecoderResult() {
        return this.decoderResult();
    }

    public void setDecoderResult(DecoderResult result) {
        this.result = result;
    }

    public int refCnt() {
        return 1;
    }

    public boolean release() {
        return false;
    }

    public boolean release(int decrement) {
        return false;
    }
}

