/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.crt.http.HttpRequestBodyStream
 *  software.amazon.awssdk.http.async.SdkHttpContentPublisher
 *  software.amazon.awssdk.utils.async.ByteBufferStoringSubscriber
 *  software.amazon.awssdk.utils.async.ByteBufferStoringSubscriber$TransferResult
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.http.HttpRequestBodyStream;
import software.amazon.awssdk.http.async.SdkHttpContentPublisher;
import software.amazon.awssdk.utils.async.ByteBufferStoringSubscriber;

@SdkInternalApi
public final class S3CrtRequestBodyStreamAdapter
implements HttpRequestBodyStream {
    private static final long MINIMUM_BYTES_BUFFERED = 0x100000L;
    private final SdkHttpContentPublisher bodyPublisher;
    private final ByteBufferStoringSubscriber requestBodySubscriber;
    private final AtomicBoolean subscribed = new AtomicBoolean(false);

    public S3CrtRequestBodyStreamAdapter(SdkHttpContentPublisher bodyPublisher) {
        this.bodyPublisher = bodyPublisher;
        this.requestBodySubscriber = new ByteBufferStoringSubscriber(0x100000L);
    }

    public boolean sendRequestBody(ByteBuffer outBuffer) {
        if (this.subscribed.compareAndSet(false, true)) {
            this.bodyPublisher.subscribe((Subscriber)this.requestBodySubscriber);
        }
        return this.requestBodySubscriber.transferTo(outBuffer) == ByteBufferStoringSubscriber.TransferResult.END_OF_STREAM;
    }

    public long getLength() {
        return this.bodyPublisher.contentLength().orElse(0L);
    }
}

