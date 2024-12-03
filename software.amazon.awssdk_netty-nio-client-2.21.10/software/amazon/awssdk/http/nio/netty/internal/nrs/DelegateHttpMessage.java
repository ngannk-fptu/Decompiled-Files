/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DecoderResult
 *  io.netty.handler.codec.http.HttpHeaders
 *  io.netty.handler.codec.http.HttpMessage
 *  io.netty.handler.codec.http.HttpVersion
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpVersion;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
class DelegateHttpMessage
implements HttpMessage {
    protected final HttpMessage message;

    DelegateHttpMessage(HttpMessage message) {
        this.message = message;
    }

    @Deprecated
    public HttpVersion getProtocolVersion() {
        return this.message.protocolVersion();
    }

    public HttpVersion protocolVersion() {
        return this.message.protocolVersion();
    }

    public HttpMessage setProtocolVersion(HttpVersion version) {
        this.message.setProtocolVersion(version);
        return this;
    }

    public HttpHeaders headers() {
        return this.message.headers();
    }

    @Deprecated
    public DecoderResult getDecoderResult() {
        return this.message.decoderResult();
    }

    public DecoderResult decoderResult() {
        return this.message.decoderResult();
    }

    public void setDecoderResult(DecoderResult result) {
        this.message.setDecoderResult(result);
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.message.toString() + ")";
    }
}

