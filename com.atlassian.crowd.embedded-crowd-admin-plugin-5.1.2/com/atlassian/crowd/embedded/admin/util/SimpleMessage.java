/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.crowd.embedded.admin.util;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;

public final class SimpleMessage
implements Message {
    private final String key;
    private final Serializable[] arguments;

    public static Message instance(String key, Serializable ... arguments) {
        return new SimpleMessage(key, arguments);
    }

    private SimpleMessage(String key, Serializable ... arguments) {
        this.key = key;
        this.arguments = arguments;
    }

    public String getKey() {
        return this.key;
    }

    public Serializable[] getArguments() {
        return this.arguments;
    }
}

