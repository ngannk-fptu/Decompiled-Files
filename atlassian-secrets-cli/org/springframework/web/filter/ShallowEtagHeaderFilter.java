/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

public class ShallowEtagHeaderFilter
extends OncePerRequestFilter {
    private static final String HEADER_ETAG = "ETag";
    private static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String DIRECTIVE_NO_STORE = "no-store";
    private static final String STREAMING_ATTRIBUTE = ShallowEtagHeaderFilter.class.getName() + ".STREAMING";
    private boolean writeWeakETag = false;

    public void setWriteWeakETag(boolean writeWeakETag) {
        this.writeWeakETag = writeWeakETag;
    }

    public boolean isWriteWeakETag() {
        return this.writeWeakETag;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Object responseToUse = response;
        if (!this.isAsyncDispatch(request) && !(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new HttpStreamingAwareContentCachingResponseWrapper(response, request);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)responseToUse);
        if (!this.isAsyncStarted(request) && !ShallowEtagHeaderFilter.isContentCachingDisabled(request)) {
            this.updateResponse(request, (HttpServletResponse)responseToUse);
        }
    }

    private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse((ServletResponse)response, ContentCachingResponseWrapper.class);
        Assert.notNull((Object)responseWrapper, "ContentCachingResponseWrapper not found");
        HttpServletResponse rawResponse = (HttpServletResponse)responseWrapper.getResponse();
        int statusCode = responseWrapper.getStatusCode();
        if (rawResponse.isCommitted()) {
            responseWrapper.copyBodyToResponse();
        } else if (this.isEligibleForEtag(request, (HttpServletResponse)responseWrapper, statusCode, responseWrapper.getContentInputStream())) {
            String responseETag = this.generateETagHeaderValue(responseWrapper.getContentInputStream(), this.writeWeakETag);
            rawResponse.setHeader(HEADER_ETAG, responseETag);
            String requestETag = request.getHeader(HEADER_IF_NONE_MATCH);
            if (requestETag != null && ("*".equals(requestETag) || responseETag.equals(requestETag) || responseETag.replaceFirst("^W/", "").equals(requestETag.replaceFirst("^W/", "")))) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("ETag [" + responseETag + "] equal to If-None-Match, sending 304");
                }
                rawResponse.setStatus(304);
            } else {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("ETag [" + responseETag + "] not equal to If-None-Match [" + requestETag + "], sending normal response");
                }
                responseWrapper.copyBodyToResponse();
            }
        } else {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Response with status code [" + statusCode + "] not eligible for ETag");
            }
            responseWrapper.copyBodyToResponse();
        }
    }

    protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response, int responseStatusCode, InputStream inputStream) {
        String method = request.getMethod();
        if (responseStatusCode >= 200 && responseStatusCode < 300 && HttpMethod.GET.matches(method)) {
            String cacheControl = response.getHeader(HEADER_CACHE_CONTROL);
            return cacheControl == null || !cacheControl.contains(DIRECTIVE_NO_STORE);
        }
        return false;
    }

    protected String generateETagHeaderValue(InputStream inputStream, boolean isWeak) throws IOException {
        StringBuilder builder = new StringBuilder(37);
        if (isWeak) {
            builder.append("W/");
        }
        builder.append("\"0");
        DigestUtils.appendMd5DigestAsHex(inputStream, builder);
        builder.append('\"');
        return builder.toString();
    }

    public static void disableContentCaching(ServletRequest request) {
        Assert.notNull((Object)request, "ServletRequest must not be null");
        request.setAttribute(STREAMING_ATTRIBUTE, (Object)true);
    }

    private static boolean isContentCachingDisabled(HttpServletRequest request) {
        return request.getAttribute(STREAMING_ATTRIBUTE) != null;
    }

    private static class HttpStreamingAwareContentCachingResponseWrapper
    extends ContentCachingResponseWrapper {
        private final HttpServletRequest request;

        public HttpStreamingAwareContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return this.useRawResponse() ? this.getResponse().getOutputStream() : super.getOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return this.useRawResponse() ? this.getResponse().getWriter() : super.getWriter();
        }

        private boolean useRawResponse() {
            return ShallowEtagHeaderFilter.isContentCachingDisabled(this.request);
        }
    }
}

