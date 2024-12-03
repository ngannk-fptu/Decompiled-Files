/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.filter.SecurityFilter
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.johnson.Johnson;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.filter.SecurityFilter;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ConfluenceSecurityFilter
extends SecurityFilter {
    static final String ALREADY_FILTERED = "os_securityfilter_already_filtered";

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (this.bypassFilter()) {
            chain.doFilter(req, res);
        } else {
            this.applyFilter(req, res, chain);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    void applyFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        Object previousAlreadyFiltered = req.getAttribute(ALREADY_FILTERED);
        req.setAttribute(ALREADY_FILTERED, null);
        Principal existingUser = this.getAuthenticationContext().getUser();
        try {
            super.doFilter(req, res, chain);
        }
        finally {
            this.getAuthenticationContext().setUser(existingUser);
            req.setAttribute(ALREADY_FILTERED, previousAlreadyFiltered);
        }
    }

    @VisibleForTesting
    boolean bypassFilter() {
        return Johnson.getEventContainer().hasEvent(JohnsonEventPredicates.hasLevel(JohnsonEventLevel.FATAL));
    }

    @VisibleForTesting
    protected SecurityConfig getSecurityConfig() {
        return super.getSecurityConfig();
    }
}

