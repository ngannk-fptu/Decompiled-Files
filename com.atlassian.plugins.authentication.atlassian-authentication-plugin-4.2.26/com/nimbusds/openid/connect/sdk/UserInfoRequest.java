/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import java.net.URI;
import java.net.URISyntaxException;
import net.jcip.annotations.Immutable;

@Immutable
public class UserInfoRequest
extends ProtectedResourceRequest {
    private final HTTPRequest.Method httpMethod;

    public UserInfoRequest(URI uri, BearerAccessToken accessToken) {
        this(uri, HTTPRequest.Method.GET, accessToken);
    }

    public UserInfoRequest(URI uri, HTTPRequest.Method httpMethod, BearerAccessToken accessToken) {
        super(uri, accessToken);
        if (httpMethod == null) {
            throw new IllegalArgumentException("The HTTP method must not be null");
        }
        this.httpMethod = httpMethod;
        if (accessToken == null) {
            throw new IllegalArgumentException("The access token must not be null");
        }
    }

    public HTTPRequest.Method getMethod() {
        return this.httpMethod;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(this.httpMethod, this.getEndpointURI());
        switch (this.httpMethod) {
            case GET: {
                httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
                break;
            }
            case POST: {
                httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
                httpRequest.setQuery("access_token=" + this.getAccessToken().getValue());
                break;
            }
            default: {
                throw new SerializeException("Unexpected HTTP method: " + (Object)((Object)this.httpMethod));
            }
        }
        return httpRequest;
    }

    public static UserInfoRequest parse(HTTPRequest httpRequest) throws ParseException {
        URI endpointURI;
        HTTPRequest.Method httpMethod = httpRequest.getMethod();
        BearerAccessToken accessToken = BearerAccessToken.parse(httpRequest);
        try {
            endpointURI = httpRequest.getURL().toURI();
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), e);
        }
        return new UserInfoRequest(endpointURI, httpMethod, accessToken);
    }
}

