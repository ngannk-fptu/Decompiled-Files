/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest.Interceptors;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamCalResourceLoggingInterceptor
implements ResourceInterceptor {
    private static Logger LOGGER = LoggerFactory.getLogger(TeamCalResourceLoggingInterceptor.class);

    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        String httpMethod = methodInvocation.getMethod().getHttpMethod();
        String methodName = methodInvocation.getMethod().toString();
        LOGGER.info("Invoking rest endpoint [{}] via http method [{}]", (Object)methodName, (Object)httpMethod);
        methodInvocation.invoke();
    }
}

