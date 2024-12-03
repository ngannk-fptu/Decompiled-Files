/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.HttpMethod
 *  io.netty.handler.codec.http.HttpRequest
 *  io.netty.handler.codec.http.HttpVersion
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateHttpMessage;

@SdkInternalApi
class DelegateHttpRequest
extends DelegateHttpMessage
implements HttpRequest {
    protected final HttpRequest request;

    DelegateHttpRequest(HttpRequest request) {
        super((HttpMessage)request);
        this.request = request;
    }

    public HttpRequest setMethod(HttpMethod method) {
        this.request.setMethod(method);
        return this;
    }

    public HttpRequest setUri(String uri) {
        this.request.setUri(uri);
        return this;
    }

    @Deprecated
    public HttpMethod getMethod() {
        return this.request.method();
    }

    public HttpMethod method() {
        return this.request.method();
    }

    @Deprecated
    public String getUri() {
        return this.request.uri();
    }

    public String uri() {
        return this.request.uri();
    }

    public HttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }
}

