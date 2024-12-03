/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.eventstream;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.utils.async.SequentialSubscriber;

@SdkProtectedApi
public abstract class DefaultEventStreamResponseHandlerBuilder<ResponseT, EventT, SubBuilderT>
implements EventStreamResponseHandler.Builder<ResponseT, EventT, SubBuilderT> {
    private Consumer<ResponseT> onResponse;
    private Consumer<Throwable> onError;
    private Runnable onComplete;
    private Supplier<Subscriber<EventT>> subscriber;
    private Consumer<SdkPublisher<EventT>> onSubscribe;
    private Function<SdkPublisher<EventT>, SdkPublisher<EventT>> publisherTransformer;

    protected DefaultEventStreamResponseHandlerBuilder() {
    }

    @Override
    public SubBuilderT onResponse(Consumer<ResponseT> responseConsumer) {
        this.onResponse = responseConsumer;
        return this.subclass();
    }

    Consumer<ResponseT> onResponse() {
        return this.onResponse;
    }

    @Override
    public SubBuilderT onError(Consumer<Throwable> consumer) {
        this.onError = consumer;
        return this.subclass();
    }

    Consumer<Throwable> onError() {
        return this.onError;
    }

    @Override
    public SubBuilderT onComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this.subclass();
    }

    Runnable onComplete() {
        return this.onComplete;
    }

    @Override
    public SubBuilderT subscriber(Supplier<Subscriber<EventT>> eventSubscriber) {
        this.subscriber = eventSubscriber;
        return this.subclass();
    }

    @Override
    public SubBuilderT subscriber(Consumer<EventT> eventConsumer) {
        this.subscriber = () -> new SequentialSubscriber(eventConsumer, new CompletableFuture<Void>());
        return this.subclass();
    }

    Supplier<Subscriber<EventT>> subscriber() {
        return this.subscriber;
    }

    @Override
    public SubBuilderT onEventStream(Consumer<SdkPublisher<EventT>> onSubscribe) {
        this.onSubscribe = onSubscribe;
        return this.subclass();
    }

    Consumer<SdkPublisher<EventT>> onEventStream() {
        return this.onSubscribe;
    }

    @Override
    public SubBuilderT publisherTransformer(Function<SdkPublisher<EventT>, SdkPublisher<EventT>> publisherTransformer) {
        this.publisherTransformer = publisherTransformer;
        return this.subclass();
    }

    Function<SdkPublisher<EventT>, SdkPublisher<EventT>> publisherTransformer() {
        return this.publisherTransformer;
    }

    private SubBuilderT subclass() {
        return (SubBuilderT)this;
    }
}

