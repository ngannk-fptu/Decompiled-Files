/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.client.ClientMetadata;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class ClientUpdateRequest
extends ProtectedResourceRequest {
    private final ClientID id;
    private final ClientMetadata metadata;
    private final Secret secret;

    public ClientUpdateRequest(URI uri, ClientID id, BearerAccessToken accessToken, ClientMetadata metadata, Secret secret) {
        super(uri, accessToken);
        if (id == null) {
            throw new IllegalArgumentException("The client identifier must not be null");
        }
        this.id = id;
        if (metadata == null) {
            throw new IllegalArgumentException("The client metadata must not be null");
        }
        this.metadata = metadata;
        this.secret = secret;
    }

    public ClientID getClientID() {
        return this.id;
    }

    public ClientMetadata getClientMetadata() {
        return this.metadata;
    }

    public Secret getClientSecret() {
        return this.secret;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.PUT, this.getEndpointURI());
        httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
        httpRequest.setEntityContentType(ContentType.APPLICATION_JSON);
        JSONObject jsonObject = this.metadata.toJSONObject();
        jsonObject.put("client_id", this.id.getValue());
        if (this.secret != null) {
            jsonObject.put("client_secret", this.secret.getValue());
        }
        httpRequest.setQuery(jsonObject.toString());
        return httpRequest;
    }

    public static ClientUpdateRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.PUT);
        BearerAccessToken accessToken = BearerAccessToken.parse(httpRequest.getAuthorization());
        JSONObject jsonObject = httpRequest.getQueryAsJSONObject();
        ClientID id = new ClientID(JSONObjectUtils.getString(jsonObject, "client_id"));
        ClientMetadata metadata = ClientMetadata.parse(jsonObject);
        Secret clientSecret = null;
        if (jsonObject.get("client_secret") != null) {
            clientSecret = new Secret(JSONObjectUtils.getString(jsonObject, "client_secret"));
        }
        return new ClientUpdateRequest(httpRequest.getURI(), id, accessToken, metadata, clientSecret);
    }
}

