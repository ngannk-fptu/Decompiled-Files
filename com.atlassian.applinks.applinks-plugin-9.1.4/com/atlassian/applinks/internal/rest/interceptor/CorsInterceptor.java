/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.sun.jersey.api.core.HttpRequestContext
 *  com.sun.jersey.api.core.HttpResponseContext
 *  com.sun.jersey.spi.container.ContainerRequest
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.rest.interceptor;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.spi.container.ContainerRequest;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CorsInterceptor
implements ResourceInterceptor {
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ORIGIN = "Origin";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TRUE = String.valueOf(true);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        methodInvocation.invoke();
        HttpResponseContext response = methodInvocation.getHttpContext().getResponse();
        HttpRequestContext request = methodInvocation.getHttpContext().getRequest();
        Response corsAllowedResponse = this.addCorsHeaders(request, response);
        methodInvocation.getHttpContext().getResponse().setResponse(corsAllowedResponse);
    }

    private Response addCorsHeaders(HttpRequestContext request, HttpResponseContext response) {
        Response.ResponseBuilder newResponse = Response.fromResponse((Response)response.getResponse());
        String origin = request.getHeaderValue(ORIGIN);
        newResponse.header(ACCESS_CONTROL_ALLOW_ORIGIN, (Object)origin);
        this.logger.debug("CORS Header [{}] set to [{}]", (Object)ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)origin);
        newResponse.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)TRUE);
        this.logger.debug("CORS Header [{}] set to [{}]", (Object)ACCESS_CONTROL_ALLOW_CREDENTIALS, (Object)TRUE);
        if (this.isCorsPreflightRequest(request)) {
            newResponse.header(ACCESS_CONTROL_ALLOW_HEADERS, (Object)CONTENT_TYPE);
            this.logger.debug("CORS Preflight Header [{}] set to [{}]", (Object)ACCESS_CONTROL_ALLOW_HEADERS, (Object)CONTENT_TYPE);
            newResponse.header(ACCESS_CONTROL_ALLOW_METHODS, (Object)request.getMethod());
            this.logger.debug("CORS Preflight Header [{}] set to [{}]", (Object)ACCESS_CONTROL_ALLOW_METHODS, (Object)request.getMethod());
        }
        return newResponse.build();
    }

    private boolean isCorsPreflightRequest(HttpRequestContext requestContext) {
        if (requestContext instanceof ContainerRequest) {
            ContainerRequest request = (ContainerRequest)requestContext;
            Object preflightValue = request.getProperties().get("Cors-Preflight-Requested");
            return TRUE.equals(preflightValue);
        }
        return false;
    }
}

