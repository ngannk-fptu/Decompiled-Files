/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.config.db.HibernateConfig;

public interface HibernateDatabaseCapabilities {
    public boolean isPostgreSql();

    public boolean isHSQL();

    public boolean isH2();

    public boolean isOracle();

    public boolean isSqlServer();

    public boolean isMySql();

    public boolean uniqueAllowsAnyNullValues();

    public boolean uniqueAllowsMultipleNullValues();

    default public boolean supportsIdentityColumns() {
        return this.isHSQL() || this.isMySql() || this.isSqlServer() || this.isH2();
    }

    default public boolean supportsSequences() {
        return this.isOracle() || this.isPostgreSql();
    }

    public static HibernateDatabaseCapabilities from(HibernateConfig config) {
        if (config instanceof HibernateDatabaseCapabilities) {
            return (HibernateDatabaseCapabilities)config;
        }
        return new HibernateConfigAdapter(config);
    }

    public static class HibernateConfigAdapter
    implements HibernateDatabaseCapabilities {
        private final HibernateConfig config;

        public HibernateConfigAdapter(HibernateConfig config) {
            this.config = config;
        }

        @Override
        public boolean isPostgreSql() {
            return this.config.isPostgreSql();
        }

        @Override
        public boolean isHSQL() {
            return this.config.isHSQL();
        }

        @Override
        public boolean isH2() {
            return this.config.isH2();
        }

        @Override
        public boolean isOracle() {
            return this.config.isOracle();
        }

        @Override
        public boolean isSqlServer() {
            return this.config.isSqlServer();
        }

        @Override
        public boolean isMySql() {
            return this.config.isMySql();
        }

        @Override
        public boolean uniqueAllowsAnyNullValues() {
            return this.config.uniqueAllowsAnyNullValues();
        }

        @Override
        public boolean uniqueAllowsMultipleNullValues() {
            return this.config.uniqueAllowsMultipleNullValues();
        }
    }
}

