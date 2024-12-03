/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class CommonsRequestLoggingFilter
extends AbstractRequestLoggingFilter {
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return this.logger.isDebugEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        this.logger.debug((Object)message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        this.logger.debug((Object)message);
    }
}

