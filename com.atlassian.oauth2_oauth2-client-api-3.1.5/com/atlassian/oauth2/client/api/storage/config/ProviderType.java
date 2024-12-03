/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.client.api.storage.config;

import java.util.Arrays;
import java.util.Optional;

public enum ProviderType {
    GOOGLE("google"),
    MICROSOFT("microsoft"),
    GENERIC("generic");

    public final String key;

    private ProviderType(String key) {
        this.key = key;
    }

    public static Optional<ProviderType> get(String key) {
        return Arrays.stream(ProviderType.values()).filter(type -> type.key.equalsIgnoreCase(key)).findAny();
    }

    public static ProviderType getOrThrow(String key) {
        return ProviderType.get(key).orElseThrow(() -> new IllegalArgumentException("Cannot find a provider type with key: " + key));
    }

    public String getKey() {
        return this.key;
    }
}

