/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class ByteBuffersAsyncRequestBody
implements AsyncRequestBody {
    private static final Logger log = Logger.loggerFor(ByteBuffersAsyncRequestBody.class);
    private final String mimetype;
    private final Long length;
    private final ByteBuffer[] buffers;

    private ByteBuffersAsyncRequestBody(String mimetype, Long length, ByteBuffer ... buffers) {
        this.mimetype = mimetype;
        this.length = length;
        this.buffers = buffers;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.ofNullable(this.length);
    }

    @Override
    public String contentType() {
        return this.mimetype;
    }

    public void subscribe(final Subscriber<? super ByteBuffer> s) {
        if (s == null) {
            throw new NullPointerException("Subscription MUST NOT be null.");
        }
        try {
            s.onSubscribe(new Subscription(){
                private final AtomicInteger index = new AtomicInteger(0);
                private final AtomicBoolean completed = new AtomicBoolean(false);

                public void request(long n) {
                    if (this.completed.get()) {
                        return;
                    }
                    if (n > 0L) {
                        int i = this.index.getAndIncrement();
                        if (i >= ByteBuffersAsyncRequestBody.this.buffers.length) {
                            return;
                        }
                        long remaining = n;
                        do {
                            ByteBuffer buffer = ByteBuffersAsyncRequestBody.this.buffers[i];
                            s.onNext((Object)buffer.asReadOnlyBuffer());
                        } while (--remaining > 0L && (i = this.index.getAndIncrement()) < ByteBuffersAsyncRequestBody.this.buffers.length);
                        if (i >= ByteBuffersAsyncRequestBody.this.buffers.length - 1 && this.completed.compareAndSet(false, true)) {
                            s.onComplete();
                        }
                    } else {
                        s.onError((Throwable)new IllegalArgumentException("\u00a73.9: non-positive requests are not allowed!"));
                    }
                }

                public void cancel() {
                    this.completed.set(true);
                }
            });
        }
        catch (Throwable ex) {
            log.error(() -> s + " violated the Reactive Streams rule 2.13 by throwing an exception from onSubscribe.", ex);
        }
    }

    public static ByteBuffersAsyncRequestBody of(ByteBuffer ... buffers) {
        long length = Arrays.stream(buffers).mapToLong(Buffer::remaining).sum();
        return new ByteBuffersAsyncRequestBody("application/octet-stream", length, buffers);
    }

    public static ByteBuffersAsyncRequestBody of(Long length, ByteBuffer ... buffers) {
        return new ByteBuffersAsyncRequestBody("application/octet-stream", length, buffers);
    }

    public static ByteBuffersAsyncRequestBody of(String mimetype, ByteBuffer ... buffers) {
        long length = Arrays.stream(buffers).mapToLong(Buffer::remaining).sum();
        return new ByteBuffersAsyncRequestBody(mimetype, length, buffers);
    }

    public static ByteBuffersAsyncRequestBody of(String mimetype, Long length, ByteBuffer ... buffers) {
        return new ByteBuffersAsyncRequestBody(mimetype, length, buffers);
    }

    public static ByteBuffersAsyncRequestBody from(byte[] bytes) {
        return new ByteBuffersAsyncRequestBody("application/octet-stream", Long.valueOf(bytes.length), ByteBuffer.wrap(bytes));
    }

    public static ByteBuffersAsyncRequestBody from(String mimetype, byte[] bytes) {
        return new ByteBuffersAsyncRequestBody(mimetype, Long.valueOf(bytes.length), ByteBuffer.wrap(bytes));
    }
}

