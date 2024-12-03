/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSCredentials;

public interface AWSSessionCredentials
extends AWSCredentials {
    public String getSessionToken();
}

