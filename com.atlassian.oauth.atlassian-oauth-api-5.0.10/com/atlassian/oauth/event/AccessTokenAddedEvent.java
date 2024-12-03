/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNullableByDefault
 */
package com.atlassian.oauth.event;

import com.atlassian.oauth.event.TokenAddedEvent;
import javax.annotation.ParametersAreNullableByDefault;

@ParametersAreNullableByDefault
public class AccessTokenAddedEvent
extends TokenAddedEvent {
    public AccessTokenAddedEvent(String username, String consumerKey) {
        super(username, consumerKey);
    }
}

