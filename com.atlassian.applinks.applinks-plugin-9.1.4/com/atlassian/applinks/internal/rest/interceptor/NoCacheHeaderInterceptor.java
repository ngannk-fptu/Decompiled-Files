/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.internal.rest.interceptor;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.core.Response;

public class NoCacheHeaderInterceptor
implements ResourceInterceptor {
    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        methodInvocation.invoke();
        Response response = methodInvocation.getHttpContext().getResponse().getResponse();
        Response noCacheResponse = Response.fromResponse((Response)response).header("Cache-Control", (Object)"no-cache").build();
        methodInvocation.getHttpContext().getResponse().setResponse(noCacheResponse);
    }
}

