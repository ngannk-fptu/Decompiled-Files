/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class InMemoryPublisher
implements Publisher<ByteBuffer> {
    private final AtomicBoolean subscribed = new AtomicBoolean(false);
    private final List<ByteBuffer> data;

    public InMemoryPublisher(List<ByteBuffer> data) {
        this.data = new ArrayList<ByteBuffer>((Collection)Validate.noNullElements(data, "Data must not contain null elements.", new Object[0]));
    }

    @Override
    public void subscribe(final Subscriber<? super ByteBuffer> s) {
        if (!this.subscribed.compareAndSet(false, true)) {
            s.onSubscribe(new NoOpSubscription());
            s.onError(new IllegalStateException("InMemoryPublisher cannot be subscribed to twice."));
            return;
        }
        s.onSubscribe(new Subscription(){
            private final AtomicBoolean sending = new AtomicBoolean(false);
            private final Object doneLock = new Object();
            private final AtomicBoolean done = new AtomicBoolean(false);
            private final AtomicLong demand = new AtomicLong(0L);
            private int position = 0;

            @Override
            public void request(long n) {
                if (this.done.get()) {
                    return;
                }
                try {
                    this.demand.addAndGet(n);
                    this.fulfillDemand();
                }
                catch (Throwable t) {
                    this.finish(() -> s.onError(t));
                }
            }

            private void fulfillDemand() {
                do {
                    if (!this.sending.compareAndSet(false, true)) continue;
                    try {
                        this.send();
                    }
                    finally {
                        this.sending.set(false);
                    }
                } while (!this.done.get() && this.demand.get() > 0L);
            }

            private void send() {
                while (true) {
                    assert (this.position >= 0);
                    assert (this.position <= InMemoryPublisher.this.data.size());
                    if (this.done.get()) break;
                    if (this.position == InMemoryPublisher.this.data.size()) {
                        this.finish(s::onComplete);
                        break;
                    }
                    if (this.demand.get() == 0L) break;
                    this.demand.decrementAndGet();
                    int dataIndex = this.position++;
                    s.onNext(InMemoryPublisher.this.data.get(dataIndex));
                    InMemoryPublisher.this.data.set(dataIndex, null);
                }
            }

            @Override
            public void cancel() {
                this.finish(() -> {});
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            private void finish(Runnable thingToDo) {
                Object object = this.doneLock;
                synchronized (object) {
                    if (this.done.compareAndSet(false, true)) {
                        thingToDo.run();
                    }
                }
            }
        });
    }

    private static class NoOpSubscription
    implements Subscription {
        private NoOpSubscription() {
        }

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
        }
    }
}

