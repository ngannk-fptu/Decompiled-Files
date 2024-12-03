/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.OAuth2Error;

public class InvalidClientException
extends GeneralException {
    public static final InvalidClientException BAD_ID = new InvalidClientException("Bad client ID");
    public static final InvalidClientException NOT_REGISTERED_FOR_AUTH_METHOD = new InvalidClientException("The client is not registered for the requested authentication method");
    public static final InvalidClientException NO_REGISTERED_SECRET = new InvalidClientException("The client has no registered secret");
    public static final InvalidClientException NO_REGISTERED_JWK_SET = new InvalidClientException("The client has no registered JWK set");
    public static final InvalidClientException EXPIRED_SECRET = new InvalidClientException("Expired client secret");
    public static final InvalidClientException BAD_SECRET = new InvalidClientException("Bad client secret");
    public static final InvalidClientException BAD_JWT_HMAC = new InvalidClientException("Bad JWT HMAC");
    public static final InvalidClientException NO_MATCHING_JWK = new InvalidClientException("No matching JWKs found");
    public static final InvalidClientException BAD_JWT_SIGNATURE = new InvalidClientException("Bad JWT signature");
    public static final InvalidClientException BAD_SELF_SIGNED_CLIENT_CERTIFICATE = new InvalidClientException("Couldn't validate client X.509 certificate signature: No matching registered client JWK found");

    public InvalidClientException(String message) {
        super(message);
    }

    @Override
    public ErrorObject getErrorObject() {
        return OAuth2Error.INVALID_CLIENT;
    }
}

