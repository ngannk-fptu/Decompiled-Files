/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth.consumer;

public class OAuthConsumerException
extends RuntimeException {
    public OAuthConsumerException() {
    }

    public OAuthConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuthConsumerException(String message) {
        super(message);
    }

    public OAuthConsumerException(Throwable cause) {
        super(cause);
    }
}

