/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.rest.interceptor;

import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.net.ServiceExceptionHttpMapper;
import com.atlassian.applinks.internal.common.rest.util.RestErrorsFactory;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceExceptionInterceptor
implements ResourceInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ServiceExceptionInterceptor.class);

    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        try {
            invocation.invoke();
        }
        catch (InvocationTargetException e) {
            ServiceException serviceException = ServiceExceptionInterceptor.findServiceCause(e);
            if (serviceException != null) {
                log.debug("ServiceException intercepted from a REST call to {}", (Object)invocation.getHttpContext().getRequest().getRequestUri(), (Object)e);
                invocation.getHttpContext().getResponse().setResponse(ServiceExceptionInterceptor.createResponse(serviceException));
            }
            throw e;
        }
    }

    private static ServiceException findServiceCause(InvocationTargetException e) {
        return ApplinkErrors.findCauseOfType(e, ServiceException.class);
    }

    private static Response createResponse(ServiceException serviceException) {
        Response.Status status = ServiceExceptionHttpMapper.getStatus(serviceException);
        return Response.status((Response.Status)status).entity((Object)RestErrorsFactory.fromException(status, serviceException)).build();
    }
}

