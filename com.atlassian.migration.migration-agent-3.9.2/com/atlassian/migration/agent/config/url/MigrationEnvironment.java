/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.config.url;

public enum MigrationEnvironment {
    FEDRAMP("fedramp-environment.properties"),
    DEFAULT("default-environment.properties");

    private final String fileName;

    private MigrationEnvironment(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}

