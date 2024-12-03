/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTClaimsSet$Builder
 */
package com.nimbusds.oauth2.sdk.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public final class DPoPUtils {
    public static JWTClaimsSet createJWTClaimsSet(JWTID jti, String htm, URI htu, Date iat, AccessToken accessToken) throws JOSEException {
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
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder().jwtID(jti.getValue()).claim("htm", (Object)htm).claim("htu", (Object)htu.toString()).issueTime(iat);
        if (accessToken != null) {
            builder = builder.claim("ath", (Object)DPoPUtils.computeSHA256(accessToken).toString());
        }
        return builder.build();
    }

    public static Base64URL computeSHA256(AccessToken accessToken) throws JOSEException {
        byte[] hash;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(accessToken.getValue().getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException(e.getMessage(), (Throwable)e);
        }
        return Base64URL.encode((byte[])hash);
    }

    private DPoPUtils() {
    }
}

