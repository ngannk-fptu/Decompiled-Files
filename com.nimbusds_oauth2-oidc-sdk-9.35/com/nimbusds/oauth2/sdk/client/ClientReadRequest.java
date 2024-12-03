/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public class ClientReadRequest
extends ProtectedResourceRequest {
    public ClientReadRequest(URI uri, BearerAccessToken accessToken) {
        super(uri, accessToken);
        if (accessToken == null) {
            throw new IllegalArgumentException("The access token must not be null");
        }
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, this.getEndpointURI());
        httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
        return httpRequest;
    }

    public static ClientReadRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.GET);
        return new ClientReadRequest(httpRequest.getURI(), BearerAccessToken.parse(httpRequest.getAuthorization()));
    }
}

