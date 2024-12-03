/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.ciba.AuthRequestID;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class CIBAPingCallback
extends ProtectedResourceRequest {
    private final AuthRequestID authRequestID;

    public CIBAPingCallback(URI endpoint, BearerAccessToken accessToken, AuthRequestID authRequestID) {
        super(endpoint, accessToken);
        if (authRequestID == null) {
            throw new IllegalArgumentException("The auth_req_id must not be null");
        }
        this.authRequestID = authRequestID;
    }

    public AuthRequestID getAuthRequestID() {
        return this.authRequestID;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setFollowRedirects(false);
        httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
        httpRequest.setEntityContentType(ContentType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put((Object)"auth_req_id", (Object)this.getAuthRequestID().getValue());
        httpRequest.setQuery(jsonObject.toJSONString());
        return httpRequest;
    }

    public static CIBAPingCallback parse(HTTPRequest httpRequest) throws ParseException {
        URI uri = httpRequest.getURI();
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_JSON);
        BearerAccessToken accessToken = BearerAccessToken.parse(httpRequest);
        AuthRequestID authRequestID = new AuthRequestID(JSONObjectUtils.getString(httpRequest.getQueryAsJSONObject(), "auth_req_id"));
        return new CIBAPingCallback(uri, accessToken, authRequestID);
    }
}

