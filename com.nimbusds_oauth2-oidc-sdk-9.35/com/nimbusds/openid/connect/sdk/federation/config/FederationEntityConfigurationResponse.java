/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.config;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.config.FederationEntityConfigurationErrorResponse;
import com.nimbusds.openid.connect.sdk.federation.config.FederationEntityConfigurationSuccessResponse;

public abstract class FederationEntityConfigurationResponse
implements Response {
    public FederationEntityConfigurationSuccessResponse toSuccessResponse() {
        return (FederationEntityConfigurationSuccessResponse)this;
    }

    public FederationEntityConfigurationErrorResponse toErrorResponse() {
        return (FederationEntityConfigurationErrorResponse)this;
    }

    public static FederationEntityConfigurationResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return FederationEntityConfigurationSuccessResponse.parse(httpResponse);
        }
        return FederationEntityConfigurationErrorResponse.parse(httpResponse);
    }
}

