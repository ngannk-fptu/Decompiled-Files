/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

public abstract class AbstractRequestLoggingFilter
extends OncePerRequestFilter {
    public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";
    public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";
    public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";
    public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";
    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;
    private boolean includeQueryString = false;
    private boolean includeClientInfo = false;
    private boolean includeHeaders = false;
    private boolean includePayload = false;
    private int maxPayloadLength = 50;
    private String beforeMessagePrefix = "Before request [";
    private String beforeMessageSuffix = "]";
    private String afterMessagePrefix = "After request [";
    private String afterMessageSuffix = "]";

    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    protected boolean isIncludeQueryString() {
        return this.includeQueryString;
    }

    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    protected boolean isIncludeHeaders() {
        return this.includeHeaders;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    protected boolean isIncludePayload() {
        return this.includePayload;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    protected int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }

    public void setBeforeMessagePrefix(String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }

    public void setBeforeMessageSuffix(String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }

    public void setAfterMessagePrefix(String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }

    public void setAfterMessageSuffix(String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean shouldLog;
        boolean isFirstRequest = !this.isAsyncDispatch(request);
        Object requestToUse = request;
        if (this.isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request, this.getMaxPayloadLength());
        }
        if ((shouldLog = this.shouldLog((HttpServletRequest)requestToUse)) && isFirstRequest) {
            this.beforeRequest((HttpServletRequest)requestToUse, this.getBeforeMessage((HttpServletRequest)requestToUse));
        }
        try {
            filterChain.doFilter((ServletRequest)requestToUse, (ServletResponse)response);
        }
        finally {
            if (shouldLog && !this.isAsyncStarted((HttpServletRequest)requestToUse)) {
                this.afterRequest((HttpServletRequest)requestToUse, this.getAfterMessage((HttpServletRequest)requestToUse));
            }
        }
    }

    private String getBeforeMessage(HttpServletRequest request) {
        return this.createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
    }

    private String getAfterMessage(HttpServletRequest request) {
        return this.createMessage(request, this.afterMessagePrefix, this.afterMessageSuffix);
    }

    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        String payload;
        String queryString;
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());
        if (this.isIncludeQueryString() && (queryString = request.getQueryString()) != null) {
            msg.append('?').append(queryString);
        }
        if (this.isIncludeClientInfo()) {
            String user;
            HttpSession session;
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            if ((session = request.getSession(false)) != null) {
                msg.append(";session=").append(session.getId());
            }
            if ((user = request.getRemoteUser()) != null) {
                msg.append(";user=").append(user);
            }
        }
        if (this.isIncludeHeaders()) {
            msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
        }
        if (this.isIncludePayload() && (payload = this.getMessagePayload(request)) != null) {
            msg.append(";payload=").append(payload);
        }
        msg.append(suffix);
        return msg.toString();
    }

    @Nullable
    protected String getMessagePayload(HttpServletRequest request) {
        byte[] buf;
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest((ServletRequest)request, ContentCachingRequestWrapper.class);
        if (wrapper != null && (buf = wrapper.getContentAsByteArray()).length > 0) {
            int length = Math.min(buf.length, this.getMaxPayloadLength());
            try {
                return new String(buf, 0, length, wrapper.getCharacterEncoding());
            }
            catch (UnsupportedEncodingException ex) {
                return "[unknown]";
            }
        }
        return null;
    }

    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    protected abstract void beforeRequest(HttpServletRequest var1, String var2);

    protected abstract void afterRequest(HttpServletRequest var1, String var2);
}

