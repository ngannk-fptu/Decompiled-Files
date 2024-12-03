/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.consumer;

import com.atlassian.oauth.consumer.OAuthConsumerException;

public class OAuthConsumerNotFoundException
extends OAuthConsumerException {
    public OAuthConsumerNotFoundException(String message) {
        super(message);
    }
}

