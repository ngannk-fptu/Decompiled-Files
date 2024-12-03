/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationResponse;
import com.nimbusds.oauth2.sdk.client.RegistrationError;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class ClientRegistrationErrorResponse
extends ClientRegistrationResponse
implements ErrorResponse {
    private final ErrorObject error;

    public static Set<ErrorObject> getStandardErrors() {
        HashSet<ErrorObject> stdErrors = new HashSet<ErrorObject>();
        stdErrors.add(BearerTokenError.MISSING_TOKEN);
        stdErrors.add(BearerTokenError.INVALID_REQUEST);
        stdErrors.add(BearerTokenError.INVALID_TOKEN);
        stdErrors.add(BearerTokenError.INSUFFICIENT_SCOPE);
        stdErrors.add(RegistrationError.INVALID_REDIRECT_URI);
        stdErrors.add(RegistrationError.INVALID_CLIENT_METADATA);
        stdErrors.add(RegistrationError.INVALID_SOFTWARE_STATEMENT);
        stdErrors.add(RegistrationError.UNAPPROVED_SOFTWARE_STATEMENT);
        return Collections.unmodifiableSet(stdErrors);
    }

    public ClientRegistrationErrorResponse(ErrorObject error) {
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
        HTTPResponse httpResponse = this.error.getHTTPStatusCode() > 0 ? new HTTPResponse(this.error.getHTTPStatusCode()) : new HTTPResponse(400);
        if (this.error instanceof BearerTokenError) {
            BearerTokenError bte = (BearerTokenError)this.error;
            httpResponse.setWWWAuthenticate(bte.toWWWAuthenticateHeader());
        } else {
            JSONObject jsonObject = new JSONObject();
            if (this.error.getCode() != null) {
                jsonObject.put((Object)"error", (Object)this.error.getCode());
            }
            if (this.error.getDescription() != null) {
                jsonObject.put((Object)"error_description", (Object)this.error.getDescription());
            }
            httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
            httpResponse.setContent(jsonObject.toString());
        }
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        return httpResponse;
    }

    public static ClientRegistrationErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCodeNotOK();
        String wwwAuth = httpResponse.getWWWAuthenticate();
        ErrorObject error = StringUtils.isNotBlank(wwwAuth) ? BearerTokenError.parse(wwwAuth) : ErrorObject.parse(httpResponse);
        return new ClientRegistrationErrorResponse(error);
    }
}

