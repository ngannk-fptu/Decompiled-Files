/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentials;

public interface AWSCredentialsProvider {
    public AWSCredentials getCredentials();

    public void refresh();
}

