/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ProtectedResourceRequest;
import com.nimbusds.oauth2.sdk.ciba.AuthRequestID;
import com.nimbusds.oauth2.sdk.ciba.CIBAErrorDelivery;
import com.nimbusds.oauth2.sdk.ciba.CIBATokenDelivery;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import java.net.URI;
import net.minidev.json.JSONObject;

public abstract class CIBAPushCallback
extends ProtectedResourceRequest {
    private final AuthRequestID authRequestID;

    public CIBAPushCallback(URI endpoint, BearerAccessToken accessToken, AuthRequestID authRequestID) {
        super(endpoint, accessToken);
        if (authRequestID == null) {
            throw new IllegalArgumentException("The auth_req_id must not be null");
        }
        this.authRequestID = authRequestID;
    }

    public abstract boolean indicatesSuccess();

    public AuthRequestID getAuthRequestID() {
        return this.authRequestID;
    }

    public CIBATokenDelivery toTokenDelivery() {
        return (CIBATokenDelivery)this;
    }

    public CIBAErrorDelivery toErrorDelivery() {
        return (CIBAErrorDelivery)this;
    }

    public static CIBAPushCallback parse(HTTPRequest httpRequest) throws ParseException {
        JSONObject jsonObject = httpRequest.getQueryAsJSONObject();
        if (jsonObject.containsKey((Object)"error")) {
            return CIBAErrorDelivery.parse(httpRequest);
        }
        return CIBATokenDelivery.parse(httpRequest);
    }
}

