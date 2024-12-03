/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.client.api.lib.flow;

import javax.annotation.Nonnull;

public interface FlowRequest {
    @Nonnull
    public String getId();

    @Nonnull
    public String getInitFlowUrl();
}

