/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationRequest;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class OIDCClientRegistrationRequest
extends ClientRegistrationRequest {
    public OIDCClientRegistrationRequest(URI uri, OIDCClientMetadata metadata, BearerAccessToken accessToken) {
        super(uri, metadata, accessToken);
    }

    public OIDCClientRegistrationRequest(URI uri, OIDCClientMetadata metadata, SignedJWT softwareStatement, BearerAccessToken accessToken) {
        super(uri, metadata, softwareStatement, accessToken);
    }

    public OIDCClientMetadata getOIDCClientMetadata() {
        return (OIDCClientMetadata)this.getClientMetadata();
    }

    public static OIDCClientRegistrationRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        JSONObject jsonObject = httpRequest.getQueryAsJSONObject();
        SignedJWT stmt = null;
        if (jsonObject.containsKey("software_statement")) {
            try {
                stmt = SignedJWT.parse(JSONObjectUtils.getString(jsonObject, "software_statement"));
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid software statement JWT: " + e.getMessage());
            }
            jsonObject.remove("software_statement");
        }
        OIDCClientMetadata metadata = OIDCClientMetadata.parse(jsonObject);
        BearerAccessToken accessToken = null;
        String authzHeaderValue = httpRequest.getAuthorization();
        if (StringUtils.isNotBlank(authzHeaderValue)) {
            accessToken = BearerAccessToken.parse(authzHeaderValue);
        }
        URI endpointURI = httpRequest.getURI();
        try {
            return new OIDCClientRegistrationRequest(endpointURI, metadata, stmt, accessToken);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

