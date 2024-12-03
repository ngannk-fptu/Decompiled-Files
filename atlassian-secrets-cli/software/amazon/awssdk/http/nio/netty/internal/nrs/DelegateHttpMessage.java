/*
 * Decompiled with CFR 0.152.
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

    @Override
    @Deprecated
    public HttpVersion getProtocolVersion() {
        return this.message.protocolVersion();
    }

    @Override
    public HttpVersion protocolVersion() {
        return this.message.protocolVersion();
    }

    @Override
    public HttpMessage setProtocolVersion(HttpVersion version) {
        this.message.setProtocolVersion(version);
        return this;
    }

    @Override
    public HttpHeaders headers() {
        return this.message.headers();
    }

    @Override
    @Deprecated
    public DecoderResult getDecoderResult() {
        return this.message.decoderResult();
    }

    @Override
    public DecoderResult decoderResult() {
        return this.message.decoderResult();
    }

    @Override
    public void setDecoderResult(DecoderResult result) {
        this.message.setDecoderResult(result);
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.message.toString() + ")";
    }
}

