/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class ServletContextRequestLoggingFilter
extends AbstractRequestLoggingFilter {
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        this.getServletContext().log(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        this.getServletContext().log(message);
    }
}

