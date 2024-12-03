/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.TokenSchemeError;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public class BearerTokenError
extends TokenSchemeError {
    private static final long serialVersionUID = -5209789923955060584L;
    public static final BearerTokenError MISSING_TOKEN = new BearerTokenError(null, null, 401);
    public static final BearerTokenError INVALID_REQUEST = new BearerTokenError("invalid_request", "Invalid request", 400);
    public static final BearerTokenError INVALID_TOKEN = new BearerTokenError("invalid_token", "Invalid access token", 401);
    public static final BearerTokenError INSUFFICIENT_SCOPE = new BearerTokenError("insufficient_scope", "Insufficient scope", 403);

    public BearerTokenError(String code, String description) {
        this(code, description, 0, null, null, null);
    }

    public BearerTokenError(String code, String description, int httpStatusCode) {
        this(code, description, httpStatusCode, null, null, null);
    }

    public BearerTokenError(String code, String description, int httpStatusCode, URI uri, String realm, Scope scope) {
        super(AccessTokenType.BEARER, code, description, httpStatusCode, uri, realm, scope);
    }

    @Override
    public BearerTokenError setDescription(String description) {
        return new BearerTokenError(super.getCode(), description, super.getHTTPStatusCode(), super.getURI(), this.getRealm(), this.getScope());
    }

    @Override
    public BearerTokenError appendDescription(String text) {
        String newDescription = this.getDescription() != null ? this.getDescription() + text : text;
        return new BearerTokenError(super.getCode(), newDescription, super.getHTTPStatusCode(), super.getURI(), this.getRealm(), this.getScope());
    }

    @Override
    public BearerTokenError setHTTPStatusCode(int httpStatusCode) {
        return new BearerTokenError(super.getCode(), super.getDescription(), httpStatusCode, super.getURI(), this.getRealm(), this.getScope());
    }

    @Override
    public BearerTokenError setURI(URI uri) {
        return new BearerTokenError(super.getCode(), super.getDescription(), super.getHTTPStatusCode(), uri, this.getRealm(), this.getScope());
    }

    @Override
    public BearerTokenError setRealm(String realm) {
        return new BearerTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), realm, this.getScope());
    }

    @Override
    public BearerTokenError setScope(Scope scope) {
        return new BearerTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), this.getRealm(), scope);
    }

    public static BearerTokenError parse(String wwwAuth) throws ParseException {
        TokenSchemeError genericError = TokenSchemeError.parse(wwwAuth, AccessTokenType.BEARER);
        return new BearerTokenError(genericError.getCode(), genericError.getDescription(), genericError.getHTTPStatusCode(), genericError.getURI(), genericError.getRealm(), genericError.getScope());
    }
}

