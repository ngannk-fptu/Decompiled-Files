/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RequestObjectPOSTResponse;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.util.Date;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Deprecated
@Immutable
public final class RequestObjectPOSTSuccessResponse
extends RequestObjectPOSTResponse
implements SuccessResponse {
    private final Issuer iss;
    private final Audience aud;
    private final URI requestURI;
    private final Date exp;

    public RequestObjectPOSTSuccessResponse(Issuer iss, Audience aud, URI requestURI, Date exp) {
        if (iss == null) {
            throw new IllegalArgumentException("The issuer must not be null");
        }
        this.iss = iss;
        if (aud == null) {
            throw new IllegalArgumentException("The audience must not be null");
        }
        this.aud = aud;
        if (requestURI == null) {
            throw new IllegalArgumentException("The request URI must not be null");
        }
        this.requestURI = requestURI;
        if (exp == null) {
            throw new IllegalArgumentException("The request URI expiration time must not be null");
        }
        this.exp = exp;
    }

    public Issuer getIssuer() {
        return this.iss;
    }

    public Audience getAudience() {
        return this.aud;
    }

    public URI getRequestURI() {
        return this.requestURI;
    }

    public Date getExpirationTime() {
        return this.exp;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iss", this.iss.getValue());
        jsonObject.put("aud", this.aud.getValue());
        jsonObject.put("request_uri", this.requestURI.toString());
        jsonObject.put("exp", DateUtils.toSecondsSinceEpoch(this.exp));
        return jsonObject;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(201);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setContent(this.toJSONObject().toJSONString());
        return httpResponse;
    }

    public static RequestObjectPOSTSuccessResponse parse(JSONObject jsonObject) throws ParseException {
        return new RequestObjectPOSTSuccessResponse(new Issuer(JSONObjectUtils.getString(jsonObject, "iss")), new Audience(JSONObjectUtils.getString(jsonObject, "aud")), JSONObjectUtils.getURI(jsonObject, "request_uri"), DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(jsonObject, "exp")));
    }

    public static RequestObjectPOSTSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(201, 200);
        return RequestObjectPOSTSuccessResponse.parse(httpResponse.getContentAsJSONObject());
    }
}

