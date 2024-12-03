/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.util.Assert
 */
package org.springframework.web.context.request.async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncWebRequest;

public class StandardServletAsyncWebRequest
extends ServletWebRequest
implements AsyncWebRequest,
AsyncListener {
    private Long timeout;
    private AsyncContext asyncContext;
    private AtomicBoolean asyncCompleted = new AtomicBoolean();
    private final List<Runnable> timeoutHandlers = new ArrayList<Runnable>();
    private final List<Consumer<Throwable>> exceptionHandlers = new ArrayList<Consumer<Throwable>>();
    private final List<Runnable> completionHandlers = new ArrayList<Runnable>();

    public StandardServletAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public void setTimeout(Long timeout) {
        Assert.state((!this.isAsyncStarted() ? 1 : 0) != 0, (String)"Cannot change the timeout with concurrent handling in progress");
        this.timeout = timeout;
    }

    @Override
    public void addTimeoutHandler(Runnable timeoutHandler) {
        this.timeoutHandlers.add(timeoutHandler);
    }

    @Override
    public void addErrorHandler(Consumer<Throwable> exceptionHandler) {
        this.exceptionHandlers.add(exceptionHandler);
    }

    @Override
    public void addCompletionHandler(Runnable runnable) {
        this.completionHandlers.add(runnable);
    }

    @Override
    public boolean isAsyncStarted() {
        return this.asyncContext != null && this.getRequest().isAsyncStarted();
    }

    @Override
    public boolean isAsyncComplete() {
        return this.asyncCompleted.get();
    }

    @Override
    public void startAsync() {
        Assert.state((boolean)this.getRequest().isAsyncSupported(), (String)"Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml.");
        Assert.state((!this.isAsyncComplete() ? 1 : 0) != 0, (String)"Async processing has already completed");
        if (this.isAsyncStarted()) {
            return;
        }
        this.asyncContext = this.getRequest().startAsync((ServletRequest)this.getRequest(), (ServletResponse)this.getResponse());
        this.asyncContext.addListener((AsyncListener)this);
        if (this.timeout != null) {
            this.asyncContext.setTimeout(this.timeout.longValue());
        }
    }

    @Override
    public void dispatch() {
        Assert.notNull((Object)this.asyncContext, (String)"Cannot dispatch without an AsyncContext");
        this.asyncContext.dispatch();
    }

    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    public void onError(AsyncEvent event) throws IOException {
        this.exceptionHandlers.forEach(consumer -> consumer.accept(event.getThrowable()));
    }

    public void onTimeout(AsyncEvent event) throws IOException {
        this.timeoutHandlers.forEach(Runnable::run);
    }

    public void onComplete(AsyncEvent event) throws IOException {
        this.completionHandlers.forEach(Runnable::run);
        this.asyncContext = null;
        this.asyncCompleted.set(true);
    }
}

