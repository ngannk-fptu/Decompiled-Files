/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.owner;

public enum OwnerType {
    TYPE_ANONYMOUS("anon"),
    TYPE_GROUP("group"),
    TYPE_USER("user");

    private String key;

    private OwnerType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

