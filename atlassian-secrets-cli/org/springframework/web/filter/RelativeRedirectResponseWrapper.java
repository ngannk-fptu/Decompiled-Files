/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package org.springframework.web.filter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;

class RelativeRedirectResponseWrapper
extends HttpServletResponseWrapper {
    private final HttpStatus redirectStatus;

    private RelativeRedirectResponseWrapper(HttpServletResponse response, HttpStatus redirectStatus) {
        super(response);
        Assert.notNull((Object)redirectStatus, "'redirectStatus' is required");
        this.redirectStatus = redirectStatus;
    }

    public void sendRedirect(String location) {
        this.setStatus(this.redirectStatus.value());
        this.setHeader("Location", location);
    }

    public static HttpServletResponse wrapIfNecessary(HttpServletResponse response, HttpStatus redirectStatus) {
        RelativeRedirectResponseWrapper wrapper = WebUtils.getNativeResponse((ServletResponse)response, RelativeRedirectResponseWrapper.class);
        return wrapper != null ? response : new RelativeRedirectResponseWrapper(response, redirectStatus);
    }
}

