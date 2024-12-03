/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.api.EntityListingErrorResponse;
import com.nimbusds.openid.connect.sdk.federation.api.EntityListingSuccessResponse;

public abstract class EntityListingResponse
implements Response {
    public EntityListingSuccessResponse toSuccessResponse() {
        return (EntityListingSuccessResponse)this;
    }

    public EntityListingErrorResponse toErrorResponse() {
        return (EntityListingErrorResponse)this;
    }

    public static EntityListingResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.indicatesSuccess()) {
            return EntityListingSuccessResponse.parse(httpResponse);
        }
        return EntityListingErrorResponse.parse(httpResponse);
    }
}

