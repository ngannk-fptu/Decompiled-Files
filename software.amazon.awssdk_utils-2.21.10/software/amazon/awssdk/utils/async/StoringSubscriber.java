/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class StoringSubscriber<T>
implements Subscriber<T> {
    private final int maxEvents;
    private final Queue<Event<T>> events;
    private Subscription subscription;

    public StoringSubscriber(int maxEvents) {
        Validate.isPositive(maxEvents, "Max elements must be positive.");
        this.maxEvents = maxEvents;
        this.events = new ConcurrentLinkedQueue<Event<T>>();
    }

    public Optional<Event<T>> peek() {
        return Optional.ofNullable(this.events.peek());
    }

    public Optional<Event<T>> poll() {
        Event<T> result = this.events.poll();
        if (result != null) {
            this.subscription.request(1L);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public void onSubscribe(Subscription subscription) {
        if (this.subscription != null) {
            subscription.cancel();
        }
        this.subscription = subscription;
        subscription.request((long)this.maxEvents);
    }

    public void onNext(T t) {
        Validate.notNull(t, "onNext(null) is not allowed.", new Object[0]);
        try {
            this.events.add(Event.value(t));
        }
        catch (RuntimeException e) {
            this.subscription.cancel();
            this.onError(new IllegalStateException("Failed to store element.", e));
        }
    }

    public void onComplete() {
        this.events.add(Event.complete());
    }

    public void onError(Throwable throwable) {
        this.events.add(Event.error(throwable));
    }

    public static enum EventType {
        ON_NEXT,
        ON_COMPLETE,
        ON_ERROR;

    }

    public static final class Event<T> {
        private final EventType type;
        private final T value;
        private final Throwable error;

        private Event(EventType type, T value, Throwable error) {
            this.type = type;
            this.value = value;
            this.error = error;
        }

        private static <T> Event<T> complete() {
            return new Event<Object>(EventType.ON_COMPLETE, null, null);
        }

        private static <T> Event<T> error(Throwable error) {
            return new Event<Object>(EventType.ON_ERROR, null, error);
        }

        private static <T> Event<T> value(T value) {
            return new Event<T>(EventType.ON_NEXT, value, null);
        }

        public EventType type() {
            return this.type;
        }

        public T value() {
            return this.value;
        }

        public RuntimeException runtimeError() {
            if (this.type != EventType.ON_ERROR) {
                return null;
            }
            if (this.error instanceof RuntimeException) {
                return (RuntimeException)this.error;
            }
            if (this.error instanceof IOException) {
                return new UncheckedIOException((IOException)this.error);
            }
            return new RuntimeException(this.error);
        }
    }
}

