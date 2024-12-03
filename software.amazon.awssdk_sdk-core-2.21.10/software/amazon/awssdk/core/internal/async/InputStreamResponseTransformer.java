/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.async.InputStreamSubscriber
 */
package software.amazon.awssdk.core.internal.async;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.utils.async.InputStreamSubscriber;

@SdkInternalApi
public class InputStreamResponseTransformer<ResponseT extends SdkResponse>
implements AsyncResponseTransformer<ResponseT, ResponseInputStream<ResponseT>> {
    private volatile CompletableFuture<ResponseInputStream<ResponseT>> future;
    private volatile ResponseT response;

    @Override
    public CompletableFuture<ResponseInputStream<ResponseT>> prepare() {
        CompletableFuture<ResponseInputStream<ResponseT>> result = new CompletableFuture<ResponseInputStream<ResponseT>>();
        this.future = result;
        return result;
    }

    @Override
    public void onResponse(ResponseT response) {
        this.response = response;
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        InputStreamSubscriber inputStreamSubscriber = new InputStreamSubscriber();
        publisher.subscribe((Subscriber)inputStreamSubscriber);
        this.future.complete(new ResponseInputStream<ResponseT>(this.response, (InputStream)inputStreamSubscriber));
    }

    @Override
    public void exceptionOccurred(Throwable error) {
        this.future.completeExceptionally(error);
    }
}

