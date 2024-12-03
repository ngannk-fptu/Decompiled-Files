/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.id.Identifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class HashClaim
extends Identifier {
    protected HashClaim(String value) {
        super(value);
    }

    public static MessageDigest getMessageDigestInstance(JWSAlgorithm alg) {
        String mdAlg;
        if (alg.equals(JWSAlgorithm.HS256) || alg.equals(JWSAlgorithm.RS256) || alg.equals(JWSAlgorithm.ES256) || alg.equals(JWSAlgorithm.PS256)) {
            mdAlg = "SHA-256";
        } else if (alg.equals(JWSAlgorithm.HS384) || alg.equals(JWSAlgorithm.RS384) || alg.equals(JWSAlgorithm.ES384) || alg.equals(JWSAlgorithm.PS384)) {
            mdAlg = "SHA-384";
        } else if (alg.equals(JWSAlgorithm.HS512) || alg.equals(JWSAlgorithm.RS512) || alg.equals(JWSAlgorithm.ES512) || alg.equals(JWSAlgorithm.PS512)) {
            mdAlg = "SHA-512";
        } else {
            return null;
        }
        try {
            return MessageDigest.getInstance(mdAlg);
        }
        catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String computeValue(Identifier identifier, JWSAlgorithm alg) {
        MessageDigest md = HashClaim.getMessageDigestInstance(alg);
        if (md == null) {
            return null;
        }
        md.update(identifier.getValue().getBytes(StandardCharsets.US_ASCII));
        byte[] hash = md.digest();
        byte[] firstHalf = Arrays.copyOf(hash, hash.length / 2);
        return Base64URL.encode(firstHalf).toString();
    }
}

