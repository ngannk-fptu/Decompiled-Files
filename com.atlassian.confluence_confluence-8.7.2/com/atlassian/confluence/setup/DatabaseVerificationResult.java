/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup;

import java.util.Objects;

public class DatabaseVerificationResult {
    private final String titleKey;
    private final String key;
    private final String[] parameters;

    public DatabaseVerificationResult(String titleKey, String key, String ... parameters) {
        this.titleKey = Objects.requireNonNull(titleKey);
        this.key = Objects.requireNonNull(key);
        this.parameters = (String[])Objects.requireNonNull(parameters).clone();
    }

    public String getKey() {
        return this.key;
    }

    public String getTitleKey() {
        return this.titleKey;
    }

    public String[] getParameters() {
        return (String[])this.parameters.clone();
    }
}

