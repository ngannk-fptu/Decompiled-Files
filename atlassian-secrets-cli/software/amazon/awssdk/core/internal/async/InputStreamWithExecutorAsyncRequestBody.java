/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodyFromInputStreamConfiguration;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.exception.NonRetryableException;
import software.amazon.awssdk.core.internal.util.NoopSubscription;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class InputStreamWithExecutorAsyncRequestBody
implements AsyncRequestBody {
    private static final Logger log = Logger.loggerFor(InputStreamWithExecutorAsyncRequestBody.class);
    private final Object subscribeLock = new Object();
    private final InputStream inputStream;
    private final Long contentLength;
    private final ExecutorService executor;
    private Future<?> writeFuture;

    public InputStreamWithExecutorAsyncRequestBody(AsyncRequestBodyFromInputStreamConfiguration configuration) {
        this.inputStream = configuration.inputStream();
        this.contentLength = configuration.contentLength();
        this.executor = configuration.executor();
        IoUtils.markStreamWithMaxReadLimit(this.inputStream, configuration.maxReadLimit());
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.ofNullable(this.contentLength);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        Object object = this.subscribeLock;
        synchronized (object) {
            try {
                if (this.writeFuture != null) {
                    this.writeFuture.cancel(true);
                    this.waitForCancellation(this.writeFuture);
                    this.tryReset(this.inputStream);
                }
                BlockingInputStreamAsyncRequestBody delegate = AsyncRequestBody.forBlockingInputStream(this.contentLength);
                this.writeFuture = this.executor.submit(() -> this.doBlockingWrite(delegate));
                delegate.subscribe(s);
            }
            catch (Throwable t) {
                s.onSubscribe(new NoopSubscription(s));
                s.onError(t);
            }
        }
    }

    private void tryReset(InputStream inputStream) {
        try {
            inputStream.reset();
        }
        catch (IOException e) {
            String message = "Request cannot be retried, because the request stream could not be reset.";
            throw NonRetryableException.create(message, e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkTestInternalApi
    public Future<?> activeWriteFuture() {
        Object object = this.subscribeLock;
        synchronized (object) {
            return this.writeFuture;
        }
    }

    private void doBlockingWrite(BlockingInputStreamAsyncRequestBody asyncRequestBody) {
        try {
            asyncRequestBody.writeInputStream(this.inputStream);
        }
        catch (Throwable t) {
            log.debug(() -> "Encountered error while writing input stream to service.", t);
            throw t;
        }
    }

    private void waitForCancellation(Future<?> writeFuture) {
        try {
            writeFuture.get(10L, TimeUnit.SECONDS);
        }
        catch (CancellationException | ExecutionException exception) {
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (TimeoutException e) {
            throw new IllegalStateException("Timed out waiting to reset the input stream.", e);
        }
    }
}

