/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.plugin.servlet.filter.ServletFilterModuleContainerFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.johnson.plugin.servlet.filter;

import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.plugin.servlet.filter.ServletFilterModuleContainerFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JohnsonServletFilterModuleContainerFilter
extends ServletFilterModuleContainerFilter {
    private static final Logger log = LoggerFactory.getLogger(JohnsonServletFilterModuleContainerFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.bypassFilters()) {
            log.debug("{}: Bypassing plugin-provided filters; the system is locked", (Object)this.getFilterLocation());
            chain.doFilter(request, response);
        } else {
            super.doFilter(request, response, chain);
        }
    }

    protected boolean bypassFilters() {
        return this.getEventContainer().hasEvents();
    }

    protected JohnsonEventContainer getEventContainer() {
        return Johnson.getEventContainer((ServletContext)this.getFilterConfig().getServletContext());
    }
}

