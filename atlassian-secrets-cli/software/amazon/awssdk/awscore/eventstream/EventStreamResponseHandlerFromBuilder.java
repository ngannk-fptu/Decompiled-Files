/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.eventstream;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.eventstream.DefaultEventStreamResponseHandlerBuilder;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public abstract class EventStreamResponseHandlerFromBuilder<ResponseT, EventT>
implements EventStreamResponseHandler<ResponseT, EventT> {
    private final Consumer<ResponseT> responseConsumer;
    private final Consumer<Throwable> errorConsumer;
    private final Runnable onComplete;
    private final Consumer<SdkPublisher<EventT>> onEventStream;
    private final Function<SdkPublisher<EventT>, SdkPublisher<EventT>> publisherTransformer;

    protected EventStreamResponseHandlerFromBuilder(DefaultEventStreamResponseHandlerBuilder<ResponseT, EventT, ?> builder) {
        Validate.mutuallyExclusive("onEventStream and subscriber are mutually exclusive, set only one on the Builder", builder.onEventStream(), builder.subscriber());
        Supplier subscriber = builder.subscriber();
        Consumer<SdkPublisher> consumer = this.onEventStream = subscriber != null ? p -> p.subscribe((Subscriber)subscriber.get()) : builder.onEventStream();
        if (this.onEventStream == null) {
            throw new IllegalArgumentException("Must provide either a subscriber or set onEventStream and subscribe to the publisher in the callback method");
        }
        this.responseConsumer = Validate.getOrDefault(builder.onResponse(), FunctionalUtils::noOpConsumer);
        this.errorConsumer = Validate.getOrDefault(builder.onError(), FunctionalUtils::noOpConsumer);
        this.onComplete = Validate.getOrDefault(builder.onComplete(), FunctionalUtils::noOpRunnable);
        this.publisherTransformer = Validate.getOrDefault(builder.publisherTransformer(), Function::identity);
    }

    @Override
    public void responseReceived(ResponseT response) {
        this.responseConsumer.accept(response);
    }

    @Override
    public void onEventStream(SdkPublisher<EventT> p) {
        this.onEventStream.accept(this.publisherTransformer.apply(p));
    }

    @Override
    public void exceptionOccurred(Throwable throwable) {
        this.errorConsumer.accept(throwable);
    }

    @Override
    public void complete() {
        this.onComplete.run();
    }
}

