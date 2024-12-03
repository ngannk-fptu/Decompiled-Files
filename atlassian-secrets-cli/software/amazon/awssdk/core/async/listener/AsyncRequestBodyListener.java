/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async.listener;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.listener.PublisherListener;
import software.amazon.awssdk.core.async.listener.SubscriberListener;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public interface AsyncRequestBodyListener
extends PublisherListener<ByteBuffer> {
    public static AsyncRequestBody wrap(AsyncRequestBody delegate, AsyncRequestBodyListener listener) {
        return new NotifyingAsyncRequestBody(delegate, listener);
    }

    @SdkInternalApi
    public static final class NotifyingAsyncRequestBody
    implements AsyncRequestBody {
        private static final Logger log = Logger.loggerFor(NotifyingAsyncRequestBody.class);
        private final AsyncRequestBody delegate;
        private final AsyncRequestBodyListener listener;

        NotifyingAsyncRequestBody(AsyncRequestBody delegate, AsyncRequestBodyListener listener) {
            this.delegate = Validate.notNull(delegate, "delegate", new Object[0]);
            this.listener = Validate.notNull(listener, "listener", new Object[0]);
        }

        @Override
        public Optional<Long> contentLength() {
            return this.delegate.contentLength();
        }

        @Override
        public String contentType() {
            return this.delegate.contentType();
        }

        @Override
        public void subscribe(Subscriber<? super ByteBuffer> s) {
            NotifyingAsyncRequestBody.invoke(() -> this.listener.publisherSubscribe(s), "publisherSubscribe");
            this.delegate.subscribe(SubscriberListener.wrap(s, this.listener));
        }

        static void invoke(Runnable runnable, String callbackName) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                log.error(() -> callbackName + " callback failed. This exception will be dropped.", e);
            }
        }
    }
}

