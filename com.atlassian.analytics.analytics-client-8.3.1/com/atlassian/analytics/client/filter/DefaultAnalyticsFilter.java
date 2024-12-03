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
package com.atlassian.analytics.client.filter;

import com.atlassian.analytics.client.filter.AbstractHttpFilter;
import com.atlassian.analytics.client.sen.SenProvider;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultAnalyticsFilter
extends AbstractHttpFilter {
    private final SenProvider senProvider;

    public DefaultAnalyticsFilter(SenProvider senProvider) {
        this.senProvider = senProvider;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Optional<String> sen = this.senProvider.getSen();
        if (sen.isPresent()) {
            DefaultAnalyticsFilter.setB3TraceId(request);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

