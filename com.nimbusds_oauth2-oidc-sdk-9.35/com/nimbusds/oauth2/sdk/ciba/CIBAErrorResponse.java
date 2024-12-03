/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ciba.CIBAError;
import com.nimbusds.oauth2.sdk.ciba.CIBAResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class CIBAErrorResponse
extends CIBAResponse
implements ErrorResponse {
    private static final Set<ErrorObject> STANDARD_ERRORS;
    private final ErrorObject error;

    public static Set<ErrorObject> getStandardErrors() {
        return STANDARD_ERRORS;
    }

    protected CIBAErrorResponse() {
        this.error = null;
    }

    public CIBAErrorResponse(ErrorObject error) {
        if (error == null) {
            throw new IllegalArgumentException("The error must not be null");
        }
        this.error = error;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    @Override
    public ErrorObject getErrorObject() {
        return this.error;
    }

    public JSONObject toJSONObject() {
        if (this.error != null) {
            return this.error.toJSONObject();
        }
        return new JSONObject();
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        int statusCode = this.error != null && this.error.getHTTPStatusCode() > 0 ? this.error.getHTTPStatusCode() : 400;
        HTTPResponse httpResponse = new HTTPResponse(statusCode);
        if (this.error == null) {
            return httpResponse;
        }
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        httpResponse.setContent(this.toJSONObject().toString());
        return httpResponse;
    }

    public static CIBAErrorResponse parse(JSONObject jsonObject) throws ParseException {
        if (!jsonObject.containsKey((Object)"error")) {
            return new CIBAErrorResponse();
        }
        return new CIBAErrorResponse(ErrorObject.parse(jsonObject));
    }

    public static CIBAErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        return new CIBAErrorResponse(ErrorObject.parse(httpResponse));
    }

    static {
        HashSet<ErrorObject> errors = new HashSet<ErrorObject>();
        errors.add(OAuth2Error.INVALID_REQUEST);
        errors.add(OAuth2Error.INVALID_SCOPE);
        errors.add(OAuth2Error.INVALID_CLIENT);
        errors.add(OAuth2Error.UNAUTHORIZED_CLIENT);
        errors.add(OAuth2Error.ACCESS_DENIED);
        errors.add(CIBAError.EXPIRED_LOGIN_HINT_TOKEN);
        errors.add(CIBAError.UNKNOWN_USER_ID);
        errors.add(CIBAError.MISSING_USER_CODE);
        errors.add(CIBAError.INVALID_USER_CODE);
        errors.add(CIBAError.INVALID_BINDING_MESSAGE);
        STANDARD_ERRORS = Collections.unmodifiableSet(errors);
    }
}

