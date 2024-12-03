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
public class RequestTokenRemovedEvent
extends TokenRemovedEvent {
    @Deprecated
    public RequestTokenRemovedEvent(String username) {
        this(username, null);
    }

    public RequestTokenRemovedEvent(String username, String consumerKey) {
        super(username, consumerKey);
    }
}

