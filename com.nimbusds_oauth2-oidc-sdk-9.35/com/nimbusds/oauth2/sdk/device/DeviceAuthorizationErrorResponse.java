/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class DeviceAuthorizationErrorResponse
extends DeviceAuthorizationResponse
implements ErrorResponse {
    private static final Set<ErrorObject> STANDARD_ERRORS;
    private final ErrorObject error;

    public static Set<ErrorObject> getStandardErrors() {
        return STANDARD_ERRORS;
    }

    protected DeviceAuthorizationErrorResponse() {
        this.error = null;
    }

    public DeviceAuthorizationErrorResponse(ErrorObject error) {
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
        JSONObject o = new JSONObject();
        if (this.error == null) {
            return o;
        }
        o.put((Object)"error", (Object)this.error.getCode());
        if (this.error.getDescription() != null) {
            o.put((Object)"error_description", (Object)this.error.getDescription());
        }
        if (this.error.getURI() != null) {
            o.put((Object)"error_uri", (Object)this.error.getURI().toString());
        }
        return o;
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

    public static DeviceAuthorizationErrorResponse parse(JSONObject jsonObject) throws ParseException {
        ErrorObject error;
        if (!jsonObject.containsKey((Object)"error")) {
            return new DeviceAuthorizationErrorResponse();
        }
        try {
            String code = JSONObjectUtils.getString(jsonObject, "error");
            String description = JSONObjectUtils.getString(jsonObject, "error_description", null);
            URI uri = JSONObjectUtils.getURI(jsonObject, "error_uri", null);
            error = new ErrorObject(code, description, 400, uri);
        }
        catch (ParseException e) {
            throw new ParseException("Missing or invalid token error response parameter: " + e.getMessage(), e);
        }
        return new DeviceAuthorizationErrorResponse(error);
    }

    public static DeviceAuthorizationErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        return new DeviceAuthorizationErrorResponse(ErrorObject.parse(httpResponse));
    }

    static {
        HashSet<ErrorObject> errors = new HashSet<ErrorObject>();
        errors.add(OAuth2Error.INVALID_REQUEST);
        errors.add(OAuth2Error.INVALID_CLIENT);
        errors.add(OAuth2Error.INVALID_SCOPE);
        STANDARD_ERRORS = Collections.unmodifiableSet(errors);
    }
}

