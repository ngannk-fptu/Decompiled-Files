/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentials;

public class AnonymousAWSCredentials
implements AWSCredentials {
    @Override
    public String getAWSAccessKeyId() {
        return null;
    }

    @Override
    public String getAWSSecretKey() {
        return null;
    }
}

