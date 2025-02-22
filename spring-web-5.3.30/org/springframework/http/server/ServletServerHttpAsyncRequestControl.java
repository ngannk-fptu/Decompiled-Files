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
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ServletServerHttpAsyncRequestControl
implements ServerHttpAsyncRequestControl,
AsyncListener {
    private static final long NO_TIMEOUT_VALUE = Long.MIN_VALUE;
    private final ServletServerHttpRequest request;
    private final ServletServerHttpResponse response;
    @Nullable
    private AsyncContext asyncContext;
    private AtomicBoolean asyncCompleted = new AtomicBoolean();

    public ServletServerHttpAsyncRequestControl(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        Assert.notNull((Object)request, (String)"request is required");
        Assert.notNull((Object)response, (String)"response is required");
        Assert.isTrue((boolean)request.getServletRequest().isAsyncSupported(), (String)"Async support must be enabled on a servlet and for all filters involved in async request processing. This is done in Java code using the Servlet API or by adding \"<async-supported>true</async-supported>\" to servlet and filter declarations in web.xml. Also you must use a Servlet 3.0+ container");
        this.request = request;
        this.response = response;
    }

    @Override
    public boolean isStarted() {
        return this.asyncContext != null && this.request.getServletRequest().isAsyncStarted();
    }

    @Override
    public boolean isCompleted() {
        return this.asyncCompleted.get();
    }

    @Override
    public void start() {
        this.start(Long.MIN_VALUE);
    }

    @Override
    public void start(long timeout) {
        Assert.state((!this.isCompleted() ? 1 : 0) != 0, (String)"Async processing has already completed");
        if (this.isStarted()) {
            return;
        }
        HttpServletRequest servletRequest = this.request.getServletRequest();
        HttpServletResponse servletResponse = this.response.getServletResponse();
        this.asyncContext = servletRequest.startAsync((ServletRequest)servletRequest, (ServletResponse)servletResponse);
        this.asyncContext.addListener((AsyncListener)this);
        if (timeout != Long.MIN_VALUE) {
            this.asyncContext.setTimeout(timeout);
        }
    }

    @Override
    public void complete() {
        if (this.asyncContext != null && this.isStarted() && !this.isCompleted()) {
            this.asyncContext.complete();
        }
    }

    public void onComplete(AsyncEvent event) throws IOException {
        this.asyncContext = null;
        this.asyncCompleted.set(true);
    }

    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    public void onError(AsyncEvent event) throws IOException {
    }

    public void onTimeout(AsyncEvent event) throws IOException {
    }
}

