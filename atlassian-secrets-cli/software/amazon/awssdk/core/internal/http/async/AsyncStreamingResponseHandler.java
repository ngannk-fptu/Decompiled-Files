/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.async;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkInternalApi
public final class AsyncStreamingResponseHandler<OutputT extends SdkResponse, ReturnT>
implements TransformingAsyncResponseHandler<ReturnT> {
    private final AsyncResponseTransformer<OutputT, ReturnT> asyncResponseTransformer;
    private volatile HttpResponseHandler<OutputT> responseHandler;

    public AsyncStreamingResponseHandler(AsyncResponseTransformer<OutputT, ReturnT> asyncResponseTransformer) {
        this.asyncResponseTransformer = asyncResponseTransformer;
    }

    public void responseHandler(HttpResponseHandler<OutputT> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void onHeaders(SdkHttpResponse response) {
        try {
            SdkResponse resp = (SdkResponse)this.responseHandler.handle((SdkHttpFullResponse)response, null);
            this.asyncResponseTransformer.onResponse(resp);
        }
        catch (Exception e) {
            this.asyncResponseTransformer.exceptionOccurred(e);
        }
    }

    @Override
    public void onStream(Publisher<ByteBuffer> publisher) {
        this.asyncResponseTransformer.onStream(SdkPublisher.adapt(publisher));
    }

    @Override
    public void onError(Throwable error) {
        this.asyncResponseTransformer.exceptionOccurred(error);
    }

    @Override
    public CompletableFuture<ReturnT> prepare() {
        return this.asyncResponseTransformer.prepare();
    }
}

