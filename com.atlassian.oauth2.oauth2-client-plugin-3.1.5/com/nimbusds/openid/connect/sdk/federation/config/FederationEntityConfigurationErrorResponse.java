/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.config;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.config.FederationEntityConfigurationResponse;

public class FederationEntityConfigurationErrorResponse
extends FederationEntityConfigurationResponse
implements ErrorResponse {
    private final ErrorObject error;

    public FederationEntityConfigurationErrorResponse(ErrorObject error) {
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

    public static FederationEntityConfigurationErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        ErrorObject errorObject = httpResponse.getEntityContentType() != null && ContentType.APPLICATION_JSON.matches(httpResponse.getEntityContentType()) ? ErrorObject.parse(httpResponse.getContentAsJSONObject()) : new ErrorObject(null);
        errorObject = errorObject.setHTTPStatusCode(httpResponse.getStatusCode());
        return new FederationEntityConfigurationErrorResponse(errorObject);
    }
}

