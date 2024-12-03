/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import java.util.Date;

final class DPoPUtils {
    static JWTClaimsSet createJWTClaimsSet(JWTID jti, String htm, URI htu, Date iat) {
        if (StringUtils.isBlank(htm)) {
            throw new IllegalArgumentException("The HTTP method (htu) is required");
        }
        if (htu.getQuery() != null) {
            throw new IllegalArgumentException("The HTTP URI (htu) must not have a query");
        }
        if (htu.getFragment() != null) {
            throw new IllegalArgumentException("The HTTP URI (htu) must not have a fragment");
        }
        if (iat == null) {
            throw new IllegalArgumentException("The issue time (iat) is required");
        }
        return new JWTClaimsSet.Builder().jwtID(jti.getValue()).claim("htm", htm).claim("htu", htu.toString()).issueTime(iat).build();
    }

    private DPoPUtils() {
    }
}

