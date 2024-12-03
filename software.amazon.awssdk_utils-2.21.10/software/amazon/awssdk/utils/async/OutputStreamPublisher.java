/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.async;

import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.CancellableOutputStream;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.SimplePublisher;

@SdkProtectedApi
public final class OutputStreamPublisher
extends CancellableOutputStream
implements Publisher<ByteBuffer> {
    private final SimplePublisher<ByteBuffer> delegate = new SimplePublisher();
    private final AtomicBoolean done = new AtomicBoolean(false);
    private ByteBuffer smallWriteBuffer;

    @Override
    public void write(int b) {
        Validate.validState(!this.done.get(), "Output stream is cancelled or closed.", new Object[0]);
        if (this.smallWriteBuffer != null && !this.smallWriteBuffer.hasRemaining()) {
            this.flush();
        }
        if (this.smallWriteBuffer == null) {
            this.smallWriteBuffer = ByteBuffer.allocate(4096);
        }
        this.smallWriteBuffer.put((byte)b);
    }

    @Override
    public void write(byte[] b) {
        this.flush();
        this.send(ByteBuffer.wrap(b));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.flush();
        this.send(ByteBuffer.wrap(b, off, len));
    }

    @Override
    public void flush() {
        if (this.smallWriteBuffer != null && this.smallWriteBuffer.position() > 0) {
            this.smallWriteBuffer.flip();
            this.send(this.smallWriteBuffer);
            this.smallWriteBuffer = null;
        }
    }

    @Override
    public void cancel() {
        if (this.done.compareAndSet(false, true)) {
            this.delegate.error(new CancellationException("Output stream has been cancelled."));
        }
    }

    @Override
    public void close() {
        if (this.done.compareAndSet(false, true)) {
            this.flush();
            this.joinInterruptiblyIgnoringCancellation(this.delegate.complete());
        }
    }

    private void send(ByteBuffer bytes) {
        CompletableFutureUtils.joinInterruptibly(this.delegate.send(bytes));
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        this.delegate.subscribe(s);
    }

    private void joinInterruptiblyIgnoringCancellation(CompletableFuture<Void> complete) {
        try {
            CompletableFutureUtils.joinInterruptibly(complete);
        }
        catch (CancellationException cancellationException) {
            // empty catch block
        }
    }
}

