/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class ClientRegistrationRequest
extends ProtectedResourceRequest {
    private final ClientMetadata metadata;
    private final SignedJWT softwareStatement;

    public ClientRegistrationRequest(URI uri, ClientMetadata metadata, BearerAccessToken accessToken) {
        this(uri, metadata, null, accessToken);
    }

    public ClientRegistrationRequest(URI uri, ClientMetadata metadata, SignedJWT softwareStatement, BearerAccessToken accessToken) {
        super(uri, accessToken);
        if (metadata == null) {
            throw new IllegalArgumentException("The client metadata must not be null");
        }
        this.metadata = metadata;
        if (softwareStatement != null) {
            JWTClaimsSet claimsSet;
            if (softwareStatement.getState() == JWSObject.State.UNSIGNED) {
                throw new IllegalArgumentException("The software statement JWT must be signed");
            }
            try {
                claimsSet = softwareStatement.getJWTClaimsSet();
            }
            catch (java.text.ParseException e) {
                throw new IllegalArgumentException("The software statement is not a valid signed JWT: " + e.getMessage());
            }
            if (claimsSet.getIssuer() == null) {
                throw new IllegalArgumentException("The software statement JWT must contain an 'iss' claim");
            }
        }
        this.softwareStatement = softwareStatement;
    }

    public ClientMetadata getClientMetadata() {
        return this.metadata;
    }

    public SignedJWT getSoftwareStatement() {
        return this.softwareStatement;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        if (this.getAccessToken() != null) {
            httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
        }
        httpRequest.setEntityContentType(ContentType.APPLICATION_JSON);
        JSONObject content = this.metadata.toJSONObject();
        if (this.softwareStatement != null) {
            content.put("software_statement", this.softwareStatement.serialize());
        }
        httpRequest.setQuery(content.toString());
        return httpRequest;
    }

    public static ClientRegistrationRequest parse(HTTPRequest httpRequest) throws ParseException {
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
        ClientMetadata metadata = ClientMetadata.parse(jsonObject);
        BearerAccessToken accessToken = null;
        String authzHeaderValue = httpRequest.getAuthorization();
        if (StringUtils.isNotBlank(authzHeaderValue)) {
            accessToken = BearerAccessToken.parse(authzHeaderValue);
        }
        try {
            return new ClientRegistrationRequest(httpRequest.getURI(), metadata, stmt, accessToken);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

