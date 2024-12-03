/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  javax.annotation.Nonnull
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ao.ConverterUtils;
import com.atlassian.activeobjects.internal.DatabaseProviderFactory;
import com.atlassian.activeobjects.internal.DatabaseProviderNotFoundException;
import com.atlassian.activeobjects.internal.DriverNameExtractor;
import com.atlassian.activeobjects.spi.DatabaseType;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.DelegatingDisposableDataSourceHandler;
import net.java.ao.db.ClientDerbyDatabaseProvider;
import net.java.ao.db.EmbeddedDerbyDatabaseProvider;
import net.java.ao.db.H2DatabaseProvider;
import net.java.ao.db.HSQLDatabaseProvider;
import net.java.ao.db.MySQLDatabaseProvider;
import net.java.ao.db.NuoDBDatabaseProvider;
import net.java.ao.db.NuoDBDisposableDataSourceHandler;
import net.java.ao.db.OracleDatabaseProvider;
import net.java.ao.db.PostgreSQLDatabaseProvider;
import net.java.ao.db.SQLServerDatabaseProvider;

public final class JdbcDriverDatabaseProviderFactory
implements DatabaseProviderFactory {
    private final DriverNameExtractor driverNameExtractor;

    public JdbcDriverDatabaseProviderFactory(DriverNameExtractor driverNameExtractor) {
        this.driverNameExtractor = Objects.requireNonNull(driverNameExtractor);
    }

    @Override
    @Nonnull
    public DatabaseProvider getDatabaseProvider(DataSource dataSource, DatabaseType databaseType, String schema) {
        String driverName = this.getDriverName(dataSource);
        for (DatabaseProviderFactoryEnum dbProviderFactory : DatabaseProviderFactoryEnum.values()) {
            if (!dbProviderFactory.accept(databaseType, driverName)) continue;
            return dbProviderFactory.getDatabaseProvider(dataSource, schema);
        }
        throw new DatabaseProviderNotFoundException(driverName);
    }

    private String getDriverName(DataSource dataSource) {
        return this.driverNameExtractor.getDriverName(dataSource);
    }

    private static DisposableDataSource getDisposableDataSource(DataSource dataSource) {
        return DelegatingDisposableDataSourceHandler.newInstance(dataSource, () -> {});
    }

    private static enum DatabaseProviderFactoryEnum {
        MYSQL(DatabaseType.MYSQL, "mysql", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new MySQLDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource));
            }
        }
        ,
        DERBY_NETWORK(DatabaseType.DERBY_NETWORK, "derby", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new ClientDerbyDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource));
            }
        }
        ,
        DERBY_EMBEDDED(DatabaseType.DERBY_EMBEDDED, "derby", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new EmbeddedDerbyDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), "a-fake-uri");
            }
        }
        ,
        ORACLE(DatabaseType.ORACLE, "oracle", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new OracleDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), schema);
            }
        }
        ,
        POSTGRESQL(DatabaseType.POSTGRESQL, "postgres", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new PostgreSQLDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), schema);
            }
        }
        ,
        MSSQL(DatabaseType.MS_SQL, "microsoft", true){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new SQLServerDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), schema);
            }
        }
        ,
        MSSQL_JTDS(DatabaseType.MS_SQL, "jtds", true){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new SQLServerDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), schema);
            }
        }
        ,
        HSQLDB(DatabaseType.HSQL, "hsql", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new HSQLDatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), schema);
            }
        }
        ,
        H2(DatabaseType.H2, "h2", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new H2DatabaseProvider(JdbcDriverDatabaseProviderFactory.getDisposableDataSource(dataSource), schema);
            }
        }
        ,
        NUODB(DatabaseType.NUODB, "nuodb", false){

            @Override
            public DatabaseProvider getDatabaseProvider(DataSource dataSource, String schema) {
                return new NuoDBDatabaseProvider(NuoDBDisposableDataSourceHandler.newInstance(dataSource), schema);
            }
        };

        private final DatabaseType databaseType;
        private final String driverName;
        private final boolean needsDatabaseTypeAndDriverName;

        private DatabaseProviderFactoryEnum(DatabaseType databaseType, String driverName, boolean needsDatabaseTypeAndDriverName) {
            this.databaseType = Objects.requireNonNull(databaseType);
            this.driverName = Objects.requireNonNull(driverName);
            this.needsDatabaseTypeAndDriverName = needsDatabaseTypeAndDriverName;
        }

        boolean accept(DatabaseType otherDatabaseType, String otherDriverName) {
            boolean acceptDatabaseType = this.acceptDatabaseType(otherDatabaseType);
            boolean acceptDriverName = this.acceptDriverName(otherDriverName);
            if (this.needsDatabaseTypeAndDriverName) {
                return acceptDatabaseType && acceptDriverName;
            }
            return acceptDatabaseType || acceptDriverName;
        }

        private boolean acceptDatabaseType(DatabaseType otherDatabaseType) {
            return this.databaseType.equals((Object)otherDatabaseType);
        }

        private boolean acceptDriverName(String otherDriverName) {
            return otherDriverName != null && ConverterUtils.toLowerCase(otherDriverName).contains(this.driverName);
        }

        public abstract DatabaseProvider getDatabaseProvider(DataSource var1, String var2);
    }
}

