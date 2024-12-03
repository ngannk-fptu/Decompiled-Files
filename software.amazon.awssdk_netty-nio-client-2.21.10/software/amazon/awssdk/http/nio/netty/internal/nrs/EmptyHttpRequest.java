/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.handler.codec.http.DefaultHttpHeaders
 *  io.netty.handler.codec.http.DefaultHttpRequest
 *  io.netty.handler.codec.http.FullHttpRequest
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpMethod
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.ReferenceCounted
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateHttpRequest;

@SdkInternalApi
class EmptyHttpRequest
extends DelegateHttpRequest
implements FullHttpRequest {
    EmptyHttpRequest(HttpRequest request) {
        super(request);
    }

    public FullHttpRequest setUri(String uri) {
        super.setUri(uri);
        return this;
    }

    public FullHttpRequest setMethod(HttpMethod method) {
        super.setMethod(method);
        return this;
    }

    public FullHttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public FullHttpRequest copy() {
        if (this.request instanceof FullHttpRequest) {
            return new EmptyHttpRequest((HttpRequest)((FullHttpRequest)this.request).copy());
        }
        DefaultHttpRequest copy = new DefaultHttpRequest(this.protocolVersion(), this.method(), this.uri());
        copy.headers().set(this.headers());
        return new EmptyHttpRequest((HttpRequest)copy);
    }

    public FullHttpRequest retain(int increment) {
        ReferenceCountUtil.retain((Object)this.message, (int)increment);
        return this;
    }

    public FullHttpRequest retain() {
        ReferenceCountUtil.retain((Object)this.message);
        return this;
    }

    public FullHttpRequest touch() {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).touch();
        }
        return this;
    }

    public FullHttpRequest touch(Object o) {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).touch(o);
        }
        return this;
    }

    public HttpHeaders trailingHeaders() {
        return new DefaultHttpHeaders();
    }

    public FullHttpRequest duplicate() {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).duplicate();
        }
        return this;
    }

    public FullHttpRequest retainedDuplicate() {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).retainedDuplicate();
        }
        return this;
    }

    public FullHttpRequest replace(ByteBuf byteBuf) {
        if (this.message instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).replace(byteBuf);
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

