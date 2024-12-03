/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsException
 */
package com.atlassian.streams.internal;

import com.atlassian.streams.api.StreamsException;

public class RemotePostReplyException
extends StreamsException {
    private final int statusCode;

    public RemotePostReplyException(int statusCode, String statusText) {
        super(statusText);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}

