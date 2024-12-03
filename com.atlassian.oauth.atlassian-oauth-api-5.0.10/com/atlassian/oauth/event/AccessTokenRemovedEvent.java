/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNullableByDefault
 */
package com.atlassian.oauth.event;

import com.atlassian.oauth.event.TokenRemovedEvent;
import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
public class AccessTokenRemovedEvent
extends TokenRemovedEvent {
    @Deprecated
    public AccessTokenRemovedEvent(String username) {
        this(username, null);
    }

    public AccessTokenRemovedEvent(String username, String consumerKey) {
        super(username, consumerKey);
    }
}

