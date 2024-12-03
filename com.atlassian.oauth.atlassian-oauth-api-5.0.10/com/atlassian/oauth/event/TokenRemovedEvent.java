/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNullableByDefault
 */
package com.atlassian.oauth.event;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
public abstract class TokenRemovedEvent {
    private final String username;
    private final String consumerKey;

    protected TokenRemovedEvent(String username, String consumerKey) {
        this.username = username;
        this.consumerKey = consumerKey;
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    @Nullable
    public String getConsumerKey() {
        return this.consumerKey;
    }
}

