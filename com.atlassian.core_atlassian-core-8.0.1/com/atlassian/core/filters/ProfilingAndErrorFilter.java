/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.filters.ProfilingFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.core.filters;

import com.atlassian.core.logging.ThreadLocalErrorCollection;
import com.atlassian.util.profiling.filters.ProfilingFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ProfilingAndErrorFilter
extends ProfilingFilter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ThreadLocalErrorCollection.clear();
        ThreadLocalErrorCollection.enable();
        try {
            super.doFilter(servletRequest, servletResponse, filterChain);
        }
        finally {
            ThreadLocalErrorCollection.disable();
            ThreadLocalErrorCollection.clear();
        }
    }
}

