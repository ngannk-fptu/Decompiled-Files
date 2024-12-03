/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateHttpResponse;

@SdkInternalApi
class EmptyHttpResponse
extends DelegateHttpResponse
implements FullHttpResponse {
    EmptyHttpResponse(HttpResponse response) {
        super(response);
    }

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public FullHttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public FullHttpResponse copy() {
        if (this.response instanceof FullHttpResponse) {
            return new EmptyHttpResponse(((FullHttpResponse)this.response).copy());
        }
        DefaultHttpResponse copy = new DefaultHttpResponse(this.protocolVersion(), this.status());
        copy.headers().set(this.headers());
        return new EmptyHttpResponse(copy);
    }

    @Override
    public FullHttpResponse retain(int increment) {
        ReferenceCountUtil.retain(this.message, increment);
        return this;
    }

    @Override
    public FullHttpResponse retain() {
        ReferenceCountUtil.retain(this.message);
        return this;
    }

    @Override
    public FullHttpResponse touch() {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).touch();
        }
        return this;
    }

    @Override
    public FullHttpResponse touch(Object o) {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).touch(o);
        }
        return this;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return new DefaultHttpHeaders();
    }

    @Override
    public FullHttpResponse duplicate() {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).duplicate();
        }
        return this;
    }

    @Override
    public FullHttpResponse retainedDuplicate() {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).retainedDuplicate();
        }
        return this;
    }

    @Override
    public FullHttpResponse replace(ByteBuf byteBuf) {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).replace(byteBuf);
        }
        return this;
    }

    @Override
    public ByteBuf content() {
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public int refCnt() {
        if (this.message instanceof ReferenceCounted) {
            return ((ReferenceCounted)((Object)this.message)).refCnt();
        }
        return 1;
    }

    @Override
    public boolean release() {
        return ReferenceCountUtil.release(this.message);
    }

    @Override
    public boolean release(int decrement) {
        return ReferenceCountUtil.release(this.message, decrement);
    }
}

