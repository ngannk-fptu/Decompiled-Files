/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class TokenIntrospectionErrorResponse
extends TokenIntrospectionResponse
implements ErrorResponse {
    private static final Set<ErrorObject> STANDARD_ERRORS;
    private final ErrorObject error;

    public static Set<ErrorObject> getStandardErrors() {
        return STANDARD_ERRORS;
    }

    public TokenIntrospectionErrorResponse(ErrorObject error) {
        this.error = error;
    }

    @Override
    public ErrorObject getErrorObject() {
        return this.error;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        int statusCode = this.error != null && this.error.getHTTPStatusCode() > 0 ? this.error.getHTTPStatusCode() : 400;
        HTTPResponse httpResponse = new HTTPResponse(statusCode);
        if (this.error == null) {
            return httpResponse;
        }
        if (this.error instanceof BearerTokenError) {
            httpResponse.setWWWAuthenticate(((BearerTokenError)this.error).toWWWAuthenticateHeader());
        }
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        httpResponse.setContent(this.error.toJSONObject().toJSONString());
        return httpResponse;
    }

    public static TokenIntrospectionErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        String wwwAuth = httpResponse.getWWWAuthenticate();
        if ((httpResponse.getStatusCode() == 401 || httpResponse.getStatusCode() == 403) && wwwAuth != null && wwwAuth.toLowerCase().startsWith("bearer")) {
            try {
                return new TokenIntrospectionErrorResponse(BearerTokenError.parse(httpResponse.getWWWAuthenticate()));
            }
            catch (ParseException parseException) {
                // empty catch block
            }
        }
        return new TokenIntrospectionErrorResponse(ErrorObject.parse(httpResponse));
    }

    static {
        HashSet<ErrorObject> errors = new HashSet<ErrorObject>();
        errors.add(OAuth2Error.INVALID_REQUEST);
        errors.add(OAuth2Error.INVALID_CLIENT);
        errors.add(BearerTokenError.MISSING_TOKEN);
        errors.add(BearerTokenError.INVALID_REQUEST);
        errors.add(BearerTokenError.INVALID_TOKEN);
        errors.add(BearerTokenError.INSUFFICIENT_SCOPE);
        STANDARD_ERRORS = Collections.unmodifiableSet(errors);
    }
}

