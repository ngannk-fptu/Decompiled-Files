/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.servlet.cache.model;

import com.google.common.annotations.VisibleForTesting;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.annotation.Nonnull;

@VisibleForTesting
public class ETagToken {
    private static final String DOUBLE_QUOTED_TEMPLATE = "\"%s\"";
    private static final String HASHING_ALGORITHM = "MD5";
    private static final int POSITIVE_NUMBER_SIGN = 1;
    private static final int TOKEN_RADIX = 16;
    private final String value;

    @VisibleForTesting
    public ETagToken(@Nonnull byte[] responseBody) {
        Objects.requireNonNull(responseBody, "The response body is mandatory to build the ETag token.");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] digestedBody = messageDigest.digest(responseBody);
            BigInteger token = new BigInteger(1, digestedBody);
            this.value = token.toString(16);
        }
        catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 cryptographic algorithm is not available.", exception);
        }
    }

    @Nonnull
    public String getValue() {
        return this.value;
    }

    @Nonnull
    public String getDoubleQuotedValue() {
        return String.format(DOUBLE_QUOTED_TEMPLATE, this.value);
    }
}

