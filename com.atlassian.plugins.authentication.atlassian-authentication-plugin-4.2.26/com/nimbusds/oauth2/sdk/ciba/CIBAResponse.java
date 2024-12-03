/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.ciba.CIBAErrorResponse;
import com.nimbusds.oauth2.sdk.ciba.CIBARequestAcknowledgement;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.minidev.json.JSONObject;

public abstract class CIBAResponse
implements Response {
    public CIBARequestAcknowledgement toRequestAcknowledgement() {
        return (CIBARequestAcknowledgement)this;
    }

    public CIBAErrorResponse toErrorResponse() {
        return (CIBAErrorResponse)this;
    }

    public static CIBAResponse parse(JSONObject jsonObject) throws ParseException {
        if (jsonObject.containsKey("auth_req_id")) {
            return CIBARequestAcknowledgement.parse(jsonObject);
        }
        return CIBAErrorResponse.parse(jsonObject);
    }

    public static CIBAResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return CIBARequestAcknowledgement.parse(httpResponse);
        }
        return CIBAErrorResponse.parse(httpResponse);
    }
}

