/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.plugin.servlet.filter;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public final class IteratingFilterChain
implements FilterChain {
    private final Iterator<Filter> iterator;
    private final FilterChain chain;

    public IteratingFilterChain(Iterator<Filter> iterator, FilterChain chain) {
        this.iterator = iterator;
        this.chain = chain;
    }

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (this.iterator.hasNext()) {
            Filter filter = this.iterator.next();
            filter.doFilter(request, response, (FilterChain)this);
        } else {
            this.chain.doFilter(request, response);
        }
    }
}

