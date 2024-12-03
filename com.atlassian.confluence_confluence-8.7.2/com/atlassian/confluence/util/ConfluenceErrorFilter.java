/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.logging.ThreadLocalErrorCollection
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.confluence.util;

import com.atlassian.core.logging.ThreadLocalErrorCollection;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ConfluenceErrorFilter
implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ThreadLocalErrorCollection.clear();
        ThreadLocalErrorCollection.enable();
        try {
            chain.doFilter(request, response);
        }
        finally {
            ThreadLocalErrorCollection.disable();
        }
    }

    public void destroy() {
    }
}

