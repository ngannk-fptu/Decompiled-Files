/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.async.ByteBufferStoringSubscriber;

@SdkProtectedApi
public final class InputStreamSubscriber
extends InputStream
implements Subscriber<ByteBuffer>,
SdkAutoCloseable {
    private static final int BUFFER_SIZE = 0x400000;
    private final ByteBufferStoringSubscriber delegate;
    private final ByteBuffer singleByte = ByteBuffer.allocate(1);
    private final AtomicReference<State> inputStreamState = new AtomicReference<State>(State.UNINITIALIZED);
    private final AtomicBoolean drainingCallQueue = new AtomicBoolean(false);
    private final Queue<QueueEntry> callQueue = new ConcurrentLinkedQueue<QueueEntry>();
    private final Object subscribeLock = new Object();
    private Subscription subscription;
    private boolean done = false;

    public InputStreamSubscriber() {
        this.delegate = new ByteBufferStoringSubscriber(0x400000L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onSubscribe(Subscription s) {
        Object object = this.subscribeLock;
        synchronized (object) {
            if (!this.inputStreamState.compareAndSet(State.UNINITIALIZED, State.READABLE)) {
                this.close();
                return;
            }
            this.subscription = new CancelWatcher(s);
            this.delegate.onSubscribe(this.subscription);
        }
    }

    public void onNext(ByteBuffer byteBuffer) {
        this.callQueue.add(new QueueEntry(false, () -> this.delegate.onNext(byteBuffer)));
        this.drainQueue();
    }

    public void onError(Throwable t) {
        this.callQueue.add(new QueueEntry(true, () -> this.delegate.onError(t)));
        this.drainQueue();
    }

    public void onComplete() {
        this.callQueue.add(new QueueEntry(true, this.delegate::onComplete));
        this.drainQueue();
    }

    @Override
    public int read() {
        this.singleByte.clear();
        ByteBufferStoringSubscriber.TransferResult transferResult = this.delegate.blockingTransferTo(this.singleByte);
        if (this.singleByte.hasRemaining()) {
            assert (transferResult == ByteBufferStoringSubscriber.TransferResult.END_OF_STREAM);
            return -1;
        }
        return this.singleByte.get(0) & 0xFF;
    }

    @Override
    public int read(byte[] b) {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] bytes, int off, int len) {
        if (len == 0) {
            return 0;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, off, len);
        ByteBufferStoringSubscriber.TransferResult transferResult = this.delegate.blockingTransferTo(byteBuffer);
        int dataTransferred = byteBuffer.position() - off;
        if (dataTransferred == 0) {
            assert (transferResult == ByteBufferStoringSubscriber.TransferResult.END_OF_STREAM);
            return -1;
        }
        return dataTransferred;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        Object object = this.subscribeLock;
        synchronized (object) {
            if (this.inputStreamState.compareAndSet(State.UNINITIALIZED, State.CLOSED)) {
                this.delegate.onSubscribe(new NoOpSubscription());
                this.delegate.onError(new CancellationException());
            } else if (this.inputStreamState.compareAndSet(State.READABLE, State.CLOSED)) {
                this.subscription.cancel();
                this.onError(new CancellationException());
            }
        }
    }

    private void drainQueue() {
        while (this.drainingCallQueue.compareAndSet(false, true)) {
            try {
                this.doDrainQueue();
            }
            finally {
                this.drainingCallQueue.set(false);
            }
            if (!this.callQueue.isEmpty()) continue;
        }
    }

    private void doDrainQueue() {
        while (true) {
            QueueEntry entry = this.callQueue.poll();
            if (this.done || entry == null) {
                return;
            }
            this.done = entry.terminal;
            entry.call.run();
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

    private final class CancelWatcher
    implements Subscription {
        private final Subscription s;

        private CancelWatcher(Subscription s) {
            this.s = s;
        }

        public void request(long n) {
            this.s.request(n);
        }

        public void cancel() {
            this.s.cancel();
            InputStreamSubscriber.this.close();
        }
    }

    private static enum State {
        UNINITIALIZED,
        READABLE,
        CLOSED;

    }

    private static final class QueueEntry {
        private final boolean terminal;
        private final Runnable call;

        private QueueEntry(boolean terminal, Runnable call) {
            this.terminal = terminal;
            this.call = call;
        }
    }
}

