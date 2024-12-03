/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.oauth2.sdk.token.DPoPTokenError;
import com.nimbusds.oauth2.sdk.token.TokenSchemeError;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class UserInfoErrorResponse
extends UserInfoResponse
implements ErrorResponse {
    private final ErrorObject error;

    public static Set<BearerTokenError> getStandardErrors() {
        HashSet<BearerTokenError> stdErrors = new HashSet<BearerTokenError>();
        stdErrors.add(BearerTokenError.MISSING_TOKEN);
        stdErrors.add(BearerTokenError.INVALID_REQUEST);
        stdErrors.add(BearerTokenError.INVALID_TOKEN);
        stdErrors.add(BearerTokenError.INSUFFICIENT_SCOPE);
        return Collections.unmodifiableSet(stdErrors);
    }

    private UserInfoErrorResponse() {
        this.error = null;
    }

    public UserInfoErrorResponse(BearerTokenError error) {
        this((ErrorObject)error);
    }

    public UserInfoErrorResponse(DPoPTokenError error) {
        this((ErrorObject)error);
    }

    public UserInfoErrorResponse(ErrorObject error) {
        if (error == null) {
            throw new IllegalArgumentException("The error must not be null");
        }
        this.error = error;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    @Override
    public ErrorObject getErrorObject() {
        return this.error;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = this.error != null && this.error.getHTTPStatusCode() > 0 ? new HTTPResponse(this.error.getHTTPStatusCode()) : new HTTPResponse(400);
        if (this.error instanceof TokenSchemeError) {
            httpResponse.setWWWAuthenticate(((TokenSchemeError)this.error).toWWWAuthenticateHeader());
        } else if (this.error != null) {
            httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
            httpResponse.setContent(this.error.toJSONObject().toJSONString());
        }
        return httpResponse;
    }

    public static UserInfoErrorResponse parse(String wwwAuth) throws ParseException {
        BearerTokenError error = BearerTokenError.parse(wwwAuth);
        return new UserInfoErrorResponse(error);
    }

    public static UserInfoErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        String wwwAuth = httpResponse.getWWWAuthenticate();
        if (StringUtils.isNotBlank(wwwAuth)) {
            if (wwwAuth.toLowerCase().startsWith(AccessTokenType.BEARER.getValue().toLowerCase())) {
                try {
                    BearerTokenError bte = BearerTokenError.parse(wwwAuth);
                    return new UserInfoErrorResponse(new BearerTokenError(bte.getCode(), bte.getDescription(), httpResponse.getStatusCode(), bte.getURI(), bte.getRealm(), bte.getScope()));
                }
                catch (ParseException bte) {}
            } else if (wwwAuth.toLowerCase().startsWith(AccessTokenType.DPOP.getValue().toLowerCase())) {
                try {
                    DPoPTokenError dte = DPoPTokenError.parse(wwwAuth);
                    return new UserInfoErrorResponse(new DPoPTokenError(dte.getCode(), dte.getDescription(), httpResponse.getStatusCode(), dte.getURI(), dte.getRealm(), dte.getScope(), dte.getJWSAlgorithms()));
                }
                catch (ParseException parseException) {
                    // empty catch block
                }
            }
        }
        return new UserInfoErrorResponse(ErrorObject.parse(httpResponse));
    }
}

