/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.catalina.filters.RequestFilter;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class RemoteHostFilter
extends RequestFilter {
    private final Log log = LogFactory.getLog(RemoteHostFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.process(request.getRemoteHost(), request, response, chain);
    }

    @Override
    protected Log getLogger() {
        return this.log;
    }
}

