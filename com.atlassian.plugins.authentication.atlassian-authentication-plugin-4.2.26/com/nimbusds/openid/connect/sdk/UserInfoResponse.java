/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;

public abstract class UserInfoResponse
implements Response {
    public UserInfoSuccessResponse toSuccessResponse() {
        return (UserInfoSuccessResponse)this;
    }

    public UserInfoErrorResponse toErrorResponse() {
        return (UserInfoErrorResponse)this;
    }

    public static UserInfoResponse parse(HTTPResponse httpResponse) throws ParseException {
        if (httpResponse.getStatusCode() == 200) {
            return UserInfoSuccessResponse.parse(httpResponse);
        }
        return UserInfoErrorResponse.parse(httpResponse);
    }
}

