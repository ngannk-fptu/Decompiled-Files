/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.keymanager;

public class Key {
    private final String key;
    private final String version;
    private final Type type;

    public Key(String key, String version, Type type) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key cannot be empty or null");
        }
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("version cannot be empty or null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.key = key;
        this.version = version;
        this.type = type;
    }

    public String getKey() {
        return this.key;
    }

    public String getVersion() {
        return this.version;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        PRIVATE,
        PUBLIC;

    }
}

