/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.annotations.Internal;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.DatabaseConnectionException;
import com.atlassian.confluence.setup.DatabaseEnum;
import com.atlassian.confluence.setup.DatabaseVerificationResult;
import com.atlassian.confluence.setup.DatabaseVerifier;
import com.atlassian.confluence.setup.DatabaseVerifyException;
import com.google.common.collect.ImmutableSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class DefaultDatabaseVerifier
implements DatabaseVerifier {
    private static final Logger log = LoggerFactory.getLogger(DefaultDatabaseVerifier.class);
    private static final String[] MS_SQLSERVER_SUPPORTED_COLLATIONS = new String[]{"SQL_Latin1_General_CP1_CS_AS"};
    private static final String[] MYSQL_SUPPORTED_COLLATIONS = new String[]{"utf8_bin", "utf8mb3_bin", "utf8mb4_bin"};
    private static final String POSTGRESQL_CHARSET = "utf8";
    private static final Set<String> MYSQL_CHARSETS = ImmutableSet.of((Object)"utf8", (Object)"utf8mb3", (Object)"utf8mb4");
    private static final String MYSQL_STORAGE_ENGINE = "InnoDB";
    private static final String ORACLE_CHARSET = "AL32UTF8";
    private static final String[] ORACLE_ROLES = new String[]{"CONNECT", "RESOURCE"};
    private static final String[] ORACLE_USER_PERMISSIONS = new String[]{"CREATE TABLE", "CREATE SEQUENCE", "CREATE TRIGGER"};
    private static final String ORACLE_SELECT_ANY_TABLE = "SELECT ANY TABLE";
    private BootstrapManager bootstrapManager;

    public BootstrapManager getBootstrapManager() {
        return this.bootstrapManager;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public void verifyDatabase(String databaseType, Connection connection) throws SQLException, DatabaseVerifyException {
        this.verify(databaseType, connection);
    }

    @Override
    public void verifyDatasource(String databaseType, String datasourceName) throws SQLException, DatabaseVerifyException {
        try (Connection connection = this.bootstrapManager.getTestDatasourceConnection(datasourceName);){
            this.verify(databaseType, connection);
        }
        catch (BootstrapException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException)e.getCause();
            }
            throw new DatabaseConnectionException(e, "setup.database.test.connection.failed.generic", new String[0]);
        }
    }

    @Override
    public void verifyDatabaseDetails(String databaseType, DatabaseDetails databaseDetails) throws SQLException, DatabaseVerifyException {
        try (Connection connection = this.bootstrapManager.getTestDatabaseConnection(databaseDetails);){
            this.verify(databaseType, connection);
        }
        catch (BootstrapException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException)e.getCause();
            }
            throw new DatabaseConnectionException(e, "setup.database.test.connection.failed.generic", new String[0]);
        }
    }

    @Override
    public Optional<DatabaseVerificationResult> verifyCollationOfDatabase(Connection connection, String script, String[] supportedCollations, String doc) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(script);){
            statement.setString(1, connection.getCatalog());
            for (int index = 0; index < supportedCollations.length; ++index) {
                statement.setString(index + 2, supportedCollations[index]);
            }
            try (ResultSet rs = statement.executeQuery();){
                if (rs.next()) {
                    String collation = rs.getString(1);
                    log.error("The collation of database is not set correctly!");
                    Optional<DatabaseVerificationResult> optional = Optional.of(new DatabaseVerificationResult("setup.database.test.connection.db.collation.error", "setup.database.test.connection.db.collation", collation, StringUtils.join((Object[])supportedCollations, (String)", "), doc));
                    return optional;
                }
            }
        }
        return Optional.empty();
    }

    private void verify(String databaseType, Connection connection) throws SQLException, DatabaseVerifyException {
        if (databaseType.equals(DatabaseEnum.MYSQL.getType())) {
            String mySQLDatabaseName = this.getDatabaseName(connection, "SELECT DATABASE()");
            this.verifyCharsetForMYSQL(connection, mySQLDatabaseName);
            this.verifyCollationForMYSQL(connection);
            this.verifyStorageEngineForMYSQL(connection);
            this.verifyIsolationLevelForMySQL(connection);
        }
        if (databaseType.equals(DatabaseEnum.MSSQL.getType())) {
            this.verifyCollationForMSSQL(connection);
            this.verifyIsolationLevelForMSSQL(connection);
        }
        if (databaseType.equals(DatabaseEnum.POSTGRESQL.getType())) {
            String postgreSQLDatabaseName = this.getDatabaseName(connection, "SELECT current_database()");
            this.verifyCharsetForPostgreSQL(connection, postgreSQLDatabaseName);
        }
        if (databaseType.equals(DatabaseEnum.ORACLE.getType())) {
            this.verifyCharsetForOracle(connection);
            this.verifyUserHasRightPermissionsInOracle(connection);
        }
    }

    @Deprecated
    private void verifyUserHasRoleInOracle(Connection connection) throws SQLException, DatabaseVerifyException {
        String getRoleSQL = "select GRANTED_ROLE from USER_ROLE_PRIVS";
        ArrayList<String> grantedRoles = new ArrayList<String>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select GRANTED_ROLE from USER_ROLE_PRIVS");){
            while (resultSet.next()) {
                grantedRoles.add(resultSet.getString(1).toUpperCase());
            }
        }
        for (String role : ORACLE_ROLES) {
            if (grantedRoles.contains(role)) continue;
            log.error("The user is not granted with the role: " + role);
            throw new DatabaseVerifyException("setup.database.test.connection.db.oracle.permission.error", "setup.database.test.connection.db.oracle.role.error", role);
        }
    }

    private void verifyUserHasRightPermissionsInOracle(Connection connection) throws SQLException, DatabaseVerifyException {
        String getPermissionSQL = "SELECT PRIVILEGE FROM SESSION_PRIVS";
        ArrayList<String> grantedPermissions = new ArrayList<String>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT PRIVILEGE FROM SESSION_PRIVS");){
            while (resultSet.next()) {
                grantedPermissions.add(resultSet.getString(1).toUpperCase());
            }
        }
        for (String permission : ORACLE_USER_PERMISSIONS) {
            if (grantedPermissions.contains(permission)) continue;
            log.error("The user is not granted with the permission: " + permission);
            throw new DatabaseVerifyException("setup.database.test.connection.db.oracle.permission.error", "setup.database.test.connection.db.oracle.lack.privilege.error", permission);
        }
        if (grantedPermissions.contains(ORACLE_SELECT_ANY_TABLE)) {
            log.error("The user has been granted the privilege of SELECT ANY TABLE");
            throw new DatabaseVerifyException("setup.database.test.connection.db.oracle.permission.error", "setup.database.test.connection.db.oracle.select.any.table.error", new String[0]);
        }
    }

    private String getDatabaseName(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);){
            String string;
            block12: {
                ResultSet resultSet = statement.executeQuery();
                try {
                    resultSet.next();
                    string = resultSet.getString(1);
                    if (resultSet == null) break block12;
                }
                catch (Throwable throwable) {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                resultSet.close();
            }
            return string;
        }
    }

    private void verifyStorageEngineForMYSQL(Connection connection) throws SQLException, DatabaseVerifyException {
        log.debug("verify the storage engine for mysql");
        Optional<String> defaultStorageEngine = this.loadStorageEngineForMYSQL(connection, "default_storage_engine");
        Optional<String> storageEngine = this.loadStorageEngineForMYSQL(connection, "storage_engine");
        if (Stream.of(defaultStorageEngine, storageEngine).filter(Optional::isPresent).count() == 0L) {
            log.error("Could not determine the storage engine for mysql");
            throw new DatabaseVerifyException("setup.database.test.connection.db.mysql.storage.engine.error", "setup.database.test.connection.db.mysql.storage.engine", new String[0]);
        }
        if (!Stream.of(defaultStorageEngine, storageEngine).filter(Optional::isPresent).map(Optional::get).allMatch(MYSQL_STORAGE_ENGINE::equals)) {
            log.error("The storage engine of mysql database is not innodb");
            throw new DatabaseVerifyException("setup.database.test.connection.db.mysql.storage.engine.error", "setup.database.test.connection.db.mysql.storage.engine", new String[0]);
        }
    }

    private void verifyIsolationLevelForMySQL(Connection connection) throws SQLException, DatabaseVerifyException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("show session variables where variable_name = 'tx_isolation' or variable_name = 'transaction_isolation'");){
            rs.next();
            String isolationLevel = rs.getString(2);
            if (!"READ-COMMITTED".equals(isolationLevel)) {
                log.error(String.format("The isolation level of database is not set correctly: %s ", isolationLevel));
                throw new DatabaseVerifyException("setup.database.test.connection.db.mysql.isolation.level.error", "setup.database.test.connection.db.mysql.isolation.level", new String[0]);
            }
        }
    }

    private Optional<String> loadStorageEngineForMYSQL(Connection connection, String variableName) throws SQLException {
        log.debug("verify the storage engine for mysql");
        String checkStorageEngine = "show variables like ?";
        try (PreparedStatement statement = connection.prepareStatement("show variables like ?");){
            Optional<String> optional;
            block16: {
                ResultSet resultSet;
                block14: {
                    Optional<String> optional2;
                    block15: {
                        statement.setString(1, variableName);
                        resultSet = statement.executeQuery();
                        try {
                            if (!resultSet.next()) break block14;
                            optional2 = Optional.of(resultSet.getString("value"));
                            if (resultSet == null) break block15;
                        }
                        catch (Throwable throwable) {
                            if (resultSet != null) {
                                try {
                                    resultSet.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        resultSet.close();
                    }
                    return optional2;
                }
                optional = Optional.empty();
                if (resultSet == null) break block16;
                resultSet.close();
            }
            return optional;
        }
    }

    private void verifyCharsetForPostgreSQL(Connection connection, String databaseName) throws SQLException, DatabaseVerifyException {
        block14: {
            log.debug("verify the charset setting for postgresql");
            String checkCharsetSql = "SELECT pg_encoding_to_char(encoding) FROM pg_database WHERE datname = ?";
            try (PreparedStatement statement = connection.prepareStatement("SELECT pg_encoding_to_char(encoding) FROM pg_database WHERE datname = ?");){
                statement.setString(1, databaseName);
                try (ResultSet resultSet = statement.executeQuery();){
                    if (resultSet.next()) {
                        String charset = resultSet.getString(1);
                        if (!POSTGRESQL_CHARSET.equalsIgnoreCase(charset)) {
                            log.error(String.format("The charset of postgresql database %s is %s", databaseName, charset));
                            throw new DatabaseVerifyException("setup.database.test.connection.db.postgresql.charset.error", "setup.database.test.connection.db.postgresql.charset", new String[0]);
                        }
                        break block14;
                    }
                    log.error(String.format("Could not find the charset for the database: %s", databaseName));
                    throw new DatabaseVerifyException("setup.database.test.connection.db.postgresql.charset.error", "setup.database.test.connection.db.postgresql.charset", new String[0]);
                }
            }
        }
    }

    private void verifyCharsetForOracle(Connection connection) throws SQLException, DatabaseVerifyException {
        block14: {
            log.debug("verify the charset setting for oracle");
            String checkCharsetSql = "select value from nls_database_parameters where parameter = ?";
            try (PreparedStatement statement = connection.prepareStatement("select value from nls_database_parameters where parameter = ?");){
                statement.setString(1, "NLS_CHARACTERSET");
                try (ResultSet resultSet = statement.executeQuery();){
                    if (resultSet.next()) {
                        String charset = resultSet.getString(1);
                        if (!ORACLE_CHARSET.equalsIgnoreCase(charset)) {
                            log.error(String.format("The charset of oracle database is %s", charset));
                            throw new DatabaseVerifyException("setup.database.test.connection.db.oracle.charset.error", "setup.database.test.connection.db.oracle.charset", new String[0]);
                        }
                        break block14;
                    }
                    log.error(String.format("Could not find the charset for the database", new Object[0]));
                    throw new DatabaseVerifyException("setup.database.test.connection.db.oracle.charset.error", "setup.database.test.connection.db.oracle.charset", new String[0]);
                }
            }
        }
    }

    private void verifyCharsetForMYSQL(Connection connection, String databaseName) throws SQLException, DatabaseVerifyException {
        block14: {
            log.debug("verify the charset setting for mysql");
            String checkCharsetSql = "SELECT DEFAULT_CHARACTER_SET_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?";
            try (PreparedStatement statement = connection.prepareStatement("SELECT DEFAULT_CHARACTER_SET_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?");){
                statement.setString(1, databaseName);
                try (ResultSet resultSet = statement.executeQuery();){
                    if (resultSet.next()) {
                        String charset = resultSet.getString(1);
                        if (!MYSQL_CHARSETS.contains(StringUtils.lowerCase((String)charset))) {
                            log.error(String.format("The charset of mysql database %s is %s", databaseName, charset));
                            throw new DatabaseVerifyException("setup.database.test.connection.db.mysql.charset.error", "setup.database.test.connection.db.mysql.charset", new String[0]);
                        }
                        break block14;
                    }
                    log.error(String.format("Could not find the charset for the database: %s", databaseName));
                    throw new DatabaseVerifyException("setup.database.test.connection.db.mysql.charset.error", "setup.database.test.connection.db.mysql.charset", new String[0]);
                }
            }
        }
    }

    private void verifyCollationForMSSQL(Connection connection) throws SQLException, DatabaseVerifyException {
        log.debug("verify the collation setting for mssql");
        String checkDatabase = "SELECT collation_name FROM sys.databases\nWHERE name = ? \nAND collation_name NOT IN (" + StringUtils.repeat((String)"?", (String)",", (int)MS_SQLSERVER_SUPPORTED_COLLATIONS.length) + ")";
        Optional<DatabaseVerificationResult> verificationResult = this.verifyCollationOfDatabase(connection, checkDatabase, MS_SQLSERVER_SUPPORTED_COLLATIONS, "https://confluence.atlassian.com/x/IYRdL");
        if (verificationResult.isPresent()) {
            DatabaseVerificationResult result = verificationResult.get();
            throw new DatabaseVerifyException(result.getTitleKey(), result.getKey(), result.getParameters());
        }
    }

    private void verifyCollationForMYSQL(Connection connection) throws SQLException, DatabaseVerifyException {
        log.debug("verify the collation setting for mysql");
        String checkDatabase = "SELECT DEFAULT_COLLATION_NAME \nFROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ? AND DEFAULT_COLLATION_NAME NOT IN (" + StringUtils.repeat((String)"?", (String)",", (int)MYSQL_SUPPORTED_COLLATIONS.length) + ")";
        Optional<DatabaseVerificationResult> verificationResult = this.verifyCollationOfDatabase(connection, checkDatabase, MYSQL_SUPPORTED_COLLATIONS, "https://confluence.atlassian.com/x/UAL_Jw");
        if (verificationResult.isPresent()) {
            DatabaseVerificationResult result = verificationResult.get();
            throw new DatabaseVerifyException(result.getTitleKey(), result.getKey(), result.getParameters());
        }
    }

    private void verifyIsolationLevelForMSSQL(Connection connection) throws SQLException, DatabaseVerifyException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DB_NAME() AS DataBaseName");){
            rs.next();
            String dbName = rs.getString(1);
            try (PreparedStatement pstm = connection.prepareStatement("SELECT is_read_committed_snapshot_on FROM sys.databases WHERE name = ?");){
                pstm.setString(1, dbName);
                try (ResultSet rs1 = pstm.executeQuery();){
                    rs1.next();
                    String snapshotOn = rs1.getString(1);
                    if (!"1".equals(snapshotOn)) {
                        log.error("The isolation level of database is not set correctly");
                        throw new DatabaseVerifyException("setup.database.test.connection.db.sqlserver.isolation.level.error", "setup.database.test.connection.db.sqlserver.isolation.level", new String[0]);
                    }
                }
            }
        }
    }
}

