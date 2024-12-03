/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void subscribe(Subscriber<? super HttpContent> subscriber) {
        this.stream.subscribe(subscriber);
    }
}

