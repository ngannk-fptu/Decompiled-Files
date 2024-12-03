/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.web.method.HandlerMethod
 */
package org.springframework.web.servlet.mvc.method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

public abstract class AbstractHandlerMethodAdapter
extends WebContentGenerator
implements HandlerAdapter,
Ordered {
    private int order = Integer.MAX_VALUE;

    public AbstractHandlerMethodAdapter() {
        super(false);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    @Override
    public final boolean supports(Object handler) {
        return handler instanceof HandlerMethod && this.supportsInternal((HandlerMethod)handler);
    }

    protected abstract boolean supportsInternal(HandlerMethod var1);

    @Override
    @Nullable
    public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.handleInternal(request, response, (HandlerMethod)handler);
    }

    @Nullable
    protected abstract ModelAndView handleInternal(HttpServletRequest var1, HttpServletResponse var2, HandlerMethod var3) throws Exception;

    @Override
    public final long getLastModified(HttpServletRequest request, Object handler) {
        return this.getLastModifiedInternal(request, (HandlerMethod)handler);
    }

    @Deprecated
    protected abstract long getLastModifiedInternal(HttpServletRequest var1, HandlerMethod var2);
}

