/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.jwt;

import com.atlassian.jwt.exception.JwsUnsupportedAlgorithmException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public enum SigningAlgorithm {
    HS256,
    RS256;


    public static SigningAlgorithm forName(String alg) throws JwsUnsupportedAlgorithmException {
        try {
            return SigningAlgorithm.valueOf(alg.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new JwsUnsupportedAlgorithmException(alg + " is not a supported JWS algorithm. Please try one of: [" + StringUtils.join(Arrays.asList(SigningAlgorithm.values()), (String)",") + "]");
        }
    }

    public boolean requiresSharedSecret() {
        return this.equals((Object)HS256);
    }

    public boolean requiresKeyPair() {
        return this.equals((Object)RS256);
    }
}

