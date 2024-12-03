/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.github.gquintana.metrics.proxy.ProxyFactory;
import com.github.gquintana.metrics.proxy.ReflectProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.MetricNamingStrategy;
import java.sql.Connection;
import javax.sql.DataSource;

public class MetricsSql {
    public static Builder withMetricNamingStrategy(MetricNamingStrategy namingStrategy) {
        return new Builder(namingStrategy);
    }

    public static class Builder {
        private final MetricNamingStrategy namingStrategy;
        private ProxyFactory proxyFactory = new ReflectProxyFactory();
        private JdbcProxyFactory jdbcProxyFactory;

        public Builder(MetricNamingStrategy namingStrategy) {
            this.namingStrategy = namingStrategy;
        }

        public Builder withProxyFactory(ProxyFactory proxyFactory) {
            this.proxyFactory = proxyFactory;
            return this;
        }

        public JdbcProxyFactory build() {
            if (this.jdbcProxyFactory == null) {
                this.jdbcProxyFactory = new JdbcProxyFactory(this.namingStrategy, this.proxyFactory);
            }
            return this.jdbcProxyFactory;
        }

        public DataSource wrap(String databaseName, DataSource dataSource) {
            return this.build().wrapDataSource(databaseName, dataSource);
        }

        public Connection wrap(String databaseName, Connection connection) {
            return this.build().wrapConnection(databaseName, connection);
        }
    }
}

