/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSSessionCredentials;

public class BasicSessionCredentials
implements AWSSessionCredentials {
    private final String awsAccessKey;
    private final String awsSecretKey;
    private final String sessionToken;

    public BasicSessionCredentials(String awsAccessKey, String awsSecretKey, String sessionToken) {
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.sessionToken = sessionToken;
    }

    @Override
    public String getAWSAccessKeyId() {
        return this.awsAccessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return this.awsSecretKey;
    }

    @Override
    public String getSessionToken() {
        return this.sessionToken;
    }
}

