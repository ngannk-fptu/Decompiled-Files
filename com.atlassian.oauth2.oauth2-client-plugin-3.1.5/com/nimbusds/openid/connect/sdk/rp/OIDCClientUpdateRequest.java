/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.client.ClientUpdateRequest;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class OIDCClientUpdateRequest
extends ClientUpdateRequest {
    public OIDCClientUpdateRequest(URI uri, ClientID id, BearerAccessToken accessToken, OIDCClientMetadata metadata, Secret secret) {
        super(uri, id, accessToken, metadata, secret);
    }

    public OIDCClientMetadata getOIDCClientMetadata() {
        return (OIDCClientMetadata)this.getClientMetadata();
    }

    public static OIDCClientUpdateRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.PUT);
        BearerAccessToken accessToken = BearerAccessToken.parse(httpRequest.getAuthorization());
        JSONObject jsonObject = httpRequest.getQueryAsJSONObject();
        ClientID id = new ClientID(JSONObjectUtils.getString(jsonObject, "client_id"));
        OIDCClientMetadata metadata = OIDCClientMetadata.parse(jsonObject);
        Secret clientSecret = null;
        if (jsonObject.get("client_secret") != null) {
            clientSecret = new Secret(JSONObjectUtils.getString(jsonObject, "client_secret"));
        }
        URI endpointURI = httpRequest.getURI();
        return new OIDCClientUpdateRequest(endpointURI, id, accessToken, metadata, clientSecret);
    }
}

