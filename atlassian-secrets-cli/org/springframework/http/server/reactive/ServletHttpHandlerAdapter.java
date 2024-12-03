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
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ServletHttpHandlerAdapter
implements Servlet {
    private static final Log logger = LogFactory.getLog(ServletHttpHandlerAdapter.class);
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final String WRITE_ERROR_ATTRIBUTE_NAME = ServletHttpHandlerAdapter.class.getName() + ".ERROR";
    private final HttpHandler httpHandler;
    private int bufferSize = 8192;
    @Nullable
    private String servletPath;
    private DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory(false);

    public ServletHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull((Object)httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    public void setBufferSize(int bufferSize) {
        Assert.isTrue(bufferSize > 0, "Buffer size must be larger than zero");
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
        Assert.notNull((Object)dataBufferFactory, "DataBufferFactory must not be null");
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
                if (!path.isEmpty()) {
                    logger.info("Found Servlet mapping '" + path + "' for Servlet '" + name + "'");
                }
                return path;
            }
        }
        throw new IllegalArgumentException("Expected a single Servlet mapping: either the default Servlet mapping (i.e. '/'), or a path based mapping (e.g. '/*', '/foo/*'). Actual mappings: " + mappings + " for Servlet '" + name + "'");
    }

    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        ServerHttpRequest httpRequest;
        if (DispatcherType.ASYNC.equals((Object)request.getDispatcherType())) {
            Throwable ex = (Throwable)request.getAttribute(WRITE_ERROR_ATTRIBUTE_NAME);
            throw new ServletException("Write publisher error", ex);
        }
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(-1L);
        try {
            httpRequest = this.createRequest((HttpServletRequest)request, asyncContext);
        }
        catch (URISyntaxException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid URL for incoming request: " + ex.getMessage());
            }
            ((HttpServletResponse)response).setStatus(400);
            asyncContext.complete();
            return;
        }
        ServerHttpResponse httpResponse = this.createResponse((HttpServletResponse)response, asyncContext);
        if (httpRequest.getMethod() == HttpMethod.HEAD) {
            httpResponse = new HttpHeadResponseDecorator(httpResponse);
        }
        AtomicBoolean isCompleted = new AtomicBoolean();
        HandlerResultAsyncListener listener = new HandlerResultAsyncListener(isCompleted);
        asyncContext.addListener((AsyncListener)listener);
        HandlerResultSubscriber subscriber = new HandlerResultSubscriber(asyncContext, isCompleted);
        this.httpHandler.handle(httpRequest, httpResponse).subscribe((Subscriber)subscriber);
    }

    protected ServerHttpRequest createRequest(HttpServletRequest request, AsyncContext context) throws IOException, URISyntaxException {
        Assert.notNull((Object)this.servletPath, "Servlet path is not initialized");
        return new ServletServerHttpRequest(request, context, this.servletPath, this.getDataBufferFactory(), this.getBufferSize());
    }

    protected ServerHttpResponse createResponse(HttpServletResponse response, AsyncContext context) throws IOException {
        return new ServletServerHttpResponse(response, context, this.getDataBufferFactory(), this.getBufferSize());
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

    private class HandlerResultSubscriber
    implements Subscriber<Void> {
        private final AsyncContext asyncContext;
        private final AtomicBoolean isCompleted;

        public HandlerResultSubscriber(AsyncContext asyncContext, AtomicBoolean isCompleted) {
            this.asyncContext = asyncContext;
            this.isCompleted = isCompleted;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Void aVoid) {
        }

        @Override
        public void onError(Throwable ex) {
            logger.warn("Handling completed with error: " + ex.getMessage());
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.isCompleted, () -> {
                if (this.asyncContext.getResponse().isCommitted()) {
                    logger.debug("Dispatching into container to raise error");
                    this.asyncContext.getRequest().setAttribute(WRITE_ERROR_ATTRIBUTE_NAME, (Object)ex);
                    this.asyncContext.dispatch();
                } else {
                    try {
                        logger.debug("Setting response status code to 500");
                        this.asyncContext.getResponse().resetBuffer();
                        ((HttpServletResponse)this.asyncContext.getResponse()).setStatus(500);
                    }
                    finally {
                        this.asyncContext.complete();
                    }
                }
            });
        }

        @Override
        public void onComplete() {
            logger.debug("Handling completed with success");
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(this.asyncContext, this.isCompleted, () -> ((AsyncContext)this.asyncContext).complete());
        }
    }

    private static class HandlerResultAsyncListener
    implements AsyncListener {
        private final AtomicBoolean isCompleted;

        public HandlerResultAsyncListener(AtomicBoolean isCompleted) {
            this.isCompleted = isCompleted;
        }

        public void onTimeout(AsyncEvent event) {
            logger.debug("Timeout notification from Servlet container");
            AsyncContext context = event.getAsyncContext();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, this.isCompleted, () -> ((AsyncContext)context).complete());
        }

        public void onError(AsyncEvent event) {
            logger.debug("Error notification from Servlet container");
            AsyncContext context = event.getAsyncContext();
            ServletHttpHandlerAdapter.runIfAsyncNotComplete(context, this.isCompleted, () -> ((AsyncContext)context).complete());
        }

        public void onStartAsync(AsyncEvent event) {
        }

        public void onComplete(AsyncEvent event) {
        }
    }
}

