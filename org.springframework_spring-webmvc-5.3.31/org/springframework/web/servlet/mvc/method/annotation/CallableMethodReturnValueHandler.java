/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.util.concurrent.Callable;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CallableMethodReturnValueHandler
implements HandlerMethodReturnValueHandler {
    public boolean supportsReturnType(MethodParameter returnType) {
        return Callable.class.isAssignableFrom(returnType.getParameterType());
    }

    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        Callable callable = (Callable)returnValue;
        WebAsyncUtils.getAsyncManager((WebRequest)webRequest).startCallableProcessing(callable, new Object[]{mavContainer});
    }
}

