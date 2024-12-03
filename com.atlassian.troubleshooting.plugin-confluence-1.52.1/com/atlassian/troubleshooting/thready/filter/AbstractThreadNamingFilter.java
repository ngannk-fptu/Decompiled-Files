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
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.thready.filter;

import com.atlassian.troubleshooting.thready.manager.RequestValidator;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import com.atlassian.troubleshooting.thready.manager.ThreadNameManager;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractThreadNamingFilter
implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractThreadNamingFilter.class);
    private final ThreadNameManager threadNameManager;
    private final ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager;
    private final RequestValidator requestValidator;

    protected AbstractThreadNamingFilter(ThreadNameManager threadNameManager, ThreadDiagnosticsConfigurationManager threadDiagnosticsConfigurationManager, RequestValidator requestValidator) {
        this.threadNameManager = Objects.requireNonNull(threadNameManager);
        this.threadDiagnosticsConfigurationManager = Objects.requireNonNull(threadDiagnosticsConfigurationManager);
        this.requestValidator = Objects.requireNonNull(requestValidator);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        boolean changeThreadName = this.shouldChangeThreadName(httpServletRequest);
        boolean shouldRestoreThreadName = this.isUnchanged();
        try {
            if (changeThreadName) {
                this.setThreadName(httpServletRequest);
            }
            chain.doFilter(request, response);
        }
        finally {
            if (shouldRestoreThreadName) {
                try {
                    this.threadNameManager.clearThreadAttributes();
                    this.threadNameManager.setThreadName();
                }
                catch (Exception e) {
                    LOGGER.error("Error restoring thread name", (Throwable)e);
                }
            }
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    private void setThreadName(HttpServletRequest request) {
        try {
            this.updateAttributes(request, this.threadNameManager);
            this.threadNameManager.setThreadName();
        }
        catch (Exception e) {
            LOGGER.error("Error setting thread name", (Throwable)e);
        }
    }

    protected abstract void updateAttributes(HttpServletRequest var1, ThreadNameManager var2);

    private boolean shouldChangeThreadName(HttpServletRequest request) {
        try {
            return this.threadDiagnosticsConfigurationManager.isThreadNameAttributesEnabled() && !this.requestValidator.isResourceRequest(request);
        }
        catch (Exception e) {
            LOGGER.error("Error checking thread diagnostics configuration", (Throwable)e);
            return false;
        }
    }

    private boolean isUnchanged() {
        try {
            return this.threadNameManager.isUnchanged();
        }
        catch (Exception e) {
            LOGGER.error("Error checking thread diagnostics status", (Throwable)e);
            return true;
        }
    }
}

