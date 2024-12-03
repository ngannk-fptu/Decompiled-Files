/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.RelativeRedirectResponseWrapper;

public class RelativeRedirectFilter
extends OncePerRequestFilter {
    private HttpStatus redirectStatus = HttpStatus.SEE_OTHER;

    public void setRedirectStatus(HttpStatus status) {
        Assert.notNull((Object)status, "Property 'redirectStatus' is required");
        Assert.isTrue(status.is3xxRedirection(), () -> "Not a redirect status code: " + (Object)((Object)status));
        this.redirectStatus = status;
    }

    public HttpStatus getRedirectStatus() {
        return this.redirectStatus;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response = RelativeRedirectResponseWrapper.wrapIfNecessary(response, this.redirectStatus);
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

