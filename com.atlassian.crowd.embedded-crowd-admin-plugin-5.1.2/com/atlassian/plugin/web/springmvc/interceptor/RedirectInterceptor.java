/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.PluginHttpRequestWrapper
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.servlet.ModelAndView
 *  org.springframework.web.servlet.View
 *  org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 *  org.springframework.web.servlet.view.RedirectView
 */
package com.atlassian.plugin.web.springmvc.interceptor;

import com.atlassian.plugin.servlet.PluginHttpRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

public final class RedirectInterceptor
extends HandlerInterceptorAdapter {
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        View view;
        if (modelAndView != null && (view = modelAndView.getView()) instanceof RedirectView && request instanceof PluginHttpRequestWrapper) {
            String rootPath = request.getServletPath();
            RedirectView v = (RedirectView)view;
            v.setUrl(rootPath + v.getUrl());
            v.setContextRelative(true);
        }
    }
}

