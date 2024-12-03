/*
 * Decompiled with CFR 0.152.
 */
package org.reactivestreams;

import java.util.Objects;
import java.util.concurrent.Flow;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class FlowAdapters {
    private FlowAdapters() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Publisher<T> toPublisher(Flow.Publisher<? extends T> flowPublisher) {
        Objects.requireNonNull(flowPublisher, "flowPublisher");
        ReactivePublisherFromFlow<? extends T> publisher = flowPublisher instanceof FlowPublisherFromReactive ? ((FlowPublisherFromReactive)flowPublisher).reactiveStreams : (flowPublisher instanceof Publisher ? (ReactivePublisherFromFlow<? extends T>)((Object)flowPublisher) : new ReactivePublisherFromFlow<T>(flowPublisher));
        return publisher;
    }

    public static <T> Flow.Publisher<T> toFlowPublisher(Publisher<? extends T> reactiveStreamsPublisher) {
        Objects.requireNonNull(reactiveStreamsPublisher, "reactiveStreamsPublisher");
        FlowPublisherFromReactive<? extends T> flowPublisher = reactiveStreamsPublisher instanceof ReactivePublisherFromFlow ? ((ReactivePublisherFromFlow)reactiveStreamsPublisher).flow : (reactiveStreamsPublisher instanceof Flow.Publisher ? (FlowPublisherFromReactive<? extends T>)((Object)reactiveStreamsPublisher) : new FlowPublisherFromReactive<T>(reactiveStreamsPublisher));
        return flowPublisher;
    }

    public static <T, U> Processor<T, U> toProcessor(Flow.Processor<? super T, ? extends U> flowProcessor) {
        Objects.requireNonNull(flowProcessor, "flowProcessor");
        ReactiveToFlowProcessor<? super T, ? extends U> processor = flowProcessor instanceof FlowToReactiveProcessor ? ((FlowToReactiveProcessor)flowProcessor).reactiveStreams : (flowProcessor instanceof Processor ? (ReactiveToFlowProcessor<? super T, ? extends U>)((Object)flowProcessor) : new ReactiveToFlowProcessor<T, U>(flowProcessor));
        return processor;
    }

    public static <T, U> Flow.Processor<T, U> toFlowProcessor(Processor<? super T, ? extends U> reactiveStreamsProcessor) {
        Objects.requireNonNull(reactiveStreamsProcessor, "reactiveStreamsProcessor");
        FlowToReactiveProcessor<? super T, ? extends U> flowProcessor = reactiveStreamsProcessor instanceof ReactiveToFlowProcessor ? ((ReactiveToFlowProcessor)reactiveStreamsProcessor).flow : (reactiveStreamsProcessor instanceof Flow.Processor ? (FlowToReactiveProcessor<? super T, ? extends U>)((Object)reactiveStreamsProcessor) : new FlowToReactiveProcessor<T, U>(reactiveStreamsProcessor));
        return flowProcessor;
    }

    public static <T> Flow.Subscriber<T> toFlowSubscriber(Subscriber<T> reactiveStreamsSubscriber) {
        Objects.requireNonNull(reactiveStreamsSubscriber, "reactiveStreamsSubscriber");
        FlowToReactiveSubscriber<T> flowSubscriber = reactiveStreamsSubscriber instanceof ReactiveToFlowSubscriber ? ((ReactiveToFlowSubscriber)reactiveStreamsSubscriber).flow : (reactiveStreamsSubscriber instanceof Flow.Subscriber ? (FlowToReactiveSubscriber<T>)((Object)reactiveStreamsSubscriber) : new FlowToReactiveSubscriber<T>(reactiveStreamsSubscriber));
        return flowSubscriber;
    }

    public static <T> Subscriber<T> toSubscriber(Flow.Subscriber<T> flowSubscriber) {
        Objects.requireNonNull(flowSubscriber, "flowSubscriber");
        ReactiveToFlowSubscriber<T> subscriber = flowSubscriber instanceof FlowToReactiveSubscriber ? ((FlowToReactiveSubscriber)flowSubscriber).reactiveStreams : (flowSubscriber instanceof Subscriber ? (ReactiveToFlowSubscriber<T>)((Object)flowSubscriber) : new ReactiveToFlowSubscriber<T>(flowSubscriber));
        return subscriber;
    }

    static final class FlowPublisherFromReactive<T>
    implements Flow.Publisher<T> {
        final Publisher<? extends T> reactiveStreams;

        public FlowPublisherFromReactive(Publisher<? extends T> reactivePublisher) {
            this.reactiveStreams = reactivePublisher;
        }

        @Override
        public void subscribe(Flow.Subscriber<? super T> flow) {
            this.reactiveStreams.subscribe(flow == null ? null : new ReactiveToFlowSubscriber<T>(flow));
        }
    }

    static final class ReactivePublisherFromFlow<T>
    implements Publisher<T> {
        final Flow.Publisher<? extends T> flow;

        public ReactivePublisherFromFlow(Flow.Publisher<? extends T> flowPublisher) {
            this.flow = flowPublisher;
        }

        @Override
        public void subscribe(Subscriber<? super T> reactive) {
            this.flow.subscribe(reactive == null ? null : new FlowToReactiveSubscriber<T>(reactive));
        }
    }

    static final class FlowToReactiveProcessor<T, U>
    implements Flow.Processor<T, U> {
        final Processor<? super T, ? extends U> reactiveStreams;

        public FlowToReactiveProcessor(Processor<? super T, ? extends U> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.reactiveStreams.onSubscribe(subscription == null ? null : new ReactiveToFlowSubscription(subscription));
        }

        @Override
        public void onNext(T t) {
            this.reactiveStreams.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            this.reactiveStreams.onError(t);
        }

        @Override
        public void onComplete() {
            this.reactiveStreams.onComplete();
        }

        @Override
        public void subscribe(Flow.Subscriber<? super U> s) {
            this.reactiveStreams.subscribe(s == null ? null : new ReactiveToFlowSubscriber<U>(s));
        }
    }

    static final class ReactiveToFlowProcessor<T, U>
    implements Processor<T, U> {
        final Flow.Processor<? super T, ? extends U> flow;

        public ReactiveToFlowProcessor(Flow.Processor<? super T, ? extends U> flow) {
            this.flow = flow;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.flow.onSubscribe(subscription == null ? null : new FlowToReactiveSubscription(subscription));
        }

        @Override
        public void onNext(T t) {
            this.flow.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            this.flow.onError(t);
        }

        @Override
        public void onComplete() {
            this.flow.onComplete();
        }

        @Override
        public void subscribe(Subscriber<? super U> s) {
            this.flow.subscribe(s == null ? null : new FlowToReactiveSubscriber<U>(s));
        }
    }

    static final class ReactiveToFlowSubscriber<T>
    implements Subscriber<T> {
        final Flow.Subscriber<? super T> flow;

        public ReactiveToFlowSubscriber(Flow.Subscriber<? super T> flow) {
            this.flow = flow;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.flow.onSubscribe(subscription == null ? null : new FlowToReactiveSubscription(subscription));
        }

        @Override
        public void onNext(T item) {
            this.flow.onNext(item);
        }

        @Override
        public void onError(Throwable throwable) {
            this.flow.onError(throwable);
        }

        @Override
        public void onComplete() {
            this.flow.onComplete();
        }
    }

    static final class FlowToReactiveSubscriber<T>
    implements Flow.Subscriber<T> {
        final Subscriber<? super T> reactiveStreams;

        public FlowToReactiveSubscriber(Subscriber<? super T> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.reactiveStreams.onSubscribe(subscription == null ? null : new ReactiveToFlowSubscription(subscription));
        }

        @Override
        public void onNext(T item) {
            this.reactiveStreams.onNext(item);
        }

        @Override
        public void onError(Throwable throwable) {
            this.reactiveStreams.onError(throwable);
        }

        @Override
        public void onComplete() {
            this.reactiveStreams.onComplete();
        }
    }

    static final class ReactiveToFlowSubscription
    implements Subscription {
        final Flow.Subscription flow;

        public ReactiveToFlowSubscription(Flow.Subscription flow) {
            this.flow = flow;
        }

        @Override
        public void request(long n) {
            this.flow.request(n);
        }

        @Override
        public void cancel() {
            this.flow.cancel();
        }
    }

    static final class FlowToReactiveSubscription
    implements Flow.Subscription {
        final Subscription reactiveStreams;

        public FlowToReactiveSubscription(Subscription reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void request(long n) {
            this.reactiveStreams.request(n);
        }

        @Override
        public void cancel() {
            this.reactiveStreams.cancel();
        }
    }
}

