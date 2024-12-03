/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.exception;

import java.net.ProtocolException;

public class MalformedRequestException
extends ProtocolException {
    public MalformedRequestException() {
    }

    public MalformedRequestException(String message) {
        super(message);
    }
}

