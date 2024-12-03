/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class SimplePublisher<T>
implements Publisher<T> {
    private static final Logger log = Logger.loggerFor(SimplePublisher.class);
    private final AtomicLong outstandingDemand = new AtomicLong();
    private final Queue<QueueEntry<T>> standardPriorityQueue = new ConcurrentLinkedQueue<QueueEntry<T>>();
    private final Queue<QueueEntry<T>> highPriorityQueue = new ConcurrentLinkedQueue<QueueEntry<T>>();
    private final AtomicBoolean processingQueue = new AtomicBoolean(false);
    private final FailureMessage failureMessage = new FailureMessage();
    private Subscriber<? super T> subscriber;

    public CompletableFuture<Void> send(T value) {
        log.trace(() -> "Received send() with " + value);
        OnNextQueueEntry entry = new OnNextQueueEntry(value);
        try {
            Validate.notNull(value, "Null cannot be written.", new Object[0]);
            this.standardPriorityQueue.add(entry);
            this.processEventQueue();
        }
        catch (RuntimeException t) {
            entry.resultFuture.completeExceptionally(t);
        }
        return entry.resultFuture;
    }

    public CompletableFuture<Void> complete() {
        log.trace(() -> "Received complete()");
        OnCompleteQueueEntry entry = new OnCompleteQueueEntry();
        try {
            this.standardPriorityQueue.add(entry);
            this.processEventQueue();
        }
        catch (RuntimeException t) {
            entry.resultFuture.completeExceptionally(t);
        }
        return entry.resultFuture;
    }

    public CompletableFuture<Void> error(Throwable error) {
        log.trace(() -> "Received error() with " + error, error);
        OnErrorQueueEntry entry = new OnErrorQueueEntry(error);
        try {
            this.standardPriorityQueue.add(entry);
            this.processEventQueue();
        }
        catch (RuntimeException t) {
            entry.resultFuture.completeExceptionally(t);
        }
        return entry.resultFuture;
    }

    public void subscribe(Subscriber<? super T> s) {
        if (this.subscriber != null) {
            s.onSubscribe((Subscription)new NoOpSubscription());
            s.onError((Throwable)new IllegalStateException("Only one subscription may be active at a time."));
        }
        this.subscriber = s;
        s.onSubscribe((Subscription)new SubscriptionImpl());
        this.processEventQueue();
    }

    private void processEventQueue() {
        do {
            if (!this.processingQueue.compareAndSet(false, true)) {
                return;
            }
            try {
                this.doProcessQueue();
            }
            catch (Throwable e) {
                this.panicAndDie(e);
                break;
            }
            finally {
                this.processingQueue.set(false);
            }
        } while (this.shouldProcessQueueEntry(this.standardPriorityQueue.peek()) || this.shouldProcessQueueEntry(this.highPriorityQueue.peek()));
    }

    private void doProcessQueue() {
        while (true) {
            QueueEntry<T> entry = this.highPriorityQueue.peek();
            Queue<QueueEntry<T>> sourceQueue = this.highPriorityQueue;
            if (entry == null) {
                entry = this.standardPriorityQueue.peek();
                sourceQueue = this.standardPriorityQueue;
            }
            if (!this.shouldProcessQueueEntry(entry)) {
                return;
            }
            if (this.failureMessage.isSet()) {
                entry.resultFuture.completeExceptionally(this.failureMessage.get());
            } else {
                switch (entry.type()) {
                    case ON_NEXT: {
                        OnNextQueueEntry onNextEntry = (OnNextQueueEntry)entry;
                        log.trace(() -> "Calling onNext() with " + onNextEntry.value);
                        this.subscriber.onNext(onNextEntry.value);
                        long newDemand = this.outstandingDemand.decrementAndGet();
                        log.trace(() -> "Decreased demand to " + newDemand);
                        break;
                    }
                    case ON_COMPLETE: {
                        this.failureMessage.trySet(() -> new IllegalStateException("onComplete() was already invoked."));
                        log.trace(() -> "Calling onComplete()");
                        this.subscriber.onComplete();
                        break;
                    }
                    case ON_ERROR: {
                        OnErrorQueueEntry onErrorEntry = (OnErrorQueueEntry)entry;
                        this.failureMessage.trySet(() -> new IllegalStateException("onError() was already invoked.", onErrorEntry.failure));
                        log.trace(() -> "Calling onError() with " + onErrorEntry.failure, onErrorEntry.failure);
                        this.subscriber.onError(onErrorEntry.failure);
                        break;
                    }
                    case CANCEL: {
                        this.failureMessage.trySet(() -> new CancellationException("subscription has been cancelled."));
                        this.subscriber = null;
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown entry type: " + (Object)((Object)entry.type()));
                    }
                }
                entry.resultFuture.complete(null);
            }
            sourceQueue.remove();
        }
    }

    private boolean shouldProcessQueueEntry(QueueEntry<T> entry) {
        if (entry == null) {
            return false;
        }
        if (this.failureMessage.isSet()) {
            return true;
        }
        if (this.subscriber == null) {
            return false;
        }
        if (entry.type() != QueueEntry.Type.ON_NEXT) {
            return true;
        }
        return this.outstandingDemand.get() > 0L;
    }

    private void panicAndDie(Throwable cause) {
        try {
            QueueEntry<T> entry;
            IllegalStateException failure = new IllegalStateException("Encountered fatal error in publisher", cause);
            this.failureMessage.trySet(() -> failure);
            this.subscriber.onError(cause instanceof Error ? cause : failure);
            while ((entry = this.standardPriorityQueue.poll()) != null) {
                entry.resultFuture.completeExceptionally(failure);
            }
        }
        catch (Throwable t) {
            t.addSuppressed(cause);
            log.error(() -> "Failed while processing a failure. This could result in stuck futures.", t);
        }
    }

    private static final class NoOpSubscription
    implements Subscription {
        private NoOpSubscription() {
        }

        public void request(long n) {
        }

        public void cancel() {
        }
    }

    private static final class CancelQueueEntry<T>
    extends QueueEntry<T> {
        private CancelQueueEntry() {
        }

        @Override
        protected QueueEntry.Type type() {
            return QueueEntry.Type.CANCEL;
        }
    }

    private static final class OnErrorQueueEntry<T>
    extends QueueEntry<T> {
        private final Throwable failure;

        private OnErrorQueueEntry(Throwable failure) {
            this.failure = failure;
        }

        @Override
        protected QueueEntry.Type type() {
            return QueueEntry.Type.ON_ERROR;
        }
    }

    private static final class OnCompleteQueueEntry<T>
    extends QueueEntry<T> {
        private OnCompleteQueueEntry() {
        }

        @Override
        protected QueueEntry.Type type() {
            return QueueEntry.Type.ON_COMPLETE;
        }
    }

    private static final class OnNextQueueEntry<T>
    extends QueueEntry<T> {
        private final T value;

        private OnNextQueueEntry(T value) {
            this.value = value;
        }

        @Override
        protected QueueEntry.Type type() {
            return QueueEntry.Type.ON_NEXT;
        }
    }

    static abstract class QueueEntry<T> {
        protected final CompletableFuture<Void> resultFuture = new CompletableFuture();

        QueueEntry() {
        }

        protected abstract Type type();

        protected static enum Type {
            ON_NEXT,
            ON_COMPLETE,
            ON_ERROR,
            CANCEL;

        }
    }

    private static final class FailureMessage {
        private Supplier<Throwable> failureMessageSupplier;
        private Throwable failureMessage;

        private FailureMessage() {
        }

        private void trySet(Supplier<Throwable> supplier) {
            if (this.failureMessageSupplier == null) {
                this.failureMessageSupplier = supplier;
            }
        }

        private boolean isSet() {
            return this.failureMessageSupplier != null;
        }

        private Throwable get() {
            if (this.failureMessage == null) {
                this.failureMessage = this.failureMessageSupplier.get();
            }
            return this.failureMessage;
        }
    }

    private class SubscriptionImpl
    implements Subscription {
        private SubscriptionImpl() {
        }

        public void request(long n) {
            log.trace(() -> "Received request() with " + n);
            if (n <= 0L) {
                IllegalArgumentException failure = new IllegalArgumentException("A downstream publisher requested an invalid amount of data: " + n);
                SimplePublisher.this.highPriorityQueue.add(new OnErrorQueueEntry(failure));
                SimplePublisher.this.processEventQueue();
            } else {
                long newDemand = SimplePublisher.this.outstandingDemand.updateAndGet(current -> {
                    if (Long.MAX_VALUE - current < n) {
                        return Long.MAX_VALUE;
                    }
                    return current + n;
                });
                log.trace(() -> "Increased demand to " + newDemand);
                SimplePublisher.this.processEventQueue();
            }
        }

        public void cancel() {
            log.trace(() -> "Received cancel() from " + SimplePublisher.this.subscriber);
            SimplePublisher.this.highPriorityQueue.add(new CancelQueueEntry());
            SimplePublisher.this.processEventQueue();
        }
    }
}

