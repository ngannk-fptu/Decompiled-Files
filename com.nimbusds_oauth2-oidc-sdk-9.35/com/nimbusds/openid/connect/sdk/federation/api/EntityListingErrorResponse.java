/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.api.EntityListingResponse;
import com.nimbusds.openid.connect.sdk.federation.api.FederationAPIError;
import net.jcip.annotations.Immutable;

@Immutable
public class EntityListingErrorResponse
extends EntityListingResponse {
    private final FederationAPIError error;

    public EntityListingErrorResponse(FederationAPIError error) {
        if (error == null) {
            throw new IllegalArgumentException("The error object must not be null");
        }
        this.error = error;
    }

    public FederationAPIError getErrorObject() {
        return this.error;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        return this.error.toHTTPResponse();
    }

    public static EntityListingErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        return new EntityListingErrorResponse(FederationAPIError.parse(httpResponse));
    }
}

