/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.i18n;

import java.util.Arrays;
import java.util.List;

public final class Message {
    private final String key;
    private final Object[] arguments;

    public static Message getInstance(String key) {
        return new Message(key, null);
    }

    public static Message getInstance(String key, Object ... arguments) {
        return new Message(key, arguments);
    }

    public static Message getInstance(String key, List arguments) {
        return new Message(key, arguments.toArray());
    }

    private Message(String key, Object[] arguments) {
        this.key = key;
        this.arguments = arguments;
    }

    public String getKey() {
        return this.key;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public boolean hasArguments() {
        return this.arguments != null;
    }

    public String toString() {
        return "Message [key: " + this.key + (String)(this.hasArguments() ? ", args: " + Arrays.asList(this.arguments) : "") + "]";
    }
}

