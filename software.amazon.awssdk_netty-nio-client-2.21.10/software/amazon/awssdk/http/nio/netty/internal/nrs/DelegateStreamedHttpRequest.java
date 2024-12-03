/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpRequest
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateHttpRequest;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpRequest;

@SdkInternalApi
final class DelegateStreamedHttpRequest
extends DelegateHttpRequest
implements StreamedHttpRequest {
    private final Publisher<HttpContent> stream;

    DelegateStreamedHttpRequest(HttpRequest request, Publisher<HttpContent> stream) {
        super(request);
        this.stream = stream;
    }

    public void subscribe(Subscriber<? super HttpContent> subscriber) {
        this.stream.subscribe(subscriber);
    }
}

