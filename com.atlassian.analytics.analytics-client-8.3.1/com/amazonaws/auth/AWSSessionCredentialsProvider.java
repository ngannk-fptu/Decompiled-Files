/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSSessionCredentials;

public interface AWSSessionCredentialsProvider
extends AWSCredentialsProvider {
    @Override
    public AWSSessionCredentials getCredentials();
}

