/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 */
package com.atlassian.core.filters;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public abstract class AbstractFilter
implements Filter {
    public FilterConfig getFilterConfig() {
        return null;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        try {
            this.init(filterConfig);
        }
        catch (ServletException servletException) {
            // empty catch block
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}

