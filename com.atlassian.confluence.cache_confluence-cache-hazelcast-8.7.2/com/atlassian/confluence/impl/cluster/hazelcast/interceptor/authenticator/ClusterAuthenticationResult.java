/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import javax.annotation.Nonnull;

public class ClusterAuthenticationResult {
    private final boolean successful;
    private final String message;

    public ClusterAuthenticationResult(boolean successful, @Nonnull String message) {
        this.successful = successful;
        this.message = message;
    }

    public ClusterAuthenticationResult(boolean successful) {
        this.successful = successful;
        this.message = "";
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    @Nonnull
    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "ClusterAuthenticationResult{successful=" + this.successful + ", message='" + this.message + "'}";
    }
}

