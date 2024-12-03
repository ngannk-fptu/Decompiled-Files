/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class GrantType
extends Identifier {
    public static final GrantType AUTHORIZATION_CODE = new GrantType("authorization_code", false, true, new HashSet<String>(Arrays.asList("code", "redirect_uri", "code_verifier")));
    public static final GrantType IMPLICIT = new GrantType("implicit", false, true, Collections.emptySet());
    public static final GrantType REFRESH_TOKEN = new GrantType("refresh_token", false, false, Collections.singleton("refresh_token"));
    public static final GrantType PASSWORD = new GrantType("password", false, false, new HashSet<String>(Arrays.asList("username", "password")));
    public static final GrantType CLIENT_CREDENTIALS = new GrantType("client_credentials", true, true, Collections.emptySet());
    public static final GrantType JWT_BEARER = new GrantType("urn:ietf:params:oauth:grant-type:jwt-bearer", false, false, Collections.singleton("assertion"));
    public static final GrantType SAML2_BEARER = new GrantType("urn:ietf:params:oauth:grant-type:saml2-bearer", false, false, Collections.singleton("assertion"));
    public static final GrantType DEVICE_CODE = new GrantType("urn:ietf:params:oauth:grant-type:device_code", false, true, Collections.singleton("device_code"));
    public static final GrantType CIBA = new GrantType("urn:openid:params:grant-type:ciba", true, true, Collections.singleton("auth_req_id"));
    public static final GrantType TOKEN_EXCHANGE = new GrantType("urn:ietf:params:oauth:grant-type:token-exchange", false, false, new HashSet<String>(Arrays.asList("audience", "requested_token_type", "subject_token", "subject_token_type", "actor_token", "actor_token_type")));
    private static final long serialVersionUID = -5367937758427680765L;
    private final boolean requiresClientAuth;
    private final boolean requiresClientID;
    private final Set<String> requestParamNames;

    public GrantType(String value) {
        this(value, false, false, Collections.emptySet());
    }

    private GrantType(String value, boolean requiresClientAuth, boolean requiresClientID, Set<String> requestParamNames) {
        super(value);
        this.requiresClientAuth = requiresClientAuth;
        this.requiresClientID = requiresClientID;
        this.requestParamNames = requestParamNames == null ? Collections.emptySet() : Collections.unmodifiableSet(requestParamNames);
    }

    public boolean requiresClientAuthentication() {
        return this.requiresClientAuth;
    }

    public boolean requiresClientID() {
        return this.requiresClientID;
    }

    public Set<String> getRequestParameterNames() {
        return this.requestParamNames;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof GrantType && this.toString().equals(object.toString());
    }

    public static GrantType parse(String value) throws ParseException {
        GrantType grantType;
        try {
            grantType = new GrantType(value);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
        if (grantType.equals(AUTHORIZATION_CODE)) {
            return AUTHORIZATION_CODE;
        }
        if (grantType.equals(IMPLICIT)) {
            return IMPLICIT;
        }
        if (grantType.equals(REFRESH_TOKEN)) {
            return REFRESH_TOKEN;
        }
        if (grantType.equals(PASSWORD)) {
            return PASSWORD;
        }
        if (grantType.equals(CLIENT_CREDENTIALS)) {
            return CLIENT_CREDENTIALS;
        }
        if (grantType.equals(JWT_BEARER)) {
            return JWT_BEARER;
        }
        if (grantType.equals(SAML2_BEARER)) {
            return SAML2_BEARER;
        }
        if (grantType.equals(DEVICE_CODE)) {
            return DEVICE_CODE;
        }
        if (grantType.equals(CIBA)) {
            return CIBA;
        }
        if (grantType.equals(TOKEN_EXCHANGE)) {
            return TOKEN_EXCHANGE;
        }
        return grantType;
    }

    public static void ensure(GrantType grantType, Map<String, List<String>> params) throws ParseException {
        String grantTypeString = MultivaluedMapUtils.getFirstValue(params, "grant_type");
        if (grantTypeString == null) {
            String msg = "Missing grant_type parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        if (!GrantType.parse(grantTypeString).equals(grantType)) {
            String msg = "The grant_type must be " + grantType + "";
            throw new ParseException(msg, OAuth2Error.UNSUPPORTED_GRANT_TYPE.appendDescription(": " + msg));
        }
    }
}

