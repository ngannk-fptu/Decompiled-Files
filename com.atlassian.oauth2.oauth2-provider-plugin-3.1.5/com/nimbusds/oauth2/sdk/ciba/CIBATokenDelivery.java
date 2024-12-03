/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ciba.AuthRequestID;
import com.nimbusds.oauth2.sdk.ciba.CIBAPushCallback;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class CIBATokenDelivery
extends CIBAPushCallback {
    private final Tokens tokens;

    public CIBATokenDelivery(URI endpoint, BearerAccessToken accessToken, AuthRequestID authRequestID, Tokens tokens) {
        super(endpoint, accessToken, authRequestID);
        if (tokens == null) {
            throw new IllegalArgumentException("The tokens must not be null");
        }
        this.tokens = tokens;
    }

    public CIBATokenDelivery(URI endpoint, BearerAccessToken accessToken, AuthRequestID authRequestID, OIDCTokens oidcTokens) {
        super(endpoint, accessToken, authRequestID);
        if (oidcTokens == null) {
            throw new IllegalArgumentException("The OpenID Connect tokens must not be null");
        }
        this.tokens = oidcTokens;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public Tokens getTokens() {
        return this.tokens;
    }

    public OIDCTokens getOIDCTokens() {
        return this.getTokens() instanceof OIDCTokens ? this.getTokens().toOIDCTokens() : null;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
        httpRequest.setEntityContentType(ContentType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("auth_req_id", this.getAuthRequestID().getValue());
        jsonObject.putAll(this.getTokens().toJSONObject());
        httpRequest.setQuery(jsonObject.toJSONString());
        return httpRequest;
    }

    public static CIBATokenDelivery parse(HTTPRequest httpRequest) throws ParseException {
        URI uri = httpRequest.getURI();
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_JSON);
        BearerAccessToken clientNotificationToken = BearerAccessToken.parse(httpRequest);
        AuthRequestID authRequestID = new AuthRequestID(JSONObjectUtils.getString(httpRequest.getQueryAsJSONObject(), "auth_req_id"));
        JSONObject jsonObject = httpRequest.getQueryAsJSONObject();
        if (jsonObject.get("id_token") != null) {
            return new CIBATokenDelivery(uri, clientNotificationToken, authRequestID, OIDCTokens.parse(jsonObject));
        }
        return new CIBATokenDelivery(uri, clientNotificationToken, authRequestID, Tokens.parse(jsonObject));
    }
}

