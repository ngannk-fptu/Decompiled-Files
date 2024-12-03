/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RequestObjectPOSTResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.jcip.annotations.Immutable;

@Deprecated
@Immutable
public final class RequestObjectPOSTErrorResponse
extends RequestObjectPOSTResponse
implements ErrorResponse {
    private final ErrorObject errorObject;

    public RequestObjectPOSTErrorResponse(int httpStatusCode) {
        this.errorObject = new ErrorObject(null, null, httpStatusCode);
    }

    public int getHTTPStatusCode() {
        return this.errorObject.getHTTPStatusCode();
    }

    @Override
    public ErrorObject getErrorObject() {
        return this.errorObject;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        return new HTTPResponse(this.getHTTPStatusCode());
    }

    public static RequestObjectPOSTErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() >= 200 && httpResponse.getStatusCode() <= 299) {
            throw new ParseException("Unexpected HTTP status code, must not be 2xx");
        }
        return new RequestObjectPOSTErrorResponse(httpResponse.getStatusCode());
    }
}

