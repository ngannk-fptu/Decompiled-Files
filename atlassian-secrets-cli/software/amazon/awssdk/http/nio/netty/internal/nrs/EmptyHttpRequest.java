/*
 * Decompiled with CFR 0.152.
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

    @Override
    public FullHttpRequest setUri(String uri) {
        super.setUri(uri);
        return this;
    }

    @Override
    public FullHttpRequest setMethod(HttpMethod method) {
        super.setMethod(method);
        return this;
    }

    @Override
    public FullHttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public FullHttpRequest copy() {
        if (this.request instanceof FullHttpRequest) {
            return new EmptyHttpRequest(((FullHttpRequest)this.request).copy());
        }
        DefaultHttpRequest copy = new DefaultHttpRequest(this.protocolVersion(), this.method(), this.uri());
        copy.headers().set(this.headers());
        return new EmptyHttpRequest(copy);
    }

    @Override
    public FullHttpRequest retain(int increment) {
        ReferenceCountUtil.retain(this.message, increment);
        return this;
    }

    @Override
    public FullHttpRequest retain() {
        ReferenceCountUtil.retain(this.message);
        return this;
    }

    @Override
    public FullHttpRequest touch() {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).touch();
        }
        return this;
    }

    @Override
    public FullHttpRequest touch(Object o) {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).touch(o);
        }
        return this;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return new DefaultHttpHeaders();
    }

    @Override
    public FullHttpRequest duplicate() {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).duplicate();
        }
        return this;
    }

    @Override
    public FullHttpRequest retainedDuplicate() {
        if (this.request instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).retainedDuplicate();
        }
        return this;
    }

    @Override
    public FullHttpRequest replace(ByteBuf byteBuf) {
        if (this.message instanceof FullHttpRequest) {
            return ((FullHttpRequest)this.request).replace(byteBuf);
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

