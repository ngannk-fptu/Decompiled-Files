/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.auth.AWSSessionCredentials;

public interface AWSRefreshableSessionCredentials
extends AWSSessionCredentials {
    public void refreshCredentials();
}

