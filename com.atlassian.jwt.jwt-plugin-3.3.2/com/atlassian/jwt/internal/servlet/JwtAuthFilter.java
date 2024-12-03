/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Status
 *  com.atlassian.sal.api.net.Request$MethodType
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.jwt.internal.servlet;

import com.atlassian.jwt.core.JwtUtil;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.net.Request;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthFilter
implements Filter {
    private final Authenticator authenticator;
    private final AuthenticationListener authenticationListener;
    private final AuthenticationController authenticationController;

    public JwtAuthFilter(AuthenticationListener authenticationListener, Authenticator authenticator, AuthenticationController authenticationController) {
        this.authenticationListener = authenticationListener;
        this.authenticator = authenticator;
        this.authenticationController = authenticationController;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.mayProceed(this.getHttpServletRequest(request), this.getHttpServletResponse(response))) {
            chain.doFilter(request, response);
        }
    }

    private HttpServletResponse getHttpServletResponse(ServletResponse response) {
        return (HttpServletResponse)response;
    }

    private HttpServletRequest getHttpServletRequest(ServletRequest request) {
        return (HttpServletRequest)request;
    }

    private boolean authenticationNotAttempted(HttpServletRequest request, HttpServletResponse response) {
        this.authenticationListener.authenticationNotAttempted(request, response);
        return true;
    }

    private boolean mayProceed(HttpServletRequest request, HttpServletResponse response) {
        if (!this.authenticationController.shouldAttemptAuthentication(request)) {
            return this.authenticationNotAttempted(request, response);
        }
        if (JwtAuthFilter.isOptions(request)) {
            return this.authenticationNotAttempted(request, response);
        }
        if (!JwtUtil.requestContainsJwt(request)) {
            return this.authenticationNotAttempted(request, response);
        }
        Authenticator.Result result = this.authenticator.authenticate(request, response);
        if (result.getStatus() == Authenticator.Result.Status.FAILED) {
            this.authenticationListener.authenticationFailure(result, request, response);
            return false;
        }
        if (result.getStatus() == Authenticator.Result.Status.ERROR) {
            this.authenticationListener.authenticationError(result, request, response);
            return false;
        }
        this.authenticationListener.authenticationNotAttempted(request, response);
        return true;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    private static boolean isOptions(HttpServletRequest request) {
        return Request.MethodType.OPTIONS.name().equalsIgnoreCase(request.getMethod());
    }
}

