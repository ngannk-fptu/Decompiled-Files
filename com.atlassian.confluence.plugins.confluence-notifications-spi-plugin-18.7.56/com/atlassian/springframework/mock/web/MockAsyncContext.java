/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.BeanUtils
 *  org.springframework.util.Assert
 *  org.springframework.web.util.WebUtils
 */
package com.atlassian.springframework.mock.web;

import com.atlassian.springframework.mock.web.MockHttpServletRequest;
import com.atlassian.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

public class MockAsyncContext
implements AsyncContext {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final List<AsyncListener> listeners = new ArrayList<AsyncListener>();
    private final List<Runnable> dispatchHandlers = new ArrayList<Runnable>();
    private String dispatchedPath;
    private long timeout = 10000L;

    public MockAsyncContext(ServletRequest request, ServletResponse response) {
        this.request = (HttpServletRequest)request;
        this.response = (HttpServletResponse)response;
    }

    public void addDispatchHandler(Runnable handler) {
        Assert.notNull((Object)handler);
        this.dispatchHandlers.add(handler);
    }

    public ServletRequest getRequest() {
        return this.request;
    }

    public ServletResponse getResponse() {
        return this.response;
    }

    public boolean hasOriginalRequestAndResponse() {
        return this.request instanceof MockHttpServletRequest && this.response instanceof MockHttpServletResponse;
    }

    public void dispatch() {
        this.dispatch(this.request.getRequestURI());
    }

    public void dispatch(String path) {
        this.dispatch(null, path);
    }

    public void dispatch(ServletContext context, String path) {
        this.dispatchedPath = path;
        for (Runnable r : this.dispatchHandlers) {
            r.run();
        }
    }

    public String getDispatchedPath() {
        return this.dispatchedPath;
    }

    public void complete() {
        MockHttpServletRequest mockRequest = (MockHttpServletRequest)WebUtils.getNativeRequest((ServletRequest)this.request, MockHttpServletRequest.class);
        if (mockRequest != null) {
            mockRequest.setAsyncStarted(false);
        }
        for (AsyncListener listener : this.listeners) {
            try {
                listener.onComplete(new AsyncEvent((AsyncContext)this, (ServletRequest)this.request, (ServletResponse)this.response));
            }
            catch (IOException e) {
                throw new IllegalStateException("AsyncListener failure", e);
            }
        }
    }

    public void start(Runnable runnable) {
        runnable.run();
    }

    public void addListener(AsyncListener listener) {
        this.listeners.add(listener);
    }

    public void addListener(AsyncListener listener, ServletRequest request, ServletResponse response) {
        this.listeners.add(listener);
    }

    public List<AsyncListener> getListeners() {
        return this.listeners;
    }

    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        return (T)((AsyncListener)BeanUtils.instantiateClass(clazz));
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}

