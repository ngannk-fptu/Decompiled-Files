/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.sql.Driver;
import java.util.Objects;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.DataSourceFactory;
import net.java.ao.builder.UnloadableJdbcDriverException;
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

enum SupportedDatabase {
    MYSQL("jdbc:mysql", "com.mysql.jdbc.Driver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new MySQLDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password));
        }
    }
    ,
    MARIA_DB("jdbc:mariadb", "org.mariadb.jdbc.Driver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new MySQLDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password));
        }
    }
    ,
    DERBY_NETWORK("jdbc:derby://", "org.apache.derby.jdbc.ClientDriver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new ClientDerbyDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password));
        }
    }
    ,
    DERBY_EMBEDDED("jdbc:derby", "org.apache.derby.jdbc.EmbeddedDriver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new EmbeddedDerbyDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), uri);
        }
    }
    ,
    ORACLE("jdbc:oracle", "oracle.jdbc.OracleDriver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new OracleDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), schema);
        }
    }
    ,
    POSTGRESQL("jdbc:postgresql", "org.postgresql.Driver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new PostgreSQLDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), schema);
        }
    }
    ,
    MSSQL("jdbc:sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new SQLServerDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), schema);
        }
    }
    ,
    MSSQL_JTDS("jdbc:jtds:sqlserver", "net.sourceforge.jtds.jdbc.Driver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new SQLServerDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), schema);
        }
    }
    ,
    HSQLDB("jdbc:hsqldb", "org.hsqldb.jdbcDriver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new HSQLDatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), schema);
        }
    }
    ,
    H2_EMBEDDED("jdbc:h2", "org.h2.Driver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            return new H2DatabaseProvider(this.getDataSource(dataSourceFactory, uri, username, password), schema);
        }
    }
    ,
    NUODB("jdbc:com.nuodb", "com.nuodb.jdbc.Driver"){

        @Override
        public DatabaseProvider getDatabaseProvider(DataSourceFactory dataSourceFactory, String uri, String username, String password, String schema) {
            DisposableDataSource dataSource = this.getDataSource(dataSourceFactory, uri, username, password);
            return new NuoDBDatabaseProvider(NuoDBDisposableDataSourceHandler.newInstance(dataSource), schema);
        }
    };

    private final String uriPrefix;
    private final String driverClassName;

    private SupportedDatabase(String uriPrefix, String driverClassName) {
        this.uriPrefix = Objects.requireNonNull(uriPrefix, "uriPrefix can't be null");
        this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName can't be null");
    }

    public abstract DatabaseProvider getDatabaseProvider(DataSourceFactory var1, String var2, String var3, String var4, String var5);

    DisposableDataSource getDataSource(DataSourceFactory factory, String uri, String username, String password) {
        Class<? extends Driver> driverClass = this.checkDriverLoaded();
        return factory.getDataSource(driverClass, uri, username, password);
    }

    private boolean accept(String uri) {
        return Objects.requireNonNull(uri, "uri can't be null").trim().startsWith(this.uriPrefix);
    }

    private Class<? extends Driver> checkDriverLoaded() {
        try {
            return SupportedDatabase.getDriverClass(this.driverClassName);
        }
        catch (ClassNotFoundException e) {
            throw new UnloadableJdbcDriverException(this.driverClassName, e);
        }
    }

    public String toString() {
        return "Database with prefix " + this.uriPrefix + " and driver " + this.driverClassName;
    }

    static SupportedDatabase fromUri(String uri) {
        for (SupportedDatabase supported : SupportedDatabase.values()) {
            if (!supported.accept(uri)) continue;
            return supported;
        }
        throw new ActiveObjectsException("Could not resolve database for database connection URI <" + uri + ">, are you sure this database is supported");
    }

    private static Class<? extends Driver> getDriverClass(String driverClassName) throws ClassNotFoundException {
        return Class.forName(driverClassName);
    }
}

