/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.embedded;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurerFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.embedded.SimpleDriverDataSourceFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class EmbeddedDatabaseFactory {
    public static final String DEFAULT_DATABASE_NAME = "testdb";
    private static final Log logger = LogFactory.getLog(EmbeddedDatabaseFactory.class);
    private boolean generateUniqueDatabaseName = false;
    private String databaseName = "testdb";
    private DataSourceFactory dataSourceFactory = new SimpleDriverDataSourceFactory();
    @Nullable
    private EmbeddedDatabaseConfigurer databaseConfigurer;
    @Nullable
    private DatabasePopulator databasePopulator;
    @Nullable
    private DataSource dataSource;

    public void setGenerateUniqueDatabaseName(boolean generateUniqueDatabaseName) {
        this.generateUniqueDatabaseName = generateUniqueDatabaseName;
    }

    public void setDatabaseName(String databaseName) {
        Assert.hasText((String)databaseName, (String)"Database name is required");
        this.databaseName = databaseName;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        Assert.notNull((Object)dataSourceFactory, (String)"DataSourceFactory is required");
        this.dataSourceFactory = dataSourceFactory;
    }

    public void setDatabaseType(EmbeddedDatabaseType type) {
        this.databaseConfigurer = EmbeddedDatabaseConfigurerFactory.getConfigurer(type);
    }

    public void setDatabaseConfigurer(EmbeddedDatabaseConfigurer configurer) {
        this.databaseConfigurer = configurer;
    }

    public void setDatabasePopulator(DatabasePopulator populator) {
        this.databasePopulator = populator;
    }

    public EmbeddedDatabase getDatabase() {
        if (this.dataSource == null) {
            this.initDatabase();
        }
        return new EmbeddedDataSourceProxy(this.dataSource);
    }

    protected void initDatabase() {
        if (this.generateUniqueDatabaseName) {
            this.setDatabaseName(UUID.randomUUID().toString());
        }
        if (this.databaseConfigurer == null) {
            this.databaseConfigurer = EmbeddedDatabaseConfigurerFactory.getConfigurer(EmbeddedDatabaseType.HSQL);
        }
        this.databaseConfigurer.configureConnectionProperties(this.dataSourceFactory.getConnectionProperties(), this.databaseName);
        this.dataSource = this.dataSourceFactory.getDataSource();
        if (logger.isInfoEnabled()) {
            if (this.dataSource instanceof SimpleDriverDataSource) {
                SimpleDriverDataSource simpleDriverDataSource = (SimpleDriverDataSource)this.dataSource;
                logger.info((Object)String.format("Starting embedded database: url='%s', username='%s'", simpleDriverDataSource.getUrl(), simpleDriverDataSource.getUsername()));
            } else {
                logger.info((Object)String.format("Starting embedded database '%s'", this.databaseName));
            }
        }
        if (this.databasePopulator != null) {
            try {
                DatabasePopulatorUtils.execute(this.databasePopulator, this.dataSource);
            }
            catch (RuntimeException ex) {
                this.shutdownDatabase();
                throw ex;
            }
        }
    }

    protected void shutdownDatabase() {
        if (this.dataSource != null) {
            if (logger.isInfoEnabled()) {
                if (this.dataSource instanceof SimpleDriverDataSource) {
                    logger.info((Object)String.format("Shutting down embedded database: url='%s'", ((SimpleDriverDataSource)this.dataSource).getUrl()));
                } else {
                    logger.info((Object)String.format("Shutting down embedded database '%s'", this.databaseName));
                }
            }
            if (this.databaseConfigurer != null) {
                this.databaseConfigurer.shutdown(this.dataSource, this.databaseName);
            }
            this.dataSource = null;
        }
    }

    @Nullable
    protected final DataSource getDataSource() {
        return this.dataSource;
    }

    private class EmbeddedDataSourceProxy
    implements EmbeddedDatabase {
        private final DataSource dataSource;

        public EmbeddedDataSourceProxy(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return this.dataSource.getConnection();
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return this.dataSource.getConnection(username, password);
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return this.dataSource.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            this.dataSource.setLogWriter(out);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return this.dataSource.getLoginTimeout();
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            this.dataSource.setLoginTimeout(seconds);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return this.dataSource.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return this.dataSource.isWrapperFor(iface);
        }

        @Override
        public Logger getParentLogger() {
            return Logger.getLogger("global");
        }

        @Override
        public void shutdown() {
            EmbeddedDatabaseFactory.this.shutdownDatabase();
        }
    }
}

