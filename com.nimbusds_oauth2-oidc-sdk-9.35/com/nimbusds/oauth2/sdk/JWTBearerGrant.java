/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEObject
 *  com.nimbusds.jose.JWEObject
 *  com.nimbusds.jose.JWEObject$State
 *  com.nimbusds.jose.JWSObject
 *  com.nimbusds.jose.JWSObject$State
 *  com.nimbusds.jose.PlainObject
 *  com.nimbusds.jwt.EncryptedJWT
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AssertionGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class JWTBearerGrant
extends AssertionGrant {
    public static final GrantType GRANT_TYPE = GrantType.JWT_BEARER;
    private static final String PLAIN_ASSERTION_REJECTED_MESSAGE = "The JWT assertion must not be unsecured (plain)";
    private static final String JWT_PARSE_MESSAGE = "The assertion is not a JWT";
    private static final ParseException PLAIN_ASSERTION_REJECTED_EXCEPTION = new ParseException("The JWT assertion must not be unsecured (plain)", OAuth2Error.INVALID_REQUEST.appendDescription(": The JWT assertion must not be unsecured (plain)"));
    private static final ParseException JWT_PARSE_EXCEPTION = new ParseException("The assertion is not a JWT", OAuth2Error.INVALID_REQUEST.appendDescription(": The assertion is not a JWT"));
    private final JOSEObject assertion;

    public JWTBearerGrant(SignedJWT assertion) {
        super(GRANT_TYPE);
        if (assertion.getState().equals((Object)JWSObject.State.UNSIGNED)) {
            throw new IllegalArgumentException("The JWT assertion must not be in a unsigned state");
        }
        this.assertion = assertion;
    }

    public JWTBearerGrant(JWEObject assertion) {
        super(GRANT_TYPE);
        if (assertion.getState().equals((Object)JWEObject.State.UNENCRYPTED)) {
            throw new IllegalArgumentException("The JWT assertion must not be in a unencrypted state");
        }
        this.assertion = assertion;
    }

    public JWTBearerGrant(EncryptedJWT assertion) {
        this((JWEObject)assertion);
    }

    public JWT getJWTAssertion() {
        return this.assertion instanceof JWT ? (JWT)this.assertion : null;
    }

    public JOSEObject getJOSEAssertion() {
        return this.assertion;
    }

    @Override
    public String getAssertion() {
        return this.assertion.serialize();
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("assertion", Collections.singletonList(this.assertion.serialize()));
        return params;
    }

    public static JWTBearerGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String assertionString = MultivaluedMapUtils.getFirstValue(params, "assertion");
        if (assertionString == null || assertionString.trim().isEmpty()) {
            throw MISSING_ASSERTION_PARAM_EXCEPTION;
        }
        try {
            JOSEObject assertion = JOSEObject.parse((String)assertionString);
            if (assertion instanceof PlainObject) {
                throw PLAIN_ASSERTION_REJECTED_EXCEPTION;
            }
            if (assertion instanceof JWSObject) {
                return new JWTBearerGrant(new SignedJWT(assertion.getParsedParts()[0], assertion.getParsedParts()[1], assertion.getParsedParts()[2]));
            }
            if ("JWT".equalsIgnoreCase(assertion.getHeader().getContentType())) {
                return new JWTBearerGrant((JWEObject)assertion);
            }
            return new JWTBearerGrant(new EncryptedJWT(assertion.getParsedParts()[0], assertion.getParsedParts()[1], assertion.getParsedParts()[2], assertion.getParsedParts()[3], assertion.getParsedParts()[4]));
        }
        catch (java.text.ParseException e) {
            throw JWT_PARSE_EXCEPTION;
        }
    }
}

