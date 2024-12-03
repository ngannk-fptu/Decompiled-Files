/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ciba.AuthRequestID;
import com.nimbusds.oauth2.sdk.ciba.CIBAError;
import com.nimbusds.oauth2.sdk.ciba.CIBAPushCallback;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class CIBAErrorDelivery
extends CIBAPushCallback {
    private static final Set<ErrorObject> STANDARD_ERRORS;
    private final ErrorObject errorObject;

    public static Set<ErrorObject> getStandardErrors() {
        return STANDARD_ERRORS;
    }

    public CIBAErrorDelivery(URI endpoint, BearerAccessToken accessToken, AuthRequestID authRequestID, ErrorObject errorObject) {
        super(endpoint, accessToken, authRequestID);
        if (endpoint == null) {
            throw new IllegalArgumentException("The error object must not be null");
        }
        this.errorObject = errorObject;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    public ErrorObject getErrorObject() {
        return this.errorObject;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setAuthorization(this.getAccessToken().toAuthorizationHeader());
        httpRequest.setEntityContentType(ContentType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("auth_req_id", this.getAuthRequestID().getValue());
        jsonObject.putAll(this.getErrorObject().toJSONObject());
        httpRequest.setQuery(jsonObject.toJSONString());
        return httpRequest;
    }

    public static CIBAErrorDelivery parse(HTTPRequest httpRequest) throws ParseException {
        URI uri = httpRequest.getURI();
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_JSON);
        BearerAccessToken clientNotificationToken = BearerAccessToken.parse(httpRequest);
        AuthRequestID authRequestID = new AuthRequestID(JSONObjectUtils.getString(httpRequest.getQueryAsJSONObject(), "auth_req_id"));
        ErrorObject errorObject = ErrorObject.parse(httpRequest.getQueryAsJSONObject());
        return new CIBAErrorDelivery(uri, clientNotificationToken, authRequestID, errorObject);
    }

    static {
        HashSet<ErrorObject> errors = new HashSet<ErrorObject>();
        errors.add(OAuth2Error.ACCESS_DENIED);
        errors.add(CIBAError.EXPIRED_TOKEN);
        errors.add(CIBAError.TRANSACTION_FAILED);
        STANDARD_ERRORS = Collections.unmodifiableSet(errors);
    }
}

