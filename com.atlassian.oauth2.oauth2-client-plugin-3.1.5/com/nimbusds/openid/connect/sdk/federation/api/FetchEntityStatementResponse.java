/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.api.FetchEntityStatementErrorResponse;
import com.nimbusds.openid.connect.sdk.federation.api.FetchEntityStatementSuccessResponse;

public abstract class FetchEntityStatementResponse
implements Response {
    public FetchEntityStatementSuccessResponse toSuccessResponse() {
        return (FetchEntityStatementSuccessResponse)this;
    }

    public FetchEntityStatementErrorResponse toErrorResponse() {
        return (FetchEntityStatementErrorResponse)this;
    }

    public static FetchEntityStatementResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.indicatesSuccess()) {
            return FetchEntityStatementSuccessResponse.parse(httpResponse);
        }
        return FetchEntityStatementErrorResponse.parse(httpResponse);
    }
}

