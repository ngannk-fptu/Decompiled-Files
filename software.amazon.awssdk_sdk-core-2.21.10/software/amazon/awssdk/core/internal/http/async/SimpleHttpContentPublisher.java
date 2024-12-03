/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.async.SdkHttpContentPublisher
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.core.internal.http.async;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.async.SdkHttpContentPublisher;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public final class SimpleHttpContentPublisher
implements SdkHttpContentPublisher {
    private final byte[] content;
    private final int length;

    public SimpleHttpContentPublisher(SdkHttpFullRequest request) {
        this.content = request.contentStreamProvider().map(p -> (byte[])FunctionalUtils.invokeSafely(() -> IoUtils.toByteArray((InputStream)p.newStream()))).orElseGet(() -> new byte[0]);
        this.length = this.content.length;
    }

    public Optional<Long> contentLength() {
        return Optional.of(Long.valueOf(this.length));
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        s.onSubscribe((Subscription)new SubscriptionImpl(s));
    }

    private class SubscriptionImpl
    implements Subscription {
        private boolean running = true;
        private final Subscriber<? super ByteBuffer> s;

        private SubscriptionImpl(Subscriber<? super ByteBuffer> s) {
            this.s = s;
        }

        public void request(long n) {
            if (this.running) {
                this.running = false;
                if (n <= 0L) {
                    this.s.onError((Throwable)new IllegalArgumentException("Demand must be positive"));
                } else {
                    this.s.onNext((Object)ByteBuffer.wrap(SimpleHttpContentPublisher.this.content));
                    this.s.onComplete();
                }
            }
        }

        public void cancel() {
            this.running = false;
        }
    }
}

