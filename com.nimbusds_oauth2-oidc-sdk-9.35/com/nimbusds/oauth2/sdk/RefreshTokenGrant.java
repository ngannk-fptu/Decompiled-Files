/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class RefreshTokenGrant
extends AuthorizationGrant {
    public static final GrantType GRANT_TYPE = GrantType.REFRESH_TOKEN;
    private final RefreshToken refreshToken;

    public RefreshTokenGrant(RefreshToken refreshToken) {
        super(GRANT_TYPE);
        if (refreshToken == null) {
            throw new IllegalArgumentException("The refresh token must not be null");
        }
        this.refreshToken = refreshToken;
    }

    public RefreshToken getRefreshToken() {
        return this.refreshToken;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        params.put("grant_type", Collections.singletonList(GRANT_TYPE.getValue()));
        params.put("refresh_token", Collections.singletonList(this.refreshToken.getValue()));
        return params;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RefreshTokenGrant grant = (RefreshTokenGrant)o;
        return this.refreshToken.equals(grant.refreshToken);
    }

    public int hashCode() {
        return this.refreshToken.hashCode();
    }

    public static RefreshTokenGrant parse(Map<String, List<String>> params) throws ParseException {
        GrantType.ensure(GRANT_TYPE, params);
        String refreshTokenString = MultivaluedMapUtils.getFirstValue(params, "refresh_token");
        if (refreshTokenString == null || refreshTokenString.trim().isEmpty()) {
            String msg = "Missing or empty refresh_token parameter";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        RefreshToken refreshToken = new RefreshToken(refreshTokenString);
        return new RefreshTokenGrant(refreshToken);
    }
}

