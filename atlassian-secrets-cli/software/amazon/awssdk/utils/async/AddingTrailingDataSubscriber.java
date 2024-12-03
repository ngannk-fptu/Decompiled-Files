/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;

@SdkProtectedApi
public class AddingTrailingDataSubscriber<T>
extends DelegatingSubscriber<T, T> {
    private static final Logger log = Logger.loggerFor(AddingTrailingDataSubscriber.class);
    private Subscription upstreamSubscription;
    private final AtomicLong downstreamDemand = new AtomicLong(0L);
    private volatile boolean onCompleteCalledByUpstream = false;
    private volatile boolean onErrorCalledByUpstream = false;
    private volatile boolean onCompleteCalledOnDownstream = false;
    private final Supplier<Iterable<T>> trailingDataIterableSupplier;
    private Iterator<T> trailingDataIterator;

    public AddingTrailingDataSubscriber(Subscriber<? super T> subscriber, Supplier<Iterable<T>> trailingDataIterableSupplier) {
        super(Validate.paramNotNull(subscriber, "subscriber"));
        this.trailingDataIterableSupplier = Validate.paramNotNull(trailingDataIterableSupplier, "trailingDataIterableSupplier");
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        if (this.upstreamSubscription != null) {
            log.warn(() -> "Received duplicate subscription, cancelling the duplicate.", new IllegalStateException());
            subscription.cancel();
            return;
        }
        this.upstreamSubscription = subscription;
        this.subscriber.onSubscribe(new Subscription(){

            @Override
            public void request(long l) {
                if (AddingTrailingDataSubscriber.this.onErrorCalledByUpstream || AddingTrailingDataSubscriber.this.onCompleteCalledOnDownstream) {
                    return;
                }
                AddingTrailingDataSubscriber.this.addDownstreamDemand(l);
                if (AddingTrailingDataSubscriber.this.onCompleteCalledByUpstream) {
                    AddingTrailingDataSubscriber.this.sendTrailingDataAndCompleteIfNeeded();
                    return;
                }
                AddingTrailingDataSubscriber.this.upstreamSubscription.request(l);
            }

            @Override
            public void cancel() {
                AddingTrailingDataSubscriber.this.upstreamSubscription.cancel();
            }
        });
    }

    @Override
    public void onError(Throwable throwable) {
        this.onErrorCalledByUpstream = true;
        this.subscriber.onError(throwable);
    }

    @Override
    public void onNext(T t) {
        Validate.paramNotNull(t, "item");
        this.downstreamDemand.decrementAndGet();
        this.subscriber.onNext(t);
    }

    @Override
    public void onComplete() {
        this.onCompleteCalledByUpstream = true;
        this.sendTrailingDataAndCompleteIfNeeded();
    }

    private void addDownstreamDemand(long l) {
        if (l > 0L) {
            this.downstreamDemand.getAndUpdate(current -> {
                long newValue = current + l;
                return newValue >= 0L ? newValue : Long.MAX_VALUE;
            });
        } else {
            this.upstreamSubscription.cancel();
            this.onError(new IllegalArgumentException("Demand must not be negative"));
        }
    }

    private synchronized void sendTrailingDataAndCompleteIfNeeded() {
        if (this.onCompleteCalledOnDownstream) {
            return;
        }
        if (this.trailingDataIterator == null) {
            Iterable<T> supplier = this.trailingDataIterableSupplier.get();
            if (supplier == null) {
                this.completeDownstreamSubscriber();
                return;
            }
            this.trailingDataIterator = supplier.iterator();
        }
        this.sendTrailingDataIfNeeded();
        if (!this.trailingDataIterator.hasNext()) {
            this.completeDownstreamSubscriber();
        }
    }

    private void sendTrailingDataIfNeeded() {
        long demand = this.downstreamDemand.get();
        while (this.trailingDataIterator.hasNext() && demand > 0L) {
            this.subscriber.onNext(this.trailingDataIterator.next());
            demand = this.downstreamDemand.decrementAndGet();
        }
    }

    private void completeDownstreamSubscriber() {
        this.subscriber.onComplete();
        this.onCompleteCalledOnDownstream = true;
    }
}

