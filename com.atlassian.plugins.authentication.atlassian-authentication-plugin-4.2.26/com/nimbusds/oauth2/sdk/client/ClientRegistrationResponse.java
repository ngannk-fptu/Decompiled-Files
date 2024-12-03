/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.client.ClientInformationResponse;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationErrorResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

public abstract class ClientRegistrationResponse
implements Response {
    public ClientInformationResponse toSuccessResponse() {
        return (ClientInformationResponse)this;
    }

    public ClientRegistrationErrorResponse toErrorResponse() {
        return (ClientRegistrationErrorResponse)this;
    }

    public static ClientRegistrationResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 201 || httpResponse.getStatusCode() == 200) {
            return ClientInformationResponse.parse(httpResponse);
        }
        return ClientRegistrationErrorResponse.parse(httpResponse);
    }
}

