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
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourOhFourErrorLoggingFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(FourOhFourErrorLoggingFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponseWrapper withLogging = new HttpServletResponseWrapper((HttpServletResponse)servletResponse){

            public void setStatus(int sc) {
                if (log.isDebugEnabled() && sc == 404) {
                    Throwable t = new Throwable();
                    log.debug("404 code set ", t);
                }
                super.setStatus(sc);
            }

            public void setStatus(int sc, String sm) {
                if (log.isDebugEnabled() && sc == 404) {
                    Throwable t = new Throwable();
                    log.debug("404 code set with message " + sm, t);
                }
                super.setStatus(sc, sm);
            }

            public void sendError(int sc, String msg) throws IOException {
                if (log.isDebugEnabled() && sc == 404) {
                    Throwable t = new Throwable();
                    log.debug("404 error sent with message " + msg, t);
                }
                super.sendError(sc, msg);
            }

            public void sendError(int sc) throws IOException {
                if (log.isDebugEnabled() && sc == 404) {
                    Throwable t = new Throwable();
                    log.debug("404 error sent ", t);
                }
                super.sendError(sc);
            }
        };
        filterChain.doFilter(servletRequest, (ServletResponse)withLogging);
    }

    public void destroy() {
    }
}

