/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentials;

public class BasicAWSCredentials
implements AWSCredentials {
    private final String accessKey;
    private final String secretKey;

    public BasicAWSCredentials(String accessKey, String secretKey) {
        if (accessKey == null) {
            throw new IllegalArgumentException("Access key cannot be null.");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key cannot be null.");
        }
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public String getAWSAccessKeyId() {
        return this.accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return this.secretKey;
    }
}

