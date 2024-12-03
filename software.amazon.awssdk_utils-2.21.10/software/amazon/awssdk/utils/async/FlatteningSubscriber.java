/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;

@SdkProtectedApi
public class FlatteningSubscriber<U>
extends DelegatingSubscriber<Iterable<U>, U> {
    private static final Logger log = Logger.loggerFor(FlatteningSubscriber.class);
    private final AtomicLong upstreamDemand = new AtomicLong(0L);
    private final AtomicLong downstreamDemand = new AtomicLong(0L);
    private final AtomicBoolean handlingStateUpdate = new AtomicBoolean(false);
    private final LinkedBlockingQueue<U> allItems = new LinkedBlockingQueue();
    private final AtomicReference<Throwable> onErrorFromUpstream = new AtomicReference<Object>(null);
    private volatile boolean terminalCallMadeDownstream = false;
    private volatile boolean onCompleteCalledByUpstream = false;
    private Subscription upstreamSubscription;

    public FlatteningSubscriber(Subscriber<? super U> subscriber) {
        super(subscriber);
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        if (this.upstreamSubscription != null) {
            log.warn(() -> "Received duplicate subscription, cancelling the duplicate.", new IllegalStateException());
            subscription.cancel();
            return;
        }
        this.upstreamSubscription = subscription;
        this.subscriber.onSubscribe(new Subscription(){

            public void request(long l) {
                FlatteningSubscriber.this.addDownstreamDemand(l);
                FlatteningSubscriber.this.handleStateUpdate();
            }

            public void cancel() {
                subscription.cancel();
            }
        });
    }

    public void onNext(Iterable<U> nextItems) {
        try {
            nextItems.forEach(item -> {
                Validate.notNull(nextItems, "Collections flattened by the flattening subscriber must not contain null.", new Object[0]);
                this.allItems.add(item);
            });
        }
        catch (RuntimeException e) {
            this.upstreamSubscription.cancel();
            this.onError(e);
            throw e;
        }
        this.upstreamDemand.decrementAndGet();
        this.handleStateUpdate();
    }

    @Override
    public void onError(Throwable throwable) {
        this.onErrorFromUpstream.compareAndSet(null, throwable);
        this.handleStateUpdate();
    }

    @Override
    public void onComplete() {
        this.onCompleteCalledByUpstream = true;
        this.handleStateUpdate();
    }

    private void addDownstreamDemand(long l) {
        if (l > 0L) {
            this.downstreamDemand.getAndUpdate(current -> {
                long newValue = current + l;
                return newValue >= 0L ? newValue : Long.MAX_VALUE;
            });
        } else {
            log.error(() -> "Demand " + l + " must not be negative.");
            this.upstreamSubscription.cancel();
            this.onError(new IllegalArgumentException("Demand must not be negative"));
        }
    }

    private void handleStateUpdate() {
        do {
            if (!this.handlingStateUpdate.compareAndSet(false, true)) {
                return;
            }
            try {
                if (this.terminalCallMadeDownstream) {
                    return;
                }
                this.handleOnNextState();
                this.handleUpstreamDemandState();
                this.handleOnCompleteState();
                this.handleOnErrorState();
            }
            catch (Error e) {
                throw e;
            }
            catch (Throwable e) {
                log.error(() -> "Unexpected exception encountered that violates the reactive streams specification. Attempting to terminate gracefully.", e);
                this.upstreamSubscription.cancel();
                this.onError(e);
            }
            finally {
                this.handlingStateUpdate.set(false);
            }
        } while (this.onNextNeeded() || this.upstreamDemandNeeded() || this.onCompleteNeeded() || this.onErrorNeeded());
    }

    private void handleOnNextState() {
        while (this.onNextNeeded() && !this.onErrorNeeded()) {
            this.downstreamDemand.decrementAndGet();
            this.subscriber.onNext(this.allItems.poll());
        }
    }

    private boolean onNextNeeded() {
        return !this.allItems.isEmpty() && this.downstreamDemand.get() > 0L;
    }

    private void handleUpstreamDemandState() {
        if (this.upstreamDemandNeeded()) {
            this.ensureUpstreamDemandExists();
        }
    }

    private boolean upstreamDemandNeeded() {
        return this.upstreamDemand.get() <= 0L && this.downstreamDemand.get() > 0L && this.allItems.isEmpty();
    }

    private void handleOnCompleteState() {
        if (this.onCompleteNeeded()) {
            this.terminalCallMadeDownstream = true;
            this.subscriber.onComplete();
        }
    }

    private boolean onCompleteNeeded() {
        return this.onCompleteCalledByUpstream && this.allItems.isEmpty() && !this.terminalCallMadeDownstream;
    }

    private void handleOnErrorState() {
        if (this.onErrorNeeded()) {
            this.terminalCallMadeDownstream = true;
            this.subscriber.onError(this.onErrorFromUpstream.get());
        }
    }

    private boolean onErrorNeeded() {
        return this.onErrorFromUpstream.get() != null && !this.terminalCallMadeDownstream;
    }

    private void ensureUpstreamDemandExists() {
        if (this.upstreamDemand.get() < 0L) {
            log.error(() -> "Upstream delivered more data than requested. Resetting state to prevent a frozen stream.", new IllegalStateException());
            this.upstreamDemand.set(1L);
            this.upstreamSubscription.request(1L);
        } else if (this.upstreamDemand.compareAndSet(0L, 1L)) {
            this.upstreamSubscription.request(1L);
        }
    }
}

