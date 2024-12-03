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
 */
package com.atlassian.core.filters.encoding;

import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.core.filters.encoding.FixedHtmlEncodingResponseWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractEncodingFilter
extends AbstractHttpFilter {
    @Override
    protected final void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setCharacterEncoding(this.getEncoding());
        response.setContentType(this.getContentType());
        filterChain.doFilter((ServletRequest)request, (ServletResponse)new FixedHtmlEncodingResponseWrapper(response));
    }

    protected abstract String getContentType();

    protected abstract String getEncoding();
}

