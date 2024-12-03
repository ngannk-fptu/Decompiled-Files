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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.filters;

import com.atlassian.confluence.extra.jira.request.SingleJiraIssuesThreadLocalAccessor;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleJiraIssuesMapThreadLocalFilter
implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleJiraIssuesMapThreadLocalFilter.class);

    public void init(FilterConfig filterConfig) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        try {
            SingleJiraIssuesThreadLocalAccessor.init();
            chain.doFilter(request, response);
        }
        finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("******* Total execution time: {} milliseconds", (Object)(System.currentTimeMillis() - start));
            }
        }
    }

    public void destroy() {
    }
}

