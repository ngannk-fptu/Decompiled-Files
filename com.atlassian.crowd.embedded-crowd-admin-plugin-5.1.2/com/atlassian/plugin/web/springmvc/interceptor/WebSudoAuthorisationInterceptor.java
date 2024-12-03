/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 */
package com.atlassian.plugin.web.springmvc.interceptor;

import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public final class WebSudoAuthorisationInterceptor
extends HandlerInterceptorAdapter {
    private final WebSudoManager webSudoManager;
    private final String postRedirectPath;

    public WebSudoAuthorisationInterceptor(WebSudoManager webSudoManager, String postRedirectPath) {
        this.webSudoManager = webSudoManager;
        this.postRedirectPath = postRedirectPath;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            return true;
        }
        catch (WebSudoSessionException wes) {
            if (request.getMethod().equals(RequestMethod.POST.toString())) {
                response.sendRedirect(request.getContextPath() + request.getServletPath() + this.postRedirectPath);
                return false;
            }
            this.webSudoManager.enforceWebSudoProtection(request, response);
            return false;
        }
    }
}

