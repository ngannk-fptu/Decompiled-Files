/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.json;

public class JsonSerializingException
extends RuntimeException {
    public JsonSerializingException(String message) {
        super(message);
    }

    public JsonSerializingException(String message, Throwable cause) {
        super(message, cause);
    }
}

