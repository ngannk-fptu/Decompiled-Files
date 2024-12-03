/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RequestObjectPOSTErrorResponse;
import com.nimbusds.oauth2.sdk.RequestObjectPOSTSuccessResponse;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

@Deprecated
public abstract class RequestObjectPOSTResponse
implements Response {
    public RequestObjectPOSTSuccessResponse toSuccessResponse() {
        return (RequestObjectPOSTSuccessResponse)this;
    }

    public RequestObjectPOSTErrorResponse toErrorResponse() {
        return (RequestObjectPOSTErrorResponse)this;
    }

    public static RequestObjectPOSTResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 201 || httpResponse.getStatusCode() == 200) {
            return RequestObjectPOSTSuccessResponse.parse(httpResponse);
        }
        return RequestObjectPOSTErrorResponse.parse(httpResponse);
    }
}

