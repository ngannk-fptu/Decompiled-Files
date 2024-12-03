/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.async.WebAsyncTask
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.method.support.HandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AsyncTaskMethodReturnValueHandler
implements HandlerMethodReturnValueHandler {
    @Nullable
    private final BeanFactory beanFactory;

    public AsyncTaskMethodReturnValueHandler(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        return WebAsyncTask.class.isAssignableFrom(returnType.getParameterType());
    }

    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }
        WebAsyncTask webAsyncTask = (WebAsyncTask)returnValue;
        if (this.beanFactory != null) {
            webAsyncTask.setBeanFactory(this.beanFactory);
        }
        WebAsyncUtils.getAsyncManager((WebRequest)webRequest).startCallableProcessing(webAsyncTask, new Object[]{mavContainer});
    }
}

