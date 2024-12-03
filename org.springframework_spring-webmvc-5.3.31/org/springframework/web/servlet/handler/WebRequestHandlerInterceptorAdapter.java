/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.context.request.AsyncWebRequestInterceptor
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.WebRequestInterceptor
 */
package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

public class WebRequestHandlerInterceptorAdapter
implements AsyncHandlerInterceptor {
    private final WebRequestInterceptor requestInterceptor;

    public WebRequestHandlerInterceptorAdapter(WebRequestInterceptor requestInterceptor) {
        Assert.notNull((Object)requestInterceptor, (String)"WebRequestInterceptor must not be null");
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.requestInterceptor.preHandle((WebRequest)new DispatcherServletWebRequest(request, response));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        this.requestInterceptor.postHandle((WebRequest)new DispatcherServletWebRequest(request, response), modelAndView != null && !modelAndView.wasCleared() ? modelAndView.getModelMap() : null);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        this.requestInterceptor.afterCompletion((WebRequest)new DispatcherServletWebRequest(request, response), ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (this.requestInterceptor instanceof AsyncWebRequestInterceptor) {
            AsyncWebRequestInterceptor asyncInterceptor = (AsyncWebRequestInterceptor)this.requestInterceptor;
            DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(request, response);
            asyncInterceptor.afterConcurrentHandlingStarted((WebRequest)webRequest);
        }
    }
}

