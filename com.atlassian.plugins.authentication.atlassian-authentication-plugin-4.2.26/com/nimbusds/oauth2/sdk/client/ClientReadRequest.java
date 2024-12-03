/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import java.net.URI;
import java.net.URISyntaxException;
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
        URI endpointURI;
        httpRequest.ensureMethod(HTTPRequest.Method.GET);
        BearerAccessToken accessToken = BearerAccessToken.parse(httpRequest.getAuthorization());
        try {
            endpointURI = httpRequest.getURL().toURI();
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), e);
        }
        return new ClientReadRequest(endpointURI, accessToken);
    }
}

