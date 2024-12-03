/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 */
package com.atlassian.confluence.setup.dbcheck;

import com.atlassian.annotations.Internal;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.util.SQLUtils;
import com.google.common.collect.ImmutableSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@Internal
public class MySQLChecker {
    private static final Logger log = LoggerFactory.getLogger(MySQLChecker.class);
    private static final Set<String> MYSQL_CHARSETS = ImmutableSet.of((Object)"utf8", (Object)"utf8mb3", (Object)"utf8mb4");
    private final SingleConnectionProvider databaseHelper;

    public MySQLChecker(SingleConnectionProvider databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void verifyDatabaseSetup(Properties databaseProperties) throws BootstrapException {
        this.checkIsolationLevel(databaseProperties);
        this.checkStorageEngineType(databaseProperties);
        this.checkCharacterSet(databaseProperties);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkIsolationLevel(Properties databaseProperties) throws BootstrapException {
        ResultSet rs;
        Statement stmt;
        Connection conn;
        block4: {
            conn = null;
            stmt = null;
            rs = null;
            try {
                conn = this.databaseHelper.getConnection(databaseProperties);
                stmt = conn.createStatement();
                rs = stmt.executeQuery("show session variables where variable_name = 'tx_isolation' or variable_name = 'transaction_isolation'");
                rs.next();
                String isolationLevel = rs.getString(2);
                if ("READ-COMMITTED".equals(isolationLevel)) break block4;
                String message = "MySQL session isolation level '" + isolationLevel + "' is no longer supported. Session isolation level must be 'READ-COMMITTED'. See http://confluence.atlassian.com/x/GAtmDg";
                throw new BootstrapException(message);
            }
            catch (SQLException sqle) {
                try {
                    log.error("MySQL isolation level could not be read. Isolation level must be 'READ-COMMITTED'. See http://confluence.atlassian.com/x/GAtmDg");
                }
                catch (Throwable throwable) {
                    SQLUtils.closeResultSetQuietly(rs);
                    SQLUtils.closeStatementQuietly(stmt);
                    SQLUtils.closeConnectionQuietly(conn);
                    throw throwable;
                }
                SQLUtils.closeResultSetQuietly(rs);
                SQLUtils.closeStatementQuietly(stmt);
                SQLUtils.closeConnectionQuietly(conn);
            }
        }
        SQLUtils.closeResultSetQuietly(rs);
        SQLUtils.closeStatementQuietly(stmt);
        SQLUtils.closeConnectionQuietly(conn);
    }

    private static String getMySQLDatabaseName(JdbcTemplate template) {
        return (String)template.queryForObject("SELECT DATABASE()", String.class);
    }

    private void checkStorageEngineType(Properties databaseProperties) throws BootstrapException {
        try (Connection conn = this.databaseHelper.getConnection(databaseProperties);){
            JdbcTemplate template = new JdbcTemplate((DataSource)new SingleConnectionDataSource(conn, true));
            String dbName = MySQLChecker.getMySQLDatabaseName(template);
            String defaultEngine = Stream.of(this.getMySQLVariable(template, "storage_engine"), this.getMySQLVariable(template, "default_storage_engine")).filter(Optional::isPresent).map(Optional::get).findFirst().orElseThrow(() -> new BootstrapException("Your database Storage Engine could not be determined"));
            if (defaultEngine.equalsIgnoreCase("myisam")) {
                throw new BootstrapException("MyISAM is configured as the default storage engine in your database, which may result in data integrity issues. Please see http://confluence.atlassian.com/x/voTcDQ for more information.");
            }
            List engines = template.queryForList("SELECT distinct ENGINE FROM information_schema.TABLES where TABLE_SCHEMA = ?", String.class, new Object[]{dbName});
            for (String engine : engines) {
                if (engine == null || !engine.equalsIgnoreCase("myisam")) continue;
                throw new BootstrapException("At least one table in your database is using the MyISAM Storage Engine, which may result in data integrity issues. Please see http://confluence.atlassian.com/x/voTcDQ for more information.");
            }
        }
        catch (SQLException sqle) {
            log.error("Your database Storage Engine could not be determined: " + sqle.getMessage(), (Throwable)sqle);
        }
    }

    private Optional<String> getMySQLVariable(JdbcTemplate template, String name) {
        try {
            return (Optional)template.queryForObject("show variables like ?", (rs, rowNum) -> Optional.of(rs.getString("Value")), new Object[]{name});
        }
        catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private void checkCharacterSet(Properties databaseProperties) throws BootstrapException {
        try (Connection conn = this.databaseHelper.getConnection(databaseProperties);){
            JdbcTemplate template = new JdbcTemplate((DataSource)new SingleConnectionDataSource(conn, true));
            String dbName = MySQLChecker.getMySQLDatabaseName(template);
            String dbCharSet = Objects.requireNonNull((String)template.queryForObject("SELECT DEFAULT_CHARACTER_SET_NAME FROM information_schema.SCHEMATA\nWHERE SCHEMA_NAME = ?", String.class, new Object[]{dbName}));
            if (!MYSQL_CHARSETS.contains(dbCharSet.toLowerCase())) {
                log.warn("Your database uses non-recommended character set: " + dbCharSet + ". See https://confluence.atlassian.com/x/IrYC");
            }
            int tablesWithNonMatchingCharset = Objects.requireNonNull((Integer)template.queryForObject("SELECT COUNT(*) FROM information_schema.TABLES,\n                     information_schema.COLLATION_CHARACTER_SET_APPLICABILITY\nWHERE collation_name = table_collation\n  AND table_schema = ?\n  AND character_set_name != ?", Integer.TYPE, new Object[]{dbName, dbCharSet}));
            int columns2WithNonMatchingCharset = Objects.requireNonNull((Integer)template.queryForObject("SELECT COUNT(*) FROM information_schema.COLUMNS\nWHERE table_schema = ?\n  AND character_set_name != ?", Integer.TYPE, new Object[]{dbName, dbCharSet}));
            if (tablesWithNonMatchingCharset > 0 || columns2WithNonMatchingCharset > 0) {
                throw new BootstrapException("Detected tables with non-default character encoding. See https://confluence.atlassian.com/x/TABrFw");
            }
        }
        catch (SQLException | DataAccessException e) {
            log.error("MySQL character set could not be read: " + e.getMessage(), e);
        }
    }
}

