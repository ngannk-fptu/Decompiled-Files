/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.HttpResponse
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  io.netty.handler.codec.http.HttpVersion
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpMessage;
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
        super((HttpMessage)response);
        this.response = response;
    }

    public HttpResponse setStatus(HttpResponseStatus status) {
        this.response.setStatus(status);
        return this;
    }

    @Deprecated
    public HttpResponseStatus getStatus() {
        return this.response.status();
    }

    public HttpResponseStatus status() {
        return this.response.status();
    }

    public HttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }
}

