/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

public abstract class AbstractHandlerMethodExceptionResolver
extends AbstractHandlerExceptionResolver {
    @Override
    protected boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (handler == null) {
            return super.shouldApplyTo(request, null);
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            handler = handlerMethod.getBean();
            return super.shouldApplyTo(request, handler);
        }
        if (this.hasGlobalExceptionHandlers() && this.hasHandlerMappings()) {
            return super.shouldApplyTo(request, handler);
        }
        return false;
    }

    protected boolean hasGlobalExceptionHandlers() {
        return false;
    }

    @Override
    @Nullable
    protected final ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        HandlerMethod handlerMethod = handler instanceof HandlerMethod ? (HandlerMethod)handler : null;
        return this.doResolveHandlerMethodException(request, response, handlerMethod, ex);
    }

    @Nullable
    protected abstract ModelAndView doResolveHandlerMethodException(HttpServletRequest var1, HttpServletResponse var2, @Nullable HandlerMethod var3, Exception var4);
}

