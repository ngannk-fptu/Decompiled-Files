/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import net.jcip.annotations.Immutable;

@Immutable
public class UserInfoSuccessResponse
extends UserInfoResponse
implements SuccessResponse {
    private final UserInfo claimsSet;
    private final JWT jwt;

    public UserInfoSuccessResponse(UserInfo claimsSet) {
        if (claimsSet == null) {
            throw new IllegalArgumentException("The claims must not be null");
        }
        this.claimsSet = claimsSet;
        this.jwt = null;
    }

    public UserInfoSuccessResponse(JWT jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("The claims JWT must not be null");
        }
        this.jwt = jwt;
        this.claimsSet = null;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public ContentType getEntityContentType() {
        if (this.claimsSet != null) {
            return ContentType.APPLICATION_JSON;
        }
        return ContentType.APPLICATION_JWT;
    }

    public UserInfo getUserInfo() {
        return this.claimsSet;
    }

    public JWT getUserInfoJWT() {
        return this.jwt;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        String content;
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(this.getEntityContentType());
        if (this.claimsSet != null) {
            content = this.claimsSet.toJSONObject().toString();
        } else {
            try {
                content = this.jwt.serialize();
            }
            catch (IllegalStateException e) {
                throw new SerializeException("Couldn't serialize UserInfo claims JWT: " + e.getMessage(), e);
            }
        }
        httpResponse.setContent(content);
        return httpResponse;
    }

    public static UserInfoSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        UserInfoSuccessResponse response;
        httpResponse.ensureStatusCode(200);
        httpResponse.ensureEntityContentType();
        ContentType ct = httpResponse.getEntityContentType();
        if (ct.matches(ContentType.APPLICATION_JSON)) {
            UserInfo claimsSet;
            try {
                claimsSet = new UserInfo(httpResponse.getContentAsJSONObject());
            }
            catch (Exception e) {
                throw new ParseException("Couldn't parse UserInfo claims: " + e.getMessage(), e);
            }
            response = new UserInfoSuccessResponse(claimsSet);
        } else if (ct.matches(ContentType.APPLICATION_JWT)) {
            JWT jwt;
            try {
                jwt = httpResponse.getContentAsJWT();
            }
            catch (ParseException e) {
                throw new ParseException("Couldn't parse UserInfo claims JWT: " + e.getMessage(), e);
            }
            response = new UserInfoSuccessResponse(jwt);
        } else {
            throw new ParseException("Unexpected Content-Type, must be " + ContentType.APPLICATION_JSON + " or " + ContentType.APPLICATION_JWT);
        }
        return response;
    }
}

