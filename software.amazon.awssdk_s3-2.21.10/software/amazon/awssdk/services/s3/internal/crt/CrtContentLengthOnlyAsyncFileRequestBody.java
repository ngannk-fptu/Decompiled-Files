/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;

@SdkInternalApi
public final class CrtContentLengthOnlyAsyncFileRequestBody
implements AsyncRequestBody {
    private final AsyncRequestBody asyncRequestBody;

    public CrtContentLengthOnlyAsyncFileRequestBody(Path path) {
        this.asyncRequestBody = AsyncRequestBody.fromFile((Path)path);
    }

    public Optional<Long> contentLength() {
        return this.asyncRequestBody.contentLength();
    }

    public void subscribe(final Subscriber<? super ByteBuffer> subscriber) {
        subscriber.onSubscribe(new Subscription(){

            public void request(long l) {
                subscriber.onError((Throwable)new IllegalStateException("subscription not supported"));
            }

            public void cancel() {
            }
        });
    }
}

