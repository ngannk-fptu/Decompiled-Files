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
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

public class ShallowEtagHeaderFilter
extends OncePerRequestFilter {
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
        if (!this.isAsyncDispatch(request) && !(response instanceof ConditionalContentCachingResponseWrapper)) {
            responseToUse = new ConditionalContentCachingResponseWrapper(response, request);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)responseToUse);
        if (!this.isAsyncStarted(request) && !ShallowEtagHeaderFilter.isContentCachingDisabled(request)) {
            this.updateResponse(request, (HttpServletResponse)responseToUse);
        }
    }

    private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConditionalContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse((ServletResponse)response, ConditionalContentCachingResponseWrapper.class);
        Assert.notNull((Object)wrapper, "ContentCachingResponseWrapper not found");
        HttpServletResponse rawResponse = (HttpServletResponse)wrapper.getResponse();
        if (this.isEligibleForEtag(request, (HttpServletResponse)wrapper, wrapper.getStatus(), wrapper.getContentInputStream())) {
            String eTag = wrapper.getHeader("ETag");
            if (!StringUtils.hasText(eTag)) {
                eTag = this.generateETagHeaderValue(wrapper.getContentInputStream(), this.writeWeakETag);
                rawResponse.setHeader("ETag", eTag);
            }
            if (new ServletWebRequest(request, rawResponse).checkNotModified(eTag)) {
                return;
            }
        }
        wrapper.copyBodyToResponse();
    }

    protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response, int responseStatusCode, InputStream inputStream) {
        if (!response.isCommitted() && responseStatusCode >= 200 && responseStatusCode < 300 && HttpMethod.GET.matches(request.getMethod())) {
            String cacheControl = response.getHeader("Cache-Control");
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

    private static class ConditionalContentCachingResponseWrapper
    extends ContentCachingResponseWrapper {
        private final HttpServletRequest request;

        ConditionalContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return ShallowEtagHeaderFilter.isContentCachingDisabled(this.request) || this.hasETag() ? this.getResponse().getOutputStream() : super.getOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return ShallowEtagHeaderFilter.isContentCachingDisabled(this.request) || this.hasETag() ? this.getResponse().getWriter() : super.getWriter();
        }

        private boolean hasETag() {
            return StringUtils.hasText(this.getHeader("ETag"));
        }
    }
}

