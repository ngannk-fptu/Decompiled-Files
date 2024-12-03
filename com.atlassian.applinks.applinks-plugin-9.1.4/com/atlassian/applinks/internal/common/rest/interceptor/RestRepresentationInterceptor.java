/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.common.rest.interceptor;

import com.atlassian.applinks.internal.common.rest.util.RestResponses;
import com.atlassian.applinks.internal.rest.model.IllegalRestRepresentationStateException;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestRepresentationInterceptor
implements ResourceInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RestRepresentationInterceptor.class);
    private final I18nResolver i18nResolver;

    public RestRepresentationInterceptor(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        try {
            invocation.invoke();
        }
        catch (InvocationTargetException e) {
            IllegalRestRepresentationStateException restException = RestRepresentationInterceptor.findRestConversionError(e);
            if (restException != null) {
                invocation.getHttpContext().getResponse().setResponse(this.createResponse(restException));
            }
            throw e;
        }
    }

    private static IllegalRestRepresentationStateException findRestConversionError(InvocationTargetException e) {
        Throwable cause;
        for (cause = e.getCause(); cause != null && !IllegalRestRepresentationStateException.class.isInstance(cause); cause = cause.getCause()) {
        }
        return (IllegalRestRepresentationStateException)cause;
    }

    private Response createResponse(IllegalRestRepresentationStateException e) {
        log.warn("Converting REST representation into a domain object failed with IllegalRestRepresentationStateException: {}:{}", (Object)e.getContext(), (Object)e.getMessage());
        log.debug("Stack trace for IllegalRestRepresentationStateException with context {}", (Object)e.getContext(), (Object)e);
        return RestResponses.badRequest(e.getContext(), this.i18nResolver.getText("applinks.rest.invalidrepresentation", new Serializable[]{e.getContext()}));
    }
}

