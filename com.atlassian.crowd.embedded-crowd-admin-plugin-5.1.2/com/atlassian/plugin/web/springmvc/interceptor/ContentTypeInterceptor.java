/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.web.servlet.ModelAndView
 *  org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 */
package com.atlassian.plugin.web.springmvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public final class ContentTypeInterceptor
extends HandlerInterceptorAdapter {
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        String existingType = response.getContentType();
        if (existingType == null) {
            response.setContentType("text/html; charset=UTF-8");
        } else {
            response.setContentType(existingType);
        }
    }
}

