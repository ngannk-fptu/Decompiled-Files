/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.db;

import com.atlassian.config.ApplicationConfiguration;
import java.util.Map;
import java.util.Properties;

public class HibernateConfig {
    public static final String HIBERNATE_SETUP = "hibernate.setup";
    public static final String HIBERNATE_CONFIG_PREFIX = "hibernate.";
    private static final String DIALECT_KEY = "hibernate.dialect";
    private ApplicationConfiguration applicationConfig;

    public static boolean isOracleDialect(String dialect) {
        return null != dialect && dialect.matches(".*Oracle.*Dialect$");
    }

    public static boolean isSqlServerDialect(String dialect) {
        return null != dialect && dialect.matches(".*?SQLServer.*?Dialect$");
    }

    public static boolean isDb2Dialect(String dialect) {
        return null != dialect && dialect.matches(".*?DB2.*?Dialect$");
    }

    public static boolean isH2Dialect(String dialect) {
        return null != dialect && dialect.matches(".*?H2.*?Dialect$");
    }

    public static boolean isHsqlDialect(String dialect) {
        return null != dialect && dialect.matches(".*?HSQL.*?Dialect$");
    }

    public static boolean isMySqlDialect(String dialect) {
        return null != dialect && dialect.matches(".*?MySQL.*?Dialect$");
    }

    public static boolean isPostgreSqlDialect(String dialect) {
        return null != dialect && dialect.matches(".*?PostgreSQL.*?Dialect$");
    }

    public ApplicationConfiguration getApplicationConfig() {
        return this.applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfig = applicationConfiguration;
    }

    public boolean isHibernateSetup() {
        return this.applicationConfig.getBooleanProperty(HIBERNATE_SETUP);
    }

    public Properties getHibernateProperties() {
        Properties props = new Properties();
        props.putAll((Map<?, ?>)this.applicationConfig.getPropertiesWithPrefix(HIBERNATE_CONFIG_PREFIX));
        return props;
    }

    public boolean isDb2() {
        return HibernateConfig.isDb2Dialect(this.getConfiguredDialect());
    }

    public boolean isSqlServer() {
        return HibernateConfig.isSqlServerDialect(this.getConfiguredDialect());
    }

    public boolean isPostgreSql() {
        return HibernateConfig.isPostgreSqlDialect(this.getConfiguredDialect());
    }

    public boolean isMySql() {
        return HibernateConfig.isMySqlDialect(this.getConfiguredDialect());
    }

    public boolean isOracle() {
        return HibernateConfig.isOracleDialect(this.getConfiguredDialect());
    }

    public boolean isHSQL() {
        return HibernateConfig.isHsqlDialect(this.getConfiguredDialect());
    }

    public boolean isH2() {
        return HibernateConfig.isH2Dialect(this.getConfiguredDialect());
    }

    public boolean booleanRequiresSubstitution() {
        return this.isOracle() || this.isSqlServer() || this.isDb2();
    }

    public boolean uniqueAllowsMultipleNullValues() {
        return !this.isOracle() && !this.isSqlServer();
    }

    public boolean uniqueAllowsAnyNullValues() {
        return !this.isDb2();
    }

    private String getConfiguredDialect() {
        return this.getConfiguredStringProperty(DIALECT_KEY);
    }

    private String getConfiguredStringProperty(String key) {
        return (String)this.getApplicationConfig().getProperty(key);
    }
}

