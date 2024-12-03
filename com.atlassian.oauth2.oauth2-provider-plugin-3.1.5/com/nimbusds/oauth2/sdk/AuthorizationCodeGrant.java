/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthorizationCodeGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.AUTHORIZATION_CODE;
    private final AuthorizationCode code;
    private final URI redirectURI;
    private final CodeVerifier codeVerifier;

    public AuthorizationCodeGrant(AuthorizationCode code, URI redirectURI) {
        this(code, redirectURI, null);
    }

    public AuthorizationCodeGrant(AuthorizationCode code, URI redirectURI, CodeVerifier codeVerifier) {
        super(GRANT_TYPE);
        if (code == null) {
            throw new IllegalArgumentException("The authorisation code must not be null");
        }
        this.code = code;
        this.redirectURI = redirectURI;
        this.codeVerifier = codeVerifier;
    }

    public AuthorizationCode getAuthorizationCode() {
        return this.code;
    }

    public URI getRedirectionURI() {
        return this.redirectURI;
    }

    public CodeVerifier getCodeVerifier() {
        return this.codeVerifier;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("code", Collections.singletonList(this.code.getValue()));
        if (this.redirectURI != null) {
            params.put("redirect_uri", Collections.singletonList(this.redirectURI.toString()));
        }
        if (this.codeVerifier != null) {
            params.put("code_verifier", Collections.singletonList(this.codeVerifier.getValue()));
        }
        return params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthorizationCodeGrant)) {
            return false;
        }
        AuthorizationCodeGrant that = (AuthorizationCodeGrant)o;
        return this.code.equals(that.code) && Objects.equals(this.redirectURI, that.redirectURI) && Objects.equals(this.getCodeVerifier(), that.getCodeVerifier());
    }

    public int hashCode() {
        return Objects.hash(this.code, this.redirectURI, this.getCodeVerifier());
    }

    public static AuthorizationCodeGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String codeString = MultivaluedMapUtils.getFirstValue(params, "code");
        if (codeString == null || codeString.trim().isEmpty()) {
            String msg = "Missing or empty code parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        AuthorizationCode code = new AuthorizationCode(codeString);
        String redirectURIString = MultivaluedMapUtils.getFirstValue(params, "redirect_uri");
        URI redirectURI = null;
        if (redirectURIString != null) {
            try {
                redirectURI = new URI(redirectURIString);
            }
            catch (URISyntaxException e) {
                String msg = "Invalid redirect_uri parameter: " + e.getMessage();
                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), e);
            }
        }
        String codeVerifierString = MultivaluedMapUtils.getFirstValue(params, "code_verifier");
        CodeVerifier codeVerifier = null;
        if (StringUtils.isNotBlank(codeVerifierString)) {
            try {
                codeVerifier = new CodeVerifier(codeVerifierString);
            }
            catch (IllegalArgumentException e) {
                String msg = "Illegal code verifier: " + e.getMessage();
                throw new ParseException(e.getMessage(), OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg), e);
            }
        }
        return new AuthorizationCodeGrant(code, redirectURI, codeVerifier);
    }
}

