/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.DispatcherType
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DefaultDataBufferFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.server.reactive;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ServletHttpHandlerAdapter
implements Servlet {
    private static final Log logger = HttpLogging.forLogName(ServletHttpHandlerAdapter.class);
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final String WRITE_ERROR_ATTRIBUTE_NAME = ServletHttpHandlerAdapter.class.getName() + ".ERROR";
    private final HttpHandler httpHandler;
    private int bufferSize = 8192;
    @Nullable
    private String servletPath;
    private DataBufferFactory dataBufferFactory = DefaultDataBufferFactory.sharedInstance;

    public ServletHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull((Object)httpHandler, (String)"HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    public void setBufferSize(int bufferSize) {
        Assert.isTrue((bufferSize > 0 ? 1 : 0) != 0, (String)"Buffer size must be larger than zero");
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    @Nullable
    public String getServletPath() {
        return this.servletPath;
    }

    public void setDataBufferFactory(DataBufferFactory dataBufferFactory) {
        Assert.notNull((Object)dataBufferFactory, (String)"DataBufferFactory must not be null");
        this.dataBufferFactory = dataBufferFactory;
    }

    public DataBufferFactory getDataBufferFactory() {
        return this.dataBufferFactory;
    }

    public void init(ServletConfig config) {
        this.servletPath = this.getServletPath(config);
    }

    private String getServletPath(ServletConfig config) {
        String name = config.getServletName();
        ServletRegistration registration = config.getServletContext().getServletRegistration(name);
        if (registration == null) {
            throw new IllegalStateException("ServletRegistration not found for Servlet '" + name + "'");
        }
        Collection mappings = registration.getMappings();
        if (mappings.size() == 1) {
            String mapping = (String)mappings.iterator().next();
            if (mapping.equals("/")) {
                return "";
            }
            if (mapping.endsWith("/*")) {
                String path = mapping.substring(0, mapping.length() - 2);
                if (!path.isEmpty() && logger.isDebugEnabled()) {
                    logger.debug((Object)("Found servlet mapping prefix '" + path + "' for '" + name + "'"));
                }
                return path;
            }
        }
        throw new IllegalArgumentException("Expected a single Servlet mapping: either the default Servlet mapping (i.e. '/'), or a path based mapping (e.g. '/*', '/foo/*'). Actual mappings: " + mappings + " for Servlet '" + name + "'");
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        ServletServerHttpResponse wrappedResponse;
        String logPrefix;
        AsyncListener requestListener;
        ServletServerHttpRequest httpRequest;
        if (DispatcherType.ASYNC == request.getDispatcherType()) {
            Throwable ex = (Throwable)request.getAttribute(WRITE_ERROR_ATTRIBUTE_NAME);
            throw new ServletException("Failed to create response content", ex);
        }
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(-1L);
        try {
            httpRequest = this.createRequest((HttpServletRequest)request, asyncContext);
            requestListener = httpRequest.getAsyncListener();
            logPrefix = httpRequest.getLogPrefix();
        }
        catch (URISyntaxException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Failed to get request  URL: " + ex.getMessage()));
            }
            ((HttpServletResponse)response).setStatus(400);
            asyncContext.complete();
            return;
        }
        ServerHttpResponse httpResponse = wrappedResponse = this.createResponse((HttpServletResponse)response, asyncContext, httpRequest);
        AsyncListener responseListener = wrappedResponse.getAsyncListener();
        if (httpRequest.getMethod() == HttpMethod.HEAD) {
            httpResponse = new HttpHeadResponseDecorator(httpResponse);
        }
        AtomicBoolean completionFlag = new AtomicBoolean();
        HandlerResultSubscriber subscriber = new HandlerResultSubscriber(asyncContext, completionFlag, logPrefix);
        asyncContext.addListener((AsyncListener)new HttpHandlerAsyncListener(requestListener, responseListener, subscriber, completionFlag, logPrefix));
        this.httpHandler.handle(httpRequest, httpResponse).subscribe((Subscriber)subscriber);
    }

    protected ServletServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
        Assert.state((this.servletPath != null ? 1 : 0) != 0, (String)"Servlet path is not initialized");
        return new ServletServerHttpRequest(request, context, this.servletPath, this.getDataBufferFactory(), this.getBufferSize());
    }

    protected ServletServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context, ServletServerHttpRequest request) throws IOException {
        return new ServletServerHttpResponse(response, context, this.getDataBufferFactory(), this.getBufferSize(), request);
    }

    public String getServletInfo() {
        return "";
    }

    @Nullable
    public ServletConfig getServletConfig() {
        return null;
    }

    public void destroy() {
    }

    private static void runIfAsyncNotComplete(AsyncContext asyncContext, AtomicBoolean isCompleted, Runnable task) {
        try {
            if (asyncContext.getRequest().isAsyncStarted() && isCompleted.compareAndSet(false, true)) {
                task.run();
            }
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    private static class HandlerResultSubscriber
    implements Subscriber<Void>,
    Runnable {
        private final AsyncContext asyncContext;
        private final AtomicBoolean completionFlag;
        private final String logPrefix;
        @Nullable
        private volatile Subscription subscription;

        public HandlerResultSubscriber(AsyncContext asyncContext, AtomicBoolean completionFlag, String logPrefix) {
            this.asyncContext = asyncContext;
            this.completionFlag = completionFlag;
            this.logPrefix = logPrefix;
        }

        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)(this.logPrefix + "onError: " + ex));
            }
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.completionFlag, () -> {
                if (this.asyncContext.getResponse().isCommitted()) {
                    logger.trace((Object)(this.logPrefix + "Dispatch to container, to raise the error on servlet thread"));
                    this.asyncContext.getRequest().setAttribute(WRITE_ERROR_ATTRIBUTE_NAME, (Object)ex);
                    this.asyncContext.dispatch();
                } else {
                    try {
                        logger.trace((Object)(this.logPrefix + "Setting ServletResponse status to 500 Server Error"));
                        this.asyncContext.getResponse().resetBuffer();
                        ((HttpServletResponse)this.asyncContext.getResponse()).setStatus(500);
                    }
                    finally {
                        this.asyncContext.complete();
                    }
                }
            });
        }

        public void onComplete() {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)(this.logPrefix + "onComplete"));
            }
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.completionFlag, () -> ((AsyncContext)this.asyncContext).complete());
        }

        @Override
        public void run() {
            Subscription s = this.subscription;
            if (s != null) {
                s.cancel();
            }
        }
    }

    private static class HttpHandlerAsyncListener
    implements AsyncListener {
        private final AsyncListener requestAsyncListener;
        private final AsyncListener responseAsyncListener;
        private final Runnable handlerDisposeTask;
        private final AtomicBoolean completionFlag;
        private final String logPrefix;

        public HttpHandlerAsyncListener(AsyncListener requestAsyncListener, AsyncListener responseAsyncListener, Runnable handlerDisposeTask, AtomicBoolean completionFlag, String logPrefix) {
            this.requestAsyncListener = requestAsyncListener;
            this.responseAsyncListener = responseAsyncListener;
            this.handlerDisposeTask = handlerDisposeTask;
            this.completionFlag = completionFlag;
            this.logPrefix = logPrefix;
        }

        public void onTimeout(AsyncEvent event) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)(this.logPrefix + "AsyncEvent onTimeout"));
            }
            HttpHandlerAsyncListener.delegateTimeout(this.requestAsyncListener, event);
            HttpHandlerAsyncListener.delegateTimeout(this.responseAsyncListener, event);
            this.handleTimeoutOrError(event);
        }

        public void onError(AsyncEvent event) {
            Throwable ex = event.getThrowable();
            if (logger.isDebugEnabled()) {
                logger.debug((Object)(this.logPrefix + "AsyncEvent onError: " + (ex != null ? ex : "<no Throwable>")));
            }
            HttpHandlerAsyncListener.delegateError(this.requestAsyncListener, event);
            HttpHandlerAsyncListener.delegateError(this.responseAsyncListener, event);
            this.handleTimeoutOrError(event);
        }

        public void onComplete(AsyncEvent event) {
            HttpHandlerAsyncListener.delegateComplete(this.requestAsyncListener, event);
            HttpHandlerAsyncListener.delegateComplete(this.responseAsyncListener, event);
        }

        private static void delegateTimeout(AsyncListener listener, AsyncEvent event) {
            try {
                listener.onTimeout(event);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        private static void delegateError(AsyncListener listener, AsyncEvent event) {
            try {
                listener.onError(event);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        private static void delegateComplete(AsyncListener listener, AsyncEvent event) {
            try {
                listener.onComplete(event);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        private void handleTimeoutOrError(AsyncEvent event) {
            AsyncContext context = event.getAsyncContext();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, this.completionFlag, () -> {
                try {
                    this.handlerDisposeTask.run();
                }
                finally {
                    context.complete();
                }
            });
        }

        public void onStartAsync(AsyncEvent event) {
        }
    }
}

