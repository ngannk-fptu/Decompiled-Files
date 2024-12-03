/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpResponse
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.DelegateHttpResponse;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpResponse;

@SdkInternalApi
final class DelegateStreamedHttpResponse
extends DelegateHttpResponse
implements StreamedHttpResponse {
    private final Publisher<HttpContent> stream;

    DelegateStreamedHttpResponse(HttpResponse response, Publisher<HttpContent> stream) {
        super(response);
        this.stream = stream;
    }

    public void subscribe(Subscriber<? super HttpContent> subscriber) {
        this.stream.subscribe(subscriber);
    }
}

