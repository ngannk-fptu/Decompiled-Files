/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.analytics;

public interface NodeIdProvider {
    public static final String NOT_CLUSTERED = "NOT_CLUSTERED";

    public String getNodeId();
}

