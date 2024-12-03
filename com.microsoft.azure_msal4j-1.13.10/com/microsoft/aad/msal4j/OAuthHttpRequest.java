/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.ParseException
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest$Method
 *  com.nimbusds.oauth2.sdk.http.HTTPResponse
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.HTTPContentType;
import com.microsoft.aad.msal4j.HttpHelper;
import com.microsoft.aad.msal4j.HttpMethod;
import com.microsoft.aad.msal4j.HttpRequest;
import com.microsoft.aad.msal4j.HttpUtils;
import com.microsoft.aad.msal4j.IHttpResponse;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.ServiceBundle;
import com.microsoft.aad.msal4j.StringHelper;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OAuthHttpRequest
extends HTTPRequest {
    private final Map<String, String> extraHeaderParams;
    private final ServiceBundle serviceBundle;
    private final RequestContext requestContext;

    OAuthHttpRequest(HTTPRequest.Method method, URL url, Map<String, String> extraHeaderParams, RequestContext requestContext, ServiceBundle serviceBundle) {
        super(method, url);
        this.extraHeaderParams = extraHeaderParams;
        this.requestContext = requestContext;
        this.serviceBundle = serviceBundle;
    }

    public HTTPResponse send() throws IOException {
        Map<String, String> httpHeaders = this.configureHttpHeaders();
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, this.getURL().toString(), httpHeaders, this.getQuery());
        IHttpResponse httpResponse = HttpHelper.executeHttpRequest(httpRequest, this.requestContext, this.serviceBundle);
        return this.createOauthHttpResponseFromHttpResponse(httpResponse);
    }

    private Map<String, String> configureHttpHeaders() {
        HashMap<String, String> httpHeaders = new HashMap<String, String>(this.extraHeaderParams);
        httpHeaders.put("Content-Type", HTTPContentType.ApplicationURLEncoded.contentType);
        if (this.getAuthorization() != null) {
            httpHeaders.put("Authorization", this.getAuthorization());
        }
        Map<String, String> telemetryHeaders = this.serviceBundle.getServerSideTelemetry().getServerTelemetryHeaderMap();
        httpHeaders.putAll(telemetryHeaders);
        return httpHeaders;
    }

    private HTTPResponse createOauthHttpResponseFromHttpResponse(IHttpResponse httpResponse) throws IOException {
        HTTPResponse response = new HTTPResponse(httpResponse.statusCode());
        String location = HttpUtils.headerValue(httpResponse.headers(), "Location");
        if (!StringHelper.isBlank(location)) {
            try {
                response.setLocation(new URI(location));
            }
            catch (URISyntaxException e) {
                throw new IOException("Invalid location URI " + location, e);
            }
        }
        try {
            String contentType = HttpUtils.headerValue(httpResponse.headers(), "Content-Type");
            if (!StringHelper.isBlank(contentType)) {
                response.setContentType(contentType);
            }
        }
        catch (ParseException e) {
            throw new IOException("Couldn't parse Content-Type header: " + e.getMessage(), e);
        }
        Map<String, List<String>> headers = httpResponse.headers();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            String headerValue;
            if (StringHelper.isBlank(header.getKey()) || (headerValue = response.getHeaderValue(header.getKey())) != null && !StringHelper.isBlank(headerValue)) continue;
            response.setHeader(header.getKey(), header.getValue().toArray(new String[0]));
        }
        if (!StringHelper.isBlank(httpResponse.body())) {
            response.setContent(httpResponse.body());
        }
        return response;
    }

    Map<String, String> getExtraHeaderParams() {
        return this.extraHeaderParams;
    }
}

