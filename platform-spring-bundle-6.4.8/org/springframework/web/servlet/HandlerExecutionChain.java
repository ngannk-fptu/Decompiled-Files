/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class HandlerExecutionChain {
    private static final Log logger = LogFactory.getLog(HandlerExecutionChain.class);
    private final Object handler;
    private final List<HandlerInterceptor> interceptorList = new ArrayList<HandlerInterceptor>();
    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler) {
        this(handler, (HandlerInterceptor[])null);
    }

    public HandlerExecutionChain(Object handler, HandlerInterceptor ... interceptors) {
        this(handler, interceptors != null ? Arrays.asList(interceptors) : Collections.emptyList());
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList) {
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain originalChain = (HandlerExecutionChain)handler;
            this.handler = originalChain.getHandler();
            this.interceptorList.addAll(originalChain.interceptorList);
        } else {
            this.handler = handler;
        }
        this.interceptorList.addAll(interceptorList);
    }

    public Object getHandler() {
        return this.handler;
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    public void addInterceptor(int index, HandlerInterceptor interceptor) {
        this.interceptorList.add(index, interceptor);
    }

    public void addInterceptors(HandlerInterceptor ... interceptors) {
        CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
    }

    @Nullable
    public HandlerInterceptor[] getInterceptors() {
        return !this.interceptorList.isEmpty() ? this.interceptorList.toArray(new HandlerInterceptor[0]) : null;
    }

    public List<HandlerInterceptor> getInterceptorList() {
        return !this.interceptorList.isEmpty() ? Collections.unmodifiableList(this.interceptorList) : Collections.emptyList();
    }

    boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int i2 = 0;
        while (i2 < this.interceptorList.size()) {
            HandlerInterceptor interceptor = this.interceptorList.get(i2);
            if (!interceptor.preHandle(request, response, this.handler)) {
                this.triggerAfterCompletion(request, response, null);
                return false;
            }
            this.interceptorIndex = i2++;
        }
        return true;
    }

    void applyPostHandle(HttpServletRequest request, HttpServletResponse response, @Nullable ModelAndView mv) throws Exception {
        for (int i2 = this.interceptorList.size() - 1; i2 >= 0; --i2) {
            HandlerInterceptor interceptor = this.interceptorList.get(i2);
            interceptor.postHandle(request, response, this.handler, mv);
        }
    }

    void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, @Nullable Exception ex) {
        for (int i2 = this.interceptorIndex; i2 >= 0; --i2) {
            HandlerInterceptor interceptor = this.interceptorList.get(i2);
            try {
                interceptor.afterCompletion(request, response, this.handler, ex);
                continue;
            }
            catch (Throwable ex2) {
                logger.error((Object)"HandlerInterceptor.afterCompletion threw exception", ex2);
            }
        }
    }

    void applyAfterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response) {
        for (int i2 = this.interceptorList.size() - 1; i2 >= 0; --i2) {
            HandlerInterceptor interceptor = this.interceptorList.get(i2);
            if (!(interceptor instanceof AsyncHandlerInterceptor)) continue;
            try {
                AsyncHandlerInterceptor asyncInterceptor = (AsyncHandlerInterceptor)interceptor;
                asyncInterceptor.afterConcurrentHandlingStarted(request, response, this.handler);
                continue;
            }
            catch (Throwable ex) {
                if (!logger.isErrorEnabled()) continue;
                logger.error((Object)("Interceptor [" + interceptor + "] failed in afterConcurrentHandlingStarted"), ex);
            }
        }
    }

    public String toString() {
        return "HandlerExecutionChain with [" + this.getHandler() + "] and " + this.interceptorList.size() + " interceptors";
    }
}

