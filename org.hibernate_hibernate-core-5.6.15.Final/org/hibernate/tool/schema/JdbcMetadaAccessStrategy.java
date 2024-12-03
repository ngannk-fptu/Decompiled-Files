/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema;

import java.util.Map;
import org.hibernate.internal.util.config.ConfigurationHelper;

public enum JdbcMetadaAccessStrategy {
    INDIVIDUALLY("individually"),
    GROUPED("grouped");

    private final String strategy;

    private JdbcMetadaAccessStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String toString() {
        return this.strategy;
    }

    public static JdbcMetadaAccessStrategy interpretSetting(Map options) {
        if (options == null) {
            return JdbcMetadaAccessStrategy.interpretHbm2ddlSetting(null);
        }
        if (ConfigurationHelper.getBoolean("hibernate.synonyms", options, false)) {
            return INDIVIDUALLY;
        }
        return JdbcMetadaAccessStrategy.interpretHbm2ddlSetting(options.get("hibernate.hbm2ddl.jdbc_metadata_extraction_strategy"));
    }

    public static JdbcMetadaAccessStrategy interpretHbm2ddlSetting(Object value) {
        if (value == null) {
            return GROUPED;
        }
        String name = value.toString().trim();
        if (name.isEmpty() || JdbcMetadaAccessStrategy.GROUPED.strategy.equals(name)) {
            return GROUPED;
        }
        if (JdbcMetadaAccessStrategy.INDIVIDUALLY.strategy.equals(name)) {
            return INDIVIDUALLY;
        }
        throw new IllegalArgumentException("Unrecognized `hibernate.hbm2ddl.jdbc_metadata_extraction_strategy` value : `" + name + '`');
    }
}

