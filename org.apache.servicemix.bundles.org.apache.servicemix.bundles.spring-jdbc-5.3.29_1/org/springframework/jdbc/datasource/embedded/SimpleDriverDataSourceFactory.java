/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.embedded;

import java.sql.Driver;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;

final class SimpleDriverDataSourceFactory
implements DataSourceFactory {
    private final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    SimpleDriverDataSourceFactory() {
    }

    @Override
    public ConnectionProperties getConnectionProperties() {
        return new ConnectionProperties(){

            @Override
            public void setDriverClass(Class<? extends Driver> driverClass) {
                SimpleDriverDataSourceFactory.this.dataSource.setDriverClass(driverClass);
            }

            @Override
            public void setUrl(String url) {
                SimpleDriverDataSourceFactory.this.dataSource.setUrl(url);
            }

            @Override
            public void setUsername(String username) {
                SimpleDriverDataSourceFactory.this.dataSource.setUsername(username);
            }

            @Override
            public void setPassword(String password) {
                SimpleDriverDataSourceFactory.this.dataSource.setPassword(password);
            }
        };
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}

