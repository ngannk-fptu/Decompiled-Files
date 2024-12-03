/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.basicauth.event;

import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;

public final class BasicAuthUpdatedEvent {
    private final BasicAuthConfig newBasicAuthConfig;
    private final BasicAuthConfig oldBasicAuthConfig;

    public BasicAuthUpdatedEvent(BasicAuthConfig oldBasicAuthConfig, BasicAuthConfig newBasicAuthConfig) {
        this.newBasicAuthConfig = newBasicAuthConfig;
        this.oldBasicAuthConfig = oldBasicAuthConfig;
    }

    public BasicAuthConfig getOldBasicAuthConfig() {
        return this.oldBasicAuthConfig;
    }

    public BasicAuthConfig getNewBasicAuthConfig() {
        return this.newBasicAuthConfig;
    }
}

