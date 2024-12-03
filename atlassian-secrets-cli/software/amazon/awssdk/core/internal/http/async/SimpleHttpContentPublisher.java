/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.async;

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
        this.content = request.contentStreamProvider().map(p -> FunctionalUtils.invokeSafely(() -> IoUtils.toByteArray(p.newStream()))).orElseGet(() -> new byte[0]);
        this.length = this.content.length;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.of(Long.valueOf(this.length));
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        s.onSubscribe(new SubscriptionImpl(s));
    }

    private class SubscriptionImpl
    implements Subscription {
        private boolean running = true;
        private final Subscriber<? super ByteBuffer> s;

        private SubscriptionImpl(Subscriber<? super ByteBuffer> s) {
            this.s = s;
        }

        @Override
        public void request(long n) {
            if (this.running) {
                this.running = false;
                if (n <= 0L) {
                    this.s.onError(new IllegalArgumentException("Demand must be positive"));
                } else {
                    this.s.onNext(ByteBuffer.wrap(SimpleHttpContentPublisher.this.content));
                    this.s.onComplete();
                }
            }
        }

        @Override
        public void cancel() {
            this.running = false;
        }
    }
}

