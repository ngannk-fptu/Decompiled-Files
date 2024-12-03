/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.core.util.Clock
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.PathMatcher
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.util.DefaultClock;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.core.util.Clock;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class ConfluenceTimeoutFilter
extends AbstractHttpFilter {
    static final String SESSION_TIMESTAMP = "confluence.security.timestamp";
    static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    final Clock clock;
    Iterable<String> urlsToExclude;

    public ConfluenceTimeoutFilter() {
        this(new DefaultClock());
    }

    public ConfluenceTimeoutFilter(Clock clock) {
        this.clock = clock;
    }

    public void init(FilterConfig config) throws ServletException {
        String urlPatternsToExcludeSessionExtension = config.getInitParameter("urlPatternsToExclude");
        this.urlsToExclude = urlPatternsToExcludeSessionExtension == null ? Collections.emptyList() : Splitter.on((char)',').trimResults().split((CharSequence)urlPatternsToExcludeSessionExtension);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            this.renewSessionTimeoutIfNeeded(request, session);
        }
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    private void renewSessionTimeoutIfNeeded(HttpServletRequest request, HttpSession session) {
        Long sessionTimestamp = (Long)session.getAttribute(SESSION_TIMESTAMP);
        long currentTime = this.clock.getCurrentDate().getTime();
        if (sessionTimestamp == null) {
            session.setAttribute(SESSION_TIMESTAMP, (Object)currentTime);
            return;
        }
        long sessionLife = TimeUnit.SECONDS.toMillis(session.getMaxInactiveInterval());
        if (currentTime >= sessionTimestamp + sessionLife) {
            session.invalidate();
            return;
        }
        String requestURI = request.getRequestURI().substring(request.getContextPath().length());
        boolean matched = Iterables.any(this.urlsToExclude, input -> PATH_MATCHER.match(input, requestURI));
        if (!matched) {
            session.setAttribute(SESSION_TIMESTAMP, (Object)currentTime);
        }
    }
}

