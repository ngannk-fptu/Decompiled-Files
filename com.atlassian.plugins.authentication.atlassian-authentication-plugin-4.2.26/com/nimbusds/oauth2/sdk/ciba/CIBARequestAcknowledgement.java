/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.ciba.AuthRequestID;
import com.nimbusds.oauth2.sdk.ciba.CIBAResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class CIBARequestAcknowledgement
extends CIBAResponse
implements SuccessResponse {
    public static final int DEFAULT_MIN_WAIT_INTERVAL = 5;
    private final AuthRequestID authRequestID;
    private final int expiresIn;
    private final Integer minWaitInterval;

    public CIBARequestAcknowledgement(AuthRequestID authRequestID, int expiresIn, Integer minWaitInterval) {
        if (authRequestID == null) {
            throw new IllegalArgumentException("The auth_req_id must not be null");
        }
        this.authRequestID = authRequestID;
        if (expiresIn < 1) {
            throw new IllegalArgumentException("The expiration must be a positive integer");
        }
        this.expiresIn = expiresIn;
        if (minWaitInterval != null && minWaitInterval < 1) {
            throw new IllegalArgumentException("The interval must be a positive integer");
        }
        this.minWaitInterval = minWaitInterval;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public AuthRequestID getAuthRequestID() {
        return this.authRequestID;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }

    public Integer getMinWaitInterval() {
        return this.minWaitInterval;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("auth_req_id", this.authRequestID);
        o.put("expires_in", this.expiresIn);
        if (this.minWaitInterval != null) {
            o.put("interval", this.minWaitInterval);
        }
        return o;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        httpResponse.setContent(this.toJSONObject().toString());
        return httpResponse;
    }

    public static CIBARequestAcknowledgement parse(JSONObject jsonObject) throws ParseException {
        AuthRequestID authRequestID = AuthRequestID.parse(JSONObjectUtils.getString(jsonObject, "auth_req_id"));
        int expiresIn = JSONObjectUtils.getInt(jsonObject, "expires_in");
        if (expiresIn < 1) {
            throw new ParseException("The expires_in parameter must be a positive integer");
        }
        Integer minWaitInterval = null;
        if (jsonObject.get("interval") != null) {
            minWaitInterval = JSONObjectUtils.getInt(jsonObject, "interval");
        }
        if (minWaitInterval != null && minWaitInterval < 1) {
            throw new ParseException("The interval parameter must be a positive integer");
        }
        return new CIBARequestAcknowledgement(authRequestID, expiresIn, minWaitInterval);
    }

    public static CIBARequestAcknowledgement parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        JSONObject jsonObject = httpResponse.getContentAsJSONObject();
        return CIBARequestAcknowledgement.parse(jsonObject);
    }
}

