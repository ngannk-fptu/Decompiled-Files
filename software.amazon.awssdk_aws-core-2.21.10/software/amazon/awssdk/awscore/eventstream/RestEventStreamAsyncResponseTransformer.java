/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 *  software.amazon.awssdk.core.async.SdkPublisher
 */
package software.amazon.awssdk.awscore.eventstream;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.eventstream.EventStreamAsyncResponseTransformer;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;

@SdkProtectedApi
public class RestEventStreamAsyncResponseTransformer<ResponseT extends SdkResponse, EventT>
implements AsyncResponseTransformer<ResponseT, Void> {
    private final EventStreamAsyncResponseTransformer<ResponseT, EventT> delegate;
    private final EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler;

    private RestEventStreamAsyncResponseTransformer(EventStreamAsyncResponseTransformer<ResponseT, EventT> delegateAsyncResponseTransformer, EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler) {
        this.delegate = delegateAsyncResponseTransformer;
        this.eventStreamResponseHandler = eventStreamResponseHandler;
    }

    public CompletableFuture<Void> prepare() {
        return this.delegate.prepare();
    }

    public void onResponse(ResponseT response) {
        this.delegate.onResponse((SdkResponse)response);
        this.eventStreamResponseHandler.responseReceived(response);
    }

    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        this.delegate.onStream(publisher);
    }

    public void exceptionOccurred(Throwable throwable) {
        this.delegate.exceptionOccurred(throwable);
    }

    public static <ResponseT extends SdkResponse, EventT> Builder<ResponseT, EventT> builder() {
        return new Builder();
    }

    public static final class Builder<ResponseT extends SdkResponse, EventT> {
        private EventStreamAsyncResponseTransformer<ResponseT, EventT> delegateAsyncResponseTransformer;
        private EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler;

        private Builder() {
        }

        public Builder<ResponseT, EventT> eventStreamAsyncResponseTransformer(EventStreamAsyncResponseTransformer<ResponseT, EventT> delegateAsyncResponseTransformer) {
            this.delegateAsyncResponseTransformer = delegateAsyncResponseTransformer;
            return this;
        }

        public Builder<ResponseT, EventT> eventStreamResponseHandler(EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler) {
            this.eventStreamResponseHandler = eventStreamResponseHandler;
            return this;
        }

        public RestEventStreamAsyncResponseTransformer<ResponseT, EventT> build() {
            return new RestEventStreamAsyncResponseTransformer(this.delegateAsyncResponseTransformer, this.eventStreamResponseHandler);
        }
    }
}

