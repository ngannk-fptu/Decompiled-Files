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
public class RequestTokenAddedEvent
extends TokenAddedEvent {
    public RequestTokenAddedEvent(String username, String consumerKey) {
        super(username, consumerKey);
    }
}

