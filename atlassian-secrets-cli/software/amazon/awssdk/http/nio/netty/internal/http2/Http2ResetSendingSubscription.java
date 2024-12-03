/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2ResetFrame;
import io.netty.handler.codec.http2.Http2Error;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.async.DelegatingSubscription;

@SdkInternalApi
public class Http2ResetSendingSubscription
extends DelegatingSubscription {
    private final ChannelHandlerContext ctx;

    public Http2ResetSendingSubscription(ChannelHandlerContext ctx, Subscription delegate) {
        super(delegate);
        this.ctx = ctx;
    }

    @Override
    public void cancel() {
        this.ctx.write(new DefaultHttp2ResetFrame(Http2Error.CANCEL));
        super.cancel();
    }
}

