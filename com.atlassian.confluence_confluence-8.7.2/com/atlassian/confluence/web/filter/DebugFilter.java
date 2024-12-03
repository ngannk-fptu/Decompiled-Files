/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(DebugFilter.class);
    private String phase;

    @Deprecated
    public DebugFilter() {
    }

    public DebugFilter(String phase) {
        this.phase = Objects.requireNonNull(phase);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        if (this.phase == null) {
            this.phase = filterConfig.getInitParameter("phase");
        }
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        DebugHttpServletRequestWrapper requestWrapper = new DebugHttpServletRequestWrapper(request, response);
        LoggingResponseWrapper responseWrapper = new LoggingResponseWrapper(request, response);
        try {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Pre-filter execution (committed: {}) for URI {}", new Object[]{request.getDispatcherType(), this.phase, response.isCommitted(), request.getRequestURI()});
            }
            filterChain.doFilter((ServletRequest)requestWrapper, (ServletResponse)responseWrapper);
        }
        catch (IOException | Error | RuntimeException | ServletException e) {
            try {
                log.debug("{} {} {}", new Object[]{request.getDispatcherType(), this.phase, e.getClass().getSimpleName(), e});
                throw e;
            }
            catch (Throwable throwable) {
                responseWrapper.logCurrentHeaders();
                if (log.isDebugEnabled()) {
                    log.debug("{} {} Post-filter execution (committed: {}) for URI {}", new Object[]{request.getDispatcherType(), this.phase, response.isCommitted(), request.getRequestURI()});
                }
                throw throwable;
            }
        }
        responseWrapper.logCurrentHeaders();
        if (log.isDebugEnabled()) {
            log.debug("{} {} Post-filter execution (committed: {}) for URI {}", new Object[]{request.getDispatcherType(), this.phase, response.isCommitted(), request.getRequestURI()});
        }
    }

    private class LoggingResponseWrapper
    extends HttpServletResponseWrapper {
        private Map<String, Object> headers;
        private HttpServletRequest request;

        public LoggingResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
            this.headers = new HashMap<String, Object>();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Getting output stream (committed: {}) for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, this.isCommitted(), this.request.getRequestURI()});
            }
            return super.getOutputStream();
        }

        public PrintWriter getWriter() throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Getting writer (committed: {}) for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, this.isCommitted(), this.request.getRequestURI()});
            }
            return super.getWriter();
        }

        public void flushBuffer() throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Flushing buffer (committed: {}) for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, this.isCommitted(), this.request.getRequestURI()});
            }
            super.flushBuffer();
        }

        public void sendRedirect(String location) throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Sending Redirect to {} for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, location, this.request.getRequestURI()});
            }
            super.sendRedirect(location);
        }

        public void sendError(int sc, String msg) throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Sending Error: {}: {} for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, sc, msg, this.request.getRequestURI()});
            }
            super.sendError(sc, msg);
        }

        public void sendError(int sc) throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Sending Error: {}: {} for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, sc, "<no message>", this.request.getRequestURI()});
            }
            super.sendError(sc);
        }

        public void setStatus(int sc) {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Setting Status: {}: {} for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, sc, "<no message>", this.request.getRequestURI()});
            }
            super.setStatus(sc);
        }

        public void setStatus(int sc, String msg) {
            if (log.isDebugEnabled()) {
                log.debug("{} {} Setting Status: {}: {} for URI {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, sc, msg, this.request.getRequestURI()});
            }
            super.setStatus(sc, msg);
        }

        public void setHeader(String key, String val) {
            super.setHeader(key, val);
            this.headers.put(key, val);
        }

        public void addHeader(String key, String val) {
            super.addHeader(key, val);
            this.headers.put(key, val);
        }

        public void addDateHeader(String key, long val) {
            super.addDateHeader(key, val);
            this.headers.put(key, val);
        }

        public void setDateHeader(String key, long val) {
            super.setDateHeader(key, val);
            this.headers.put(key, val);
        }

        public void addIntHeader(String key, int val) {
            super.addIntHeader(key, val);
            this.headers.put(key, val);
        }

        public void setIntHeader(String key, int val) {
            super.setIntHeader(key, val);
            this.headers.put(key, val);
        }

        public void logCurrentHeaders() {
            if (log.isDebugEnabled()) {
                try {
                    int size = this.headers != null ? this.headers.size() : -1;
                    log.debug("{} {} Listing {} headers:", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, size});
                    if (size > 0) {
                        this.headers.forEach((key, value) -> {
                            if (this.containsHeader((String)key)) {
                                log.debug("{} {} {}: {}", new Object[]{this.request.getDispatcherType(), DebugFilter.this.phase, key, value});
                            }
                        });
                    }
                }
                catch (Throwable e) {
                    try {
                        log.error("Error logging headers, message: {}", (Object)e.getMessage());
                        e.printStackTrace();
                    }
                    catch (Throwable e1) {
                        log.error("Error logging the error logging headers, message: {}", (Object)e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private static class DebugHttpServletRequestWrapper
    extends HttpServletRequestWrapper {
        private final HttpServletResponse response;

        public DebugHttpServletRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
            super(request);
            this.response = response;
        }

        public HttpSession getSession() {
            return this.getSession(true);
        }

        public HttpSession getSession(boolean create) {
            try {
                return super.getSession(create);
            }
            catch (IllegalStateException ex) {
                if (this.response.isCommitted()) {
                    log.error("HttpServletRequest#getSession has been invoked after the response has been committed for URI {}", (Object)this.getRequestURI());
                }
                throw ex;
            }
        }
    }
}

