/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateHttpMessage;

@SdkInternalApi
class DelegateHttpResponse
extends DelegateHttpMessage
implements HttpResponse {
    protected final HttpResponse response;

    DelegateHttpResponse(HttpResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public HttpResponse setStatus(HttpResponseStatus status) {
        this.response.setStatus(status);
        return this;
    }

    @Override
    @Deprecated
    public HttpResponseStatus getStatus() {
        return this.response.status();
    }

    @Override
    public HttpResponseStatus status() {
        return this.response.status();
    }

    @Override
    public HttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }
}

