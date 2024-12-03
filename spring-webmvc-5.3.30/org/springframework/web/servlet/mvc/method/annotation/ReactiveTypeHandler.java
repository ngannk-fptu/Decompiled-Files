/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Publisher
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.task.SimpleAsyncTaskExecutor
 *  org.springframework.core.task.SyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.http.MediaType
 *  org.springframework.http.codec.ServerSentEvent
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MimeType
 *  org.springframework.web.HttpMediaTypeNotAcceptableException
 *  org.springframework.web.accept.ContentNegotiationManager
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.async.DeferredResult
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class ReactiveTypeHandler {
    private static final long STREAMING_TIMEOUT_VALUE = -1L;
    private static final List<MediaType> JSON_STREAMING_MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_STREAM_JSON);
    private static final Log logger = LogFactory.getLog(ReactiveTypeHandler.class);
    private final ReactiveAdapterRegistry adapterRegistry;
    private final TaskExecutor taskExecutor;
    private final ContentNegotiationManager contentNegotiationManager;
    private boolean taskExecutorWarning;

    public ReactiveTypeHandler() {
        this(ReactiveAdapterRegistry.getSharedInstance(), (TaskExecutor)new SyncTaskExecutor(), new ContentNegotiationManager());
    }

    ReactiveTypeHandler(ReactiveAdapterRegistry registry, TaskExecutor executor, ContentNegotiationManager manager) {
        Assert.notNull((Object)registry, (String)"ReactiveAdapterRegistry is required");
        Assert.notNull((Object)executor, (String)"TaskExecutor is required");
        Assert.notNull((Object)manager, (String)"ContentNegotiationManager is required");
        this.adapterRegistry = registry;
        this.taskExecutor = executor;
        this.contentNegotiationManager = manager;
        this.taskExecutorWarning = executor instanceof SimpleAsyncTaskExecutor || executor instanceof SyncTaskExecutor;
    }

    public boolean isReactiveType(Class<?> type) {
        return this.adapterRegistry.getAdapter(type) != null;
    }

    @Nullable
    public ResponseBodyEmitter handleValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mav, NativeWebRequest request) throws Exception {
        Assert.notNull((Object)returnValue, (String)"Expected return value");
        ReactiveAdapter adapter = this.adapterRegistry.getAdapter(returnValue.getClass());
        Assert.state((adapter != null ? 1 : 0) != 0, () -> "Unexpected return value: " + returnValue);
        ResolvableType elementType = ResolvableType.forMethodParameter((MethodParameter)returnType).getGeneric(new int[0]);
        Class elementClass = elementType.toClass();
        Collection<MediaType> mediaTypes = this.getMediaTypes(request);
        Optional<MediaType> mediaType = mediaTypes.stream().filter(MimeType::isConcrete).findFirst();
        if (adapter.isMultiValue()) {
            if (mediaTypes.stream().anyMatch(arg_0 -> ((MediaType)MediaType.TEXT_EVENT_STREAM).includes(arg_0)) || ServerSentEvent.class.isAssignableFrom(elementClass)) {
                this.logExecutorWarning(returnType);
                SseEmitter emitter = new SseEmitter(-1L);
                new SseEmitterSubscriber(emitter, this.taskExecutor).connect(adapter, returnValue);
                return emitter;
            }
            if (CharSequence.class.isAssignableFrom(elementClass)) {
                this.logExecutorWarning(returnType);
                ResponseBodyEmitter emitter = this.getEmitter(mediaType.orElse(MediaType.TEXT_PLAIN));
                new TextEmitterSubscriber(emitter, this.taskExecutor).connect(adapter, returnValue);
                return emitter;
            }
            for (MediaType type : mediaTypes) {
                for (MediaType streamingType : JSON_STREAMING_MEDIA_TYPES) {
                    if (!streamingType.includes(type)) continue;
                    this.logExecutorWarning(returnType);
                    ResponseBodyEmitter emitter = this.getEmitter(streamingType);
                    new JsonEmitterSubscriber(emitter, this.taskExecutor).connect(adapter, returnValue);
                    return emitter;
                }
            }
        }
        DeferredResult result = new DeferredResult();
        new DeferredResultSubscriber((DeferredResult<Object>)result, adapter, elementType).connect(adapter, returnValue);
        WebAsyncUtils.getAsyncManager((WebRequest)request).startDeferredResultProcessing(result, new Object[]{mav});
        return null;
    }

    private Collection<MediaType> getMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        Collection mediaTypes = (Collection)request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, 0);
        return CollectionUtils.isEmpty((Collection)mediaTypes) ? this.contentNegotiationManager.resolveMediaTypes(request) : mediaTypes;
    }

    private ResponseBodyEmitter getEmitter(final MediaType mediaType) {
        return new ResponseBodyEmitter(-1L){

            @Override
            protected void extendResponse(ServerHttpResponse outputMessage) {
                outputMessage.getHeaders().setContentType(mediaType);
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void logExecutorWarning(MethodParameter returnType) {
        if (this.taskExecutorWarning && logger.isWarnEnabled()) {
            ReactiveTypeHandler reactiveTypeHandler = this;
            synchronized (reactiveTypeHandler) {
                if (this.taskExecutorWarning) {
                    String executorTypeName = this.taskExecutor.getClass().getSimpleName();
                    logger.warn((Object)("\n!!!\nStreaming through a reactive type requires an Executor to write to the response.\nPlease, configure a TaskExecutor in the MVC config under \"async support\".\nThe " + executorTypeName + " currently in use is not suitable under load.\n-------------------------------\nController:\t" + returnType.getContainingClass().getName() + "\nMethod:\t\t" + returnType.getMethod().getName() + "\nReturning:\t" + ResolvableType.forMethodParameter((MethodParameter)returnType) + "\n!!!"));
                    this.taskExecutorWarning = false;
                }
            }
        }
    }

    static class CollectedValuesList
    extends ArrayList<Object> {
        private final ResolvableType elementType;

        CollectedValuesList(ResolvableType elementType) {
            this.elementType = elementType;
        }

        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics(List.class, (ResolvableType[])new ResolvableType[]{this.elementType});
        }
    }

    private static class DeferredResultSubscriber
    implements Subscriber<Object> {
        private final DeferredResult<Object> result;
        private final boolean multiValueSource;
        private final CollectedValuesList values;

        DeferredResultSubscriber(DeferredResult<Object> result, ReactiveAdapter adapter, ResolvableType elementType) {
            this.result = result;
            this.multiValueSource = adapter.isMultiValue();
            this.values = new CollectedValuesList(elementType);
        }

        public void connect(ReactiveAdapter adapter, Object returnValue) {
            Publisher publisher = adapter.toPublisher(returnValue);
            publisher.subscribe((Subscriber)this);
        }

        public void onSubscribe(Subscription subscription) {
            this.result.onTimeout(() -> ((Subscription)subscription).cancel());
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Object element) {
            this.values.add(element);
        }

        public void onError(Throwable ex) {
            this.result.setErrorResult((Object)ex);
        }

        public void onComplete() {
            if (this.values.size() > 1 || this.multiValueSource) {
                this.result.setResult((Object)this.values);
            } else if (this.values.size() == 1) {
                this.result.setResult(this.values.get(0));
            } else {
                this.result.setResult(null);
            }
        }
    }

    private static class TextEmitterSubscriber
    extends AbstractEmitterSubscriber {
        TextEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            super(emitter, executor);
        }

        @Override
        protected void send(Object element) throws IOException {
            this.getEmitter().send(element, MediaType.TEXT_PLAIN);
        }
    }

    private static class JsonEmitterSubscriber
    extends AbstractEmitterSubscriber {
        JsonEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            super(emitter, executor);
        }

        @Override
        protected void send(Object element) throws IOException {
            this.getEmitter().send(element, MediaType.APPLICATION_JSON);
            this.getEmitter().send("\n", MediaType.TEXT_PLAIN);
        }
    }

    private static class SseEmitterSubscriber
    extends AbstractEmitterSubscriber {
        SseEmitterSubscriber(SseEmitter sseEmitter, TaskExecutor executor) {
            super(sseEmitter, executor);
        }

        @Override
        protected void send(Object element) throws IOException {
            if (element instanceof ServerSentEvent) {
                ServerSentEvent event = (ServerSentEvent)element;
                ((SseEmitter)this.getEmitter()).send(this.adapt(event));
            } else {
                this.getEmitter().send(element, MediaType.APPLICATION_JSON);
            }
        }

        private SseEmitter.SseEventBuilder adapt(ServerSentEvent<?> sse) {
            SseEmitter.SseEventBuilder builder = SseEmitter.event();
            String id = sse.id();
            String event = sse.event();
            Duration retry = sse.retry();
            String comment = sse.comment();
            Object data = sse.data();
            if (id != null) {
                builder.id(id);
            }
            if (event != null) {
                builder.name(event);
            }
            if (data != null) {
                builder.data(data);
            }
            if (retry != null) {
                builder.reconnectTime(retry.toMillis());
            }
            if (comment != null) {
                builder.comment(comment);
            }
            return builder;
        }
    }

    private static abstract class AbstractEmitterSubscriber
    implements Subscriber<Object>,
    Runnable {
        private final ResponseBodyEmitter emitter;
        private final TaskExecutor taskExecutor;
        @Nullable
        private Subscription subscription;
        private final AtomicReference<Object> elementRef = new AtomicReference();
        @Nullable
        private Throwable error;
        private volatile boolean terminated;
        private final AtomicLong executing = new AtomicLong();
        private volatile boolean done;

        protected AbstractEmitterSubscriber(ResponseBodyEmitter emitter, TaskExecutor executor) {
            this.emitter = emitter;
            this.taskExecutor = executor;
        }

        public void connect(ReactiveAdapter adapter, Object returnValue) {
            Publisher publisher = adapter.toPublisher(returnValue);
            publisher.subscribe((Subscriber)this);
        }

        protected ResponseBodyEmitter getEmitter() {
            return this.emitter;
        }

        public final void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.emitter.onTimeout(() -> {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Connection timeout for " + this.emitter));
                }
                this.terminate();
                this.emitter.complete();
            });
            this.emitter.onError(this.emitter::completeWithError);
            subscription.request(1L);
        }

        public final void onNext(Object element) {
            this.elementRef.lazySet(element);
            this.trySchedule();
        }

        public final void onError(Throwable ex) {
            this.error = ex;
            this.terminated = true;
            this.trySchedule();
        }

        public final void onComplete() {
            this.terminated = true;
            this.trySchedule();
        }

        private void trySchedule() {
            if (this.executing.getAndIncrement() == 0L) {
                this.schedule();
            }
        }

        private void schedule() {
            try {
                this.taskExecutor.execute((Runnable)this);
            }
            catch (Throwable ex) {
                try {
                    this.terminate();
                }
                finally {
                    this.executing.decrementAndGet();
                    this.elementRef.lazySet(null);
                }
            }
        }

        @Override
        public void run() {
            if (this.done) {
                this.elementRef.lazySet(null);
                return;
            }
            boolean isTerminated = this.terminated;
            Object element = this.elementRef.get();
            if (element != null) {
                this.elementRef.lazySet(null);
                Assert.state((this.subscription != null ? 1 : 0) != 0, (String)"No subscription");
                try {
                    this.send(element);
                    this.subscription.request(1L);
                }
                catch (Throwable ex) {
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Send for " + this.emitter + " failed: " + ex));
                    }
                    this.terminate();
                    return;
                }
            }
            if (isTerminated) {
                this.done = true;
                Throwable ex = this.error;
                this.error = null;
                if (ex != null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Publisher for " + this.emitter + " failed: " + ex));
                    }
                    this.emitter.completeWithError(ex);
                } else {
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Publisher for " + this.emitter + " completed"));
                    }
                    this.emitter.complete();
                }
                return;
            }
            if (this.executing.decrementAndGet() != 0L) {
                this.schedule();
            }
        }

        protected abstract void send(Object var1) throws IOException;

        private void terminate() {
            this.done = true;
            if (this.subscription != null) {
                this.subscription.cancel();
            }
        }
    }
}

