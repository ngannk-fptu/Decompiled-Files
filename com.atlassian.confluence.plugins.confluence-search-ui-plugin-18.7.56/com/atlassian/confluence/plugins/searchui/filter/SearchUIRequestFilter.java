/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.searchui.filter;

import com.atlassian.confluence.plugins.searchui.filter.SearchUIRequestSemaphore;
import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchUIRequestFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(SearchUIRequestFilter.class);
    private static final int ACQUIRE_TIMEOUT = 500;
    private static final String NEXT_UI_SEARCH = "next.ui.search";
    private final SearchUIRequestSemaphore semaphoreHolder;

    public SearchUIRequestFilter(SearchUIRequestSemaphore semaphoreHolder) {
        this.semaphoreHolder = semaphoreHolder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!NEXT_UI_SEARCH.equalsIgnoreCase(request.getParameter("src"))) {
            chain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        Semaphore semaphore = this.semaphoreHolder.getSemaphore();
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(500L, TimeUnit.MILLISECONDS);
            if (acquired) {
                chain.doFilter((ServletRequest)request, (ServletResponse)response);
            } else {
                this.handleResponseTimeout(response);
            }
        }
        catch (InterruptedException exception) {
            log.warn(exception.getMessage(), (Throwable)exception);
            this.handleResponseTimeout(response);
        }
        finally {
            if (acquired) {
                semaphore.release();
            }
        }
    }

    private void handleResponseTimeout(HttpServletResponse response) {
        response.setStatus(408);
    }
}

