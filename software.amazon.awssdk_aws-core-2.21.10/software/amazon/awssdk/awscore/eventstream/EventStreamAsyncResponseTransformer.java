/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 *  software.amazon.awssdk.core.async.SdkPublisher
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpFullResponse$Builder
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.eventstream.HeaderValue
 *  software.amazon.eventstream.Message
 *  software.amazon.eventstream.MessageDecoder
 */
package software.amazon.awssdk.awscore.eventstream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.eventstream.EventStreamResponseHandler;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;
import software.amazon.eventstream.HeaderValue;
import software.amazon.eventstream.Message;
import software.amazon.eventstream.MessageDecoder;

@SdkProtectedApi
public final class EventStreamAsyncResponseTransformer<ResponseT, EventT>
implements AsyncResponseTransformer<SdkResponse, Void> {
    private static final Logger log = Logger.loggerFor(EventStreamAsyncResponseTransformer.class);
    private final EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler;
    private final HttpResponseHandler<? extends ResponseT> initialResponseHandler;
    private final HttpResponseHandler<? extends EventT> eventResponseHandler;
    private final HttpResponseHandler<? extends Throwable> exceptionResponseHandler;
    private final Supplier<ExecutionAttributes> attributesFactory;
    private final CompletableFuture<Void> future;
    private final AtomicBoolean exceptionsMayBeSent = new AtomicBoolean(true);
    private volatile CompletableFuture<Void> transformFuture;
    private volatile String requestId = null;
    private volatile String extendedRequestId = null;

    private EventStreamAsyncResponseTransformer(EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler, HttpResponseHandler<? extends ResponseT> initialResponseHandler, HttpResponseHandler<? extends EventT> eventResponseHandler, HttpResponseHandler<? extends Throwable> exceptionResponseHandler, CompletableFuture<Void> future, String serviceName) {
        this.eventStreamResponseHandler = eventStreamResponseHandler;
        this.initialResponseHandler = initialResponseHandler;
        this.eventResponseHandler = eventResponseHandler;
        this.exceptionResponseHandler = exceptionResponseHandler;
        this.future = future;
        this.attributesFactory = () -> new ExecutionAttributes().putAttribute(SdkExecutionAttribute.SERVICE_NAME, (Object)serviceName);
    }

    public static <ResponseT, EventT> Builder<ResponseT, EventT> builder() {
        return new Builder();
    }

    public CompletableFuture<Void> prepare() {
        this.transformFuture = new CompletableFuture();
        return this.transformFuture;
    }

    public void onResponse(SdkResponse response) {
        if (response != null && response.sdkHttpResponse() != null) {
            this.requestId = response.sdkHttpResponse().firstMatchingHeader((Collection)HttpResponseHandler.X_AMZN_REQUEST_ID_HEADERS).orElse(null);
            this.extendedRequestId = response.sdkHttpResponse().firstMatchingHeader("x-amz-id-2").orElse(null);
            log.debug(() -> this.getLogPrefix() + "Received HTTP response headers: " + response);
        }
    }

    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        Validate.isTrue((this.transformFuture != null ? 1 : 0) != 0, (String)"onStream() invoked without prepare().", (Object[])new Object[0]);
        this.exceptionsMayBeSent.set(true);
        SynchronousMessageDecoder decoder = new SynchronousMessageDecoder();
        this.eventStreamResponseHandler.onEventStream(publisher.flatMapIterable(x$0 -> decoder.decode(x$0)).flatMapIterable(this::transformMessage).doAfterOnComplete(this::handleOnStreamComplete).doAfterOnError(this::handleOnStreamError).doAfterOnCancel(this::handleOnStreamCancel));
    }

    public void exceptionOccurred(Throwable throwable) {
        if (this.exceptionsMayBeSent.compareAndSet(true, false)) {
            try {
                this.eventStreamResponseHandler.exceptionOccurred(throwable);
            }
            catch (RuntimeException e) {
                log.warn(() -> "Exception raised by exceptionOccurred. Ignoring.", (Throwable)e);
            }
            this.transformFuture.completeExceptionally(throwable);
        }
    }

    private void handleOnStreamComplete() {
        log.trace(() -> this.getLogPrefix() + "Event stream completed successfully.");
        this.exceptionsMayBeSent.set(false);
        this.eventStreamResponseHandler.complete();
        this.transformFuture.complete(null);
        this.future.complete(null);
    }

    private void handleOnStreamError(Throwable throwable) {
        log.trace(() -> this.getLogPrefix() + "Event stream failed.", throwable);
        this.exceptionOccurred(throwable);
    }

    private void handleOnStreamCancel() {
        log.trace(() -> this.getLogPrefix() + "Event stream cancelled.");
        this.exceptionsMayBeSent.set(false);
        this.transformFuture.complete(null);
        this.future.complete(null);
    }

    private Iterable<EventT> transformMessage(Message message) {
        try {
            if (this.isEvent(message)) {
                return this.transformEventMessage(message);
            }
            if (this.isError(message) || this.isException(message)) {
                throw this.transformErrorMessage(message);
            }
            log.debug(() -> this.getLogPrefix() + "Decoded a message of an unknown type, it will be dropped: " + message);
            return Collections.emptyList();
        }
        catch (Error | SdkException e) {
            throw e;
        }
        catch (Throwable e) {
            throw SdkClientException.builder().cause(e).build();
        }
    }

    private Iterable<EventT> transformEventMessage(Message message) throws Exception {
        SdkHttpFullResponse response = this.adaptMessageToResponse(message, false);
        if (((HeaderValue)message.getHeaders().get(":event-type")).getString().equals("initial-response")) {
            Object initialResponse = this.initialResponseHandler.handle(response, this.attributesFactory.get());
            this.eventStreamResponseHandler.responseReceived(initialResponse);
            log.debug(() -> this.getLogPrefix() + "Decoded initial response: " + initialResponse);
            return Collections.emptyList();
        }
        Object event = this.eventResponseHandler.handle(response, this.attributesFactory.get());
        log.debug(() -> this.getLogPrefix() + "Decoded event: " + event);
        return Collections.singleton(event);
    }

    private Throwable transformErrorMessage(Message message) throws Exception {
        SdkHttpFullResponse errorResponse = this.adaptMessageToResponse(message, true);
        Throwable exception = (Throwable)this.exceptionResponseHandler.handle(errorResponse, this.attributesFactory.get());
        log.debug(() -> this.getLogPrefix() + "Decoded error or exception: " + exception, exception);
        return exception;
    }

    private String getLogPrefix() {
        if (this.requestId == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append("RequestId: ").append(this.requestId);
        if (this.extendedRequestId != null) {
            stringBuilder.append(", ExtendedRequestId: ").append(this.extendedRequestId);
        }
        stringBuilder.append(") ");
        return stringBuilder.toString();
    }

    private SdkHttpFullResponse adaptMessageToResponse(Message message, boolean isException) {
        Map headers = message.getHeaders().entrySet().stream().collect(HashMap::new, (m, e) -> m.put(e.getKey(), Collections.singletonList(((HeaderValue)e.getValue()).getString())), Map::putAll);
        if (this.requestId != null) {
            headers.put("x-amzn-RequestId", Collections.singletonList(this.requestId));
        }
        if (this.extendedRequestId != null) {
            headers.put("x-amz-id-2", Collections.singletonList(this.extendedRequestId));
        }
        SdkHttpFullResponse.Builder builder = SdkHttpFullResponse.builder().content(AbortableInputStream.create((InputStream)new ByteArrayInputStream(message.getPayload()))).headers(headers);
        if (!isException) {
            builder.statusCode(200);
        }
        return builder.build();
    }

    private boolean isEvent(Message m) {
        return "event".equals(((HeaderValue)m.getHeaders().get(":message-type")).getString());
    }

    private boolean isError(Message m) {
        return "error".equals(((HeaderValue)m.getHeaders().get(":message-type")).getString());
    }

    private boolean isException(Message m) {
        return "exception".equals(((HeaderValue)m.getHeaders().get(":message-type")).getString());
    }

    public static final class Builder<ResponseT, EventT> {
        private EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler;
        private HttpResponseHandler<? extends ResponseT> initialResponseHandler;
        private HttpResponseHandler<? extends EventT> eventResponseHandler;
        private HttpResponseHandler<? extends Throwable> exceptionResponseHandler;
        private CompletableFuture<Void> future;
        private String serviceName;

        private Builder() {
        }

        public Builder<ResponseT, EventT> eventStreamResponseHandler(EventStreamResponseHandler<ResponseT, EventT> eventStreamResponseHandler) {
            this.eventStreamResponseHandler = eventStreamResponseHandler;
            return this;
        }

        public Builder<ResponseT, EventT> initialResponseHandler(HttpResponseHandler<? extends ResponseT> initialResponseHandler) {
            this.initialResponseHandler = initialResponseHandler;
            return this;
        }

        public Builder<ResponseT, EventT> eventResponseHandler(HttpResponseHandler<? extends EventT> eventResponseHandler) {
            this.eventResponseHandler = eventResponseHandler;
            return this;
        }

        public Builder<ResponseT, EventT> exceptionResponseHandler(HttpResponseHandler<? extends Throwable> exceptionResponseHandler) {
            this.exceptionResponseHandler = exceptionResponseHandler;
            return this;
        }

        @Deprecated
        public Builder<ResponseT, EventT> executor(Executor executor) {
            return this;
        }

        public Builder<ResponseT, EventT> future(CompletableFuture<Void> future) {
            this.future = future;
            return this;
        }

        public Builder<ResponseT, EventT> serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public EventStreamAsyncResponseTransformer<ResponseT, EventT> build() {
            return new EventStreamAsyncResponseTransformer(this.eventStreamResponseHandler, this.initialResponseHandler, this.eventResponseHandler, this.exceptionResponseHandler, this.future, this.serviceName);
        }
    }

    private static final class SynchronousMessageDecoder {
        private final MessageDecoder decoder = new MessageDecoder();

        private SynchronousMessageDecoder() {
        }

        private Iterable<Message> decode(ByteBuffer bytes) {
            this.decoder.feed(bytes);
            return this.decoder.getDecodedMessages();
        }
    }
}

