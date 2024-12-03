/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.handler.codec.http.DefaultHttpHeaders
 *  io.netty.handler.codec.http.DefaultHttpResponse
 *  io.netty.handler.codec.http.FullHttpResponse
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpResponse
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.ReferenceCounted
 *  software.amazon.awssdk.annotations.SdkInternalApi
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

    public FullHttpResponse setStatus(HttpResponseStatus status) {
        super.setStatus(status);
        return this;
    }

    public FullHttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public FullHttpResponse copy() {
        if (this.response instanceof FullHttpResponse) {
            return new EmptyHttpResponse((HttpResponse)((FullHttpResponse)this.response).copy());
        }
        DefaultHttpResponse copy = new DefaultHttpResponse(this.protocolVersion(), this.status());
        copy.headers().set(this.headers());
        return new EmptyHttpResponse((HttpResponse)copy);
    }

    public FullHttpResponse retain(int increment) {
        ReferenceCountUtil.retain((Object)this.message, (int)increment);
        return this;
    }

    public FullHttpResponse retain() {
        ReferenceCountUtil.retain((Object)this.message);
        return this;
    }

    public FullHttpResponse touch() {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).touch();
        }
        return this;
    }

    public FullHttpResponse touch(Object o) {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).touch(o);
        }
        return this;
    }

    public HttpHeaders trailingHeaders() {
        return new DefaultHttpHeaders();
    }

    public FullHttpResponse duplicate() {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).duplicate();
        }
        return this;
    }

    public FullHttpResponse retainedDuplicate() {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).retainedDuplicate();
        }
        return this;
    }

    public FullHttpResponse replace(ByteBuf byteBuf) {
        if (this.response instanceof FullHttpResponse) {
            return ((FullHttpResponse)this.response).replace(byteBuf);
        }
        return this;
    }

    public ByteBuf content() {
        return Unpooled.EMPTY_BUFFER;
    }

    public int refCnt() {
        if (this.message instanceof ReferenceCounted) {
            return ((ReferenceCounted)this.message).refCnt();
        }
        return 1;
    }

    public boolean release() {
        return ReferenceCountUtil.release((Object)this.message);
    }

    public boolean release(int decrement) {
        return ReferenceCountUtil.release((Object)this.message, (int)decrement);
    }
}

