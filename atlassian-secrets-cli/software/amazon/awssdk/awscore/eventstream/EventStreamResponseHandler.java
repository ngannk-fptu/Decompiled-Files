/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.eventstream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.SdkPublisher;

@SdkProtectedApi
public interface EventStreamResponseHandler<ResponseT, EventT> {
    public void responseReceived(ResponseT var1);

    public void onEventStream(SdkPublisher<EventT> var1);

    public void exceptionOccurred(Throwable var1);

    public void complete();

    public static interface Builder<ResponseT, EventT, SubBuilderT> {
        public SubBuilderT onResponse(Consumer<ResponseT> var1);

        public SubBuilderT onError(Consumer<Throwable> var1);

        public SubBuilderT onComplete(Runnable var1);

        public SubBuilderT subscriber(Supplier<Subscriber<EventT>> var1);

        public SubBuilderT subscriber(Consumer<EventT> var1);

        public SubBuilderT onEventStream(Consumer<SdkPublisher<EventT>> var1);

        public SubBuilderT publisherTransformer(Function<SdkPublisher<EventT>, SdkPublisher<EventT>> var1);
    }
}

