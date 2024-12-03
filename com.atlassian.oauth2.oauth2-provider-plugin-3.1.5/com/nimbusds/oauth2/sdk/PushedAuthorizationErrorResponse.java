/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.PushedAuthorizationResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.jcip.annotations.Immutable;

@Immutable
public class PushedAuthorizationErrorResponse
extends PushedAuthorizationResponse
implements ErrorResponse {
    private final ErrorObject error;

    public PushedAuthorizationErrorResponse(ErrorObject error) {
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

    @Override
    public HTTPResponse toHTTPResponse() {
        return this.getErrorObject().toHTTPResponse();
    }

    public static PushedAuthorizationErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        int statusCode = httpResponse.getStatusCode();
        if (statusCode == 201 || statusCode == 200) {
            throw new ParseException("The HTTP status code must be other than 201 and 200");
        }
        ErrorObject errorObject = httpResponse.getEntityContentType() != null && ContentType.APPLICATION_JSON.matches(httpResponse.getEntityContentType()) ? ErrorObject.parse(httpResponse.getContentAsJSONObject()) : new ErrorObject(null);
        return new PushedAuthorizationErrorResponse(errorObject.setHTTPStatusCode(statusCode));
    }
}

