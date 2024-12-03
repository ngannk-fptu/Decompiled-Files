/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.core.auth.AbstractApplicationLinkRequest
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.Request$HttpMethod
 *  com.atlassian.oauth.Request$Parameter
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.google.common.collect.ImmutableMap
 *  net.oauth.OAuthMessage
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.core.auth.AbstractApplicationLinkRequest;
import com.atlassian.applinks.oauth.auth.OAuthApplinksResponseHandler;
import com.atlassian.applinks.oauth.auth.OAuthApplinksReturningResponseHandler;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.oauth.auth.OAuthResponseHandler;
import com.atlassian.oauth.Request;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.oauth.OAuthMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OAuthRequest
extends AbstractApplicationLinkRequest {
    private static final Logger log = LoggerFactory.getLogger(OAuthRequest.class);
    private static final Map<Request.MethodType, Request.HttpMethod> METHOD_TYPE_MAP = ImmutableMap.builder().put((Object)Request.MethodType.GET, (Object)Request.HttpMethod.GET).put((Object)Request.MethodType.DELETE, (Object)Request.HttpMethod.DELETE).put((Object)Request.MethodType.POST, (Object)Request.HttpMethod.POST).put((Object)Request.MethodType.PUT, (Object)Request.HttpMethod.PUT).put((Object)Request.MethodType.HEAD, (Object)Request.HttpMethod.HEAD).put((Object)Request.MethodType.OPTIONS, (Object)Request.HttpMethod.OPTIONS).put((Object)Request.MethodType.TRACE, (Object)Request.HttpMethod.TRACE).build();
    protected final Request.MethodType methodType;
    protected final ApplicationId applicationId;
    protected final ServiceProvider serviceProvider;
    protected final ConsumerService consumerService;

    public OAuthRequest(String url, Request.MethodType methodType, Request wrappedRequest, ApplicationId applicationId, ServiceProvider serviceProvider, ConsumerService consumerService) {
        super(url, wrappedRequest);
        this.methodType = methodType;
        this.applicationId = applicationId;
        this.serviceProvider = serviceProvider;
        this.consumerService = consumerService;
    }

    public <R> R execute(ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) throws ResponseException {
        this.signRequest();
        return (R)this.wrappedRequest.execute(this.ensureOAuthApplinksResponseHandler(applicationLinkResponseHandler));
    }

    private <R> ApplicationLinkResponseHandler<R> ensureOAuthApplinksResponseHandler(ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) {
        if (applicationLinkResponseHandler instanceof OAuthApplinksResponseHandler) {
            return applicationLinkResponseHandler;
        }
        return new OAuthApplinksResponseHandler<R>(this.url, applicationLinkResponseHandler, this, this.applicationId, this.followRedirects);
    }

    public void execute(ResponseHandler responseHandler) throws ResponseException {
        this.signRequest();
        this.wrappedRequest.execute(this.ensureOAuthResponseHandler(responseHandler));
    }

    private ResponseHandler ensureOAuthResponseHandler(ResponseHandler responseHandler) {
        if (responseHandler instanceof OAuthResponseHandler) {
            return responseHandler;
        }
        return new OAuthResponseHandler(this.url, (ResponseHandler<Response>)responseHandler, this.wrappedRequest, this.applicationId, this.followRedirects);
    }

    public <RET> RET executeAndReturn(ReturningResponseHandler<? super Response, RET> responseHandler) throws ResponseException {
        this.signRequest();
        return (RET)this.wrappedRequest.executeAndReturn(this.ensureOAuthApplinksReturningResponseHandler(responseHandler));
    }

    private <R> ReturningResponseHandler<? super Response, R> ensureOAuthApplinksReturningResponseHandler(ReturningResponseHandler<? super Response, R> returningResponseHandler) {
        if (returningResponseHandler instanceof OAuthApplinksReturningResponseHandler) {
            return returningResponseHandler;
        }
        return new OAuthApplinksReturningResponseHandler<R>(this.url, returningResponseHandler, this.wrappedRequest, this.applicationId, this.followRedirects);
    }

    protected void signRequest() throws ResponseException {
        com.atlassian.oauth.Request oAuthRequest = this.createUnsignedRequest();
        com.atlassian.oauth.Request signedRequest = this.consumerService.sign(oAuthRequest, this.serviceProvider);
        OAuthMessage oAuthMessage = OAuthHelper.asOAuthMessage(signedRequest);
        try {
            this.wrappedRequest.setHeader("Authorization", oAuthMessage.getAuthorizationHeader(null));
        }
        catch (IOException e) {
            throw new ResponseException("Unable to generate OAuth Authorization request header.", (Throwable)e);
        }
    }

    protected abstract com.atlassian.oauth.Request createUnsignedRequest();

    protected List<Request.Parameter> toOAuthParameters(String accesstoken) {
        ArrayList<Request.Parameter> parameters = new ArrayList<Request.Parameter>();
        parameters.add(new Request.Parameter("oauth_token", accesstoken));
        for (String parameterName : this.parameters.keySet()) {
            List values = (List)this.parameters.get(parameterName);
            for (String value : values) {
                parameters.add(new Request.Parameter(parameterName, value));
            }
        }
        return parameters;
    }

    protected static Request.HttpMethod toOAuthMethodType(Request.MethodType methodType) {
        Request.HttpMethod method = METHOD_TYPE_MAP.get(methodType);
        if (method == null) {
            log.warn("Did not find matching OAuth method type for " + methodType + ", returning GET. This will likely lead to signature_invalid error");
            method = Request.HttpMethod.GET;
        }
        return method;
    }
}

