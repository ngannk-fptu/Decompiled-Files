/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.async.SimplePublisher;

@SdkProtectedApi
public class InputStreamConsumingPublisher
implements Publisher<ByteBuffer> {
    private static final int BUFFER_SIZE = 16384;
    private final SimplePublisher<ByteBuffer> delegate = new SimplePublisher();

    public long doBlockingWrite(InputStream inputStream) {
        try {
            long dataWritten = 0L;
            while (true) {
                byte[] data;
                int dataLength;
                if ((dataLength = inputStream.read(data = new byte[16384])) > 0) {
                    dataWritten += (long)dataLength;
                    CompletableFutureUtils.joinInterruptibly(this.delegate.send(ByteBuffer.wrap(data, 0, dataLength)));
                    continue;
                }
                if (dataLength < 0) break;
            }
            this.joinInterruptiblyIgnoringCancellation(this.delegate.complete());
            return dataWritten;
        }
        catch (IOException e) {
            CompletableFutureUtils.joinInterruptiblyIgnoringFailures(this.delegate.error(e));
            throw new UncheckedIOException(e);
        }
        catch (Error | RuntimeException e) {
            CompletableFutureUtils.joinInterruptiblyIgnoringFailures(this.delegate.error(e));
            throw e;
        }
    }

    public void cancel() {
        this.delegate.error(new CancellationException("Input stream has been cancelled."));
    }

    @Override
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

