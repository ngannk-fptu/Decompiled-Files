/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.api.TrustNegotiationErrorResponse;
import com.nimbusds.openid.connect.sdk.federation.api.TrustNegotiationSuccessResponse;

public abstract class TrustNegotiationResponse
implements Response {
    public TrustNegotiationSuccessResponse toSuccessResponse() {
        return (TrustNegotiationSuccessResponse)this;
    }

    public TrustNegotiationErrorResponse toErrorResponse() {
        return (TrustNegotiationErrorResponse)this;
    }

    public static TrustNegotiationResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.indicatesSuccess()) {
            return TrustNegotiationSuccessResponse.parse(httpResponse);
        }
        return TrustNegotiationErrorResponse.parse(httpResponse);
    }
}

