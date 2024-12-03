/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.async.AddingTrailingDataSubscriber
 *  software.amazon.awssdk.utils.async.BufferingSubscriber
 *  software.amazon.awssdk.utils.async.EventListeningSubscriber
 *  software.amazon.awssdk.utils.async.FilteringSubscriber
 *  software.amazon.awssdk.utils.async.FlatteningSubscriber
 *  software.amazon.awssdk.utils.async.LimitingSubscriber
 *  software.amazon.awssdk.utils.async.SequentialSubscriber
 *  software.amazon.awssdk.utils.internal.MappingSubscriber
 */
package software.amazon.awssdk.core.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.async.AddingTrailingDataSubscriber;
import software.amazon.awssdk.utils.async.BufferingSubscriber;
import software.amazon.awssdk.utils.async.EventListeningSubscriber;
import software.amazon.awssdk.utils.async.FilteringSubscriber;
import software.amazon.awssdk.utils.async.FlatteningSubscriber;
import software.amazon.awssdk.utils.async.LimitingSubscriber;
import software.amazon.awssdk.utils.async.SequentialSubscriber;
import software.amazon.awssdk.utils.internal.MappingSubscriber;

@SdkPublicApi
public interface SdkPublisher<T>
extends Publisher<T> {
    public static <T> SdkPublisher<T> adapt(Publisher<T> toAdapt) {
        return arg_0 -> toAdapt.subscribe(arg_0);
    }

    default public <U extends T> SdkPublisher<U> filter(Class<U> clzz) {
        return this.filter(clzz::isInstance).map(clzz::cast);
    }

    default public SdkPublisher<T> filter(Predicate<T> predicate) {
        return subscriber -> this.subscribe((Subscriber)new FilteringSubscriber(subscriber, predicate));
    }

    default public <U> SdkPublisher<U> map(Function<T, U> mapper) {
        return subscriber -> this.subscribe((Subscriber)MappingSubscriber.create((Subscriber)subscriber, (Function)mapper));
    }

    default public <U> SdkPublisher<U> flatMapIterable(Function<T, Iterable<U>> mapper) {
        return subscriber -> this.map(mapper).subscribe((Subscriber)new FlatteningSubscriber(subscriber));
    }

    default public SdkPublisher<List<T>> buffer(int bufferSize) {
        return subscriber -> this.subscribe((Subscriber)new BufferingSubscriber(subscriber, bufferSize));
    }

    default public SdkPublisher<T> limit(int limit) {
        return subscriber -> this.subscribe((Subscriber)new LimitingSubscriber(subscriber, limit));
    }

    default public SdkPublisher<T> addTrailingData(Supplier<Iterable<T>> trailingDataSupplier) {
        return subscriber -> this.subscribe((Subscriber)new AddingTrailingDataSubscriber(subscriber, trailingDataSupplier));
    }

    default public SdkPublisher<T> doAfterOnComplete(Runnable afterOnComplete) {
        return subscriber -> this.subscribe((Subscriber)new EventListeningSubscriber(subscriber, afterOnComplete, null, null));
    }

    default public SdkPublisher<T> doAfterOnError(Consumer<Throwable> afterOnError) {
        return subscriber -> this.subscribe((Subscriber)new EventListeningSubscriber(subscriber, null, afterOnError, null));
    }

    default public SdkPublisher<T> doAfterOnCancel(Runnable afterOnCancel) {
        return subscriber -> this.subscribe((Subscriber)new EventListeningSubscriber(subscriber, null, null, afterOnCancel));
    }

    default public CompletableFuture<Void> subscribe(Consumer<T> consumer) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        this.subscribe((Subscriber)new SequentialSubscriber(consumer, future));
        return future;
    }
}

