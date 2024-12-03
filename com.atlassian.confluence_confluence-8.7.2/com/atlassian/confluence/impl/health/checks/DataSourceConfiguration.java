/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health.checks;

import java.util.Optional;

public interface DataSourceConfiguration {
    default public boolean isDataSourceConfigured() {
        return this.getJdbcUrl().isPresent();
    }

    public Optional<String> getJdbcUrl();

    public Optional<Integer> getPoolSize();
}

