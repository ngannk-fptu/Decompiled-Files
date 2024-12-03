/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

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
        super(request);
        this.request = request;
    }

    @Override
    public HttpRequest setMethod(HttpMethod method) {
        this.request.setMethod(method);
        return this;
    }

    @Override
    public HttpRequest setUri(String uri) {
        this.request.setUri(uri);
        return this;
    }

    @Override
    @Deprecated
    public HttpMethod getMethod() {
        return this.request.method();
    }

    @Override
    public HttpMethod method() {
        return this.request.method();
    }

    @Override
    @Deprecated
    public String getUri() {
        return this.request.uri();
    }

    @Override
    public String uri() {
        return this.request.uri();
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }
}

