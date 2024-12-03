/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

public class HealthCheckModuleDescriptorNotFoundException
extends Exception {
    private final String key;

    public HealthCheckModuleDescriptorNotFoundException(String key) {
        this.key = key;
    }

    public HealthCheckModuleDescriptorNotFoundException(String key, Exception e) {
        super(e);
        this.key = key;
    }

    public String getUnfoundKey() {
        return this.key;
    }
}

