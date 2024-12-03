/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.servlet.ModelAndView
 *  org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 *  org.springframework.web.servlet.view.RedirectView
 */
package com.atlassian.plugin.web.springmvc.interceptor;

import com.atlassian.plugin.web.springmvc.xsrf.XsrfTokenGenerator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

public class XsrfTokenInterceptor
extends HandlerInterceptorAdapter {
    private ApplicationProperties applicationProperties;
    private XsrfTokenGenerator xsrfTokenGenerator;
    private String redirectPath = "";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("atl_token");
        if ("POST".equals(request.getMethod()) && !this.xsrfTokenGenerator.validateToken(request, token)) {
            response.sendRedirect(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + request.getServletPath() + this.redirectPath);
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && !this.isRedirect(modelAndView)) {
            modelAndView.getModel().put("xsrfTokenName", this.xsrfTokenGenerator.getXsrfTokenName());
            modelAndView.getModel().put("xsrfTokenValue", this.xsrfTokenGenerator.generateToken(request));
        }
    }

    private boolean isRedirect(ModelAndView modelAndView) {
        return modelAndView.getViewName() != null && modelAndView.getViewName().startsWith("redirect:/") || modelAndView.getView() instanceof RedirectView;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public void setXsrfTokenGenerator(XsrfTokenGenerator xsrfTokenGenerator) {
        this.xsrfTokenGenerator = xsrfTokenGenerator;
    }
}

