/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.util.URLUtils
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeAuthorizationGrant;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.HttpHelper;
import com.microsoft.aad.msal4j.HttpMethod;
import com.microsoft.aad.msal4j.HttpRequest;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IHttpResponse;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.MsalServiceExceptionFactory;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.ServiceBundle;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

class DeviceCodeFlowRequest
extends MsalRequest {
    private AtomicReference<CompletableFuture<IAuthenticationResult>> futureReference;
    private DeviceCodeFlowParameters parameters;
    private String scopesStr;

    DeviceCodeFlowRequest(DeviceCodeFlowParameters parameters, AtomicReference<CompletableFuture<IAuthenticationResult>> futureReference, PublicClientApplication application, RequestContext requestContext) {
        super(application, null, requestContext);
        this.parameters = parameters;
        this.scopesStr = String.join((CharSequence)" ", parameters.scopes());
        this.futureReference = futureReference;
    }

    DeviceCode acquireDeviceCode(String url, String clientId, Map<String, String> clientDataHeaders, ServiceBundle serviceBundle) {
        String bodyParams;
        Map<String, String> headers = this.appendToHeaders(clientDataHeaders);
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, url, headers, bodyParams = this.createQueryParams(clientId));
        IHttpResponse response = HttpHelper.executeHttpRequest(httpRequest, this.requestContext(), serviceBundle);
        if (response.statusCode() != 200) {
            throw MsalServiceExceptionFactory.fromHttpResponse(response);
        }
        return this.parseJsonToDeviceCodeAndSetParameters(response.body(), headers, clientId);
    }

    void createAuthenticationGrant(DeviceCode deviceCode) {
        this.msalAuthorizationGrant = new DeviceCodeAuthorizationGrant(deviceCode, deviceCode.scopes(), this.parameters.claims());
    }

    private String createQueryParams(String clientId) {
        HashMap<String, List<String>> queryParameters = new HashMap<String, List<String>>();
        queryParameters.put("client_id", Collections.singletonList(clientId));
        String scopesParam = "openid profile offline_access " + this.scopesStr;
        queryParameters.put("scope", Collections.singletonList(scopesParam));
        return URLUtils.serializeParameters(queryParameters);
    }

    private Map<String, String> appendToHeaders(Map<String, String> clientDataHeaders) {
        HashMap<String, String> headers = new HashMap<String, String>(clientDataHeaders);
        headers.put("Accept", "application/json");
        return headers;
    }

    private DeviceCode parseJsonToDeviceCodeAndSetParameters(String json, Map<String, String> headers, String clientId) {
        DeviceCode result = JsonHelper.convertJsonToObject(json, DeviceCode.class);
        String correlationIdHeader = headers.get("client-request-id");
        if (correlationIdHeader != null) {
            result.correlationId(correlationIdHeader);
        }
        result.clientId(clientId);
        result.scopes(this.scopesStr);
        return result;
    }

    AtomicReference<CompletableFuture<IAuthenticationResult>> futureReference() {
        return this.futureReference;
    }

    DeviceCodeFlowParameters parameters() {
        return this.parameters;
    }

    String scopesStr() {
        return this.scopesStr;
    }
}

