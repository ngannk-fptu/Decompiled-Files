/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;

public abstract class AssertionGrant
extends AuthorizationGrant {
    private static final String MISSING_ASSERTION_PARAM_MESSAGE = "Missing or empty assertion parameter";
    protected static final ParseException MISSING_ASSERTION_PARAM_EXCEPTION = new ParseException("Missing or empty assertion parameter", OAuth2Error.INVALID_REQUEST.appendDescription(": Missing or empty assertion parameter"));

    protected AssertionGrant(GrantType type) {
        super(type);
    }

    public abstract String getAssertion();
}

