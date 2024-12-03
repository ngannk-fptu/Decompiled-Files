/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.quickreload;

import com.atlassian.confluence.plugins.quickreload.QuickReloadCaches;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QuickReloadFilter
implements Filter {
    private static final Pattern PATH_INFO_PATTERN = Pattern.compile("/quickreload/latest/(\\d+)");
    private final QuickReloadCaches caches;

    public QuickReloadFilter(QuickReloadCaches caches) {
        this.caches = caches;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest)request;
        Matcher matcher = PATH_INFO_PATTERN.matcher(servletRequest.getPathInfo());
        Long pageId = matcher.matches() ? QuickReloadFilter.tryParseLong(matcher.group(1)) : null;
        Long since = QuickReloadFilter.tryParseLong(servletRequest.getParameter("since"));
        if (pageId == null || since == null || this.caches.hasAccessModeUpdate(since) || this.caches.hasUpdates(pageId, since)) {
            chain.doFilter(request, response);
        } else {
            response.setContentType("application/json");
            ((HttpServletResponse)response).setStatus(204);
            response.flushBuffer();
        }
    }

    public void destroy() {
    }

    private static Long tryParseLong(String val) {
        if (val != null) {
            try {
                return Long.parseLong(val);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return null;
    }
}

