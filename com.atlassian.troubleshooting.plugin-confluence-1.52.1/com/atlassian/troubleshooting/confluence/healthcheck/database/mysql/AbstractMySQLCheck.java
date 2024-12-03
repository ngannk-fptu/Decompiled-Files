/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.confluence.healthcheck.database.mysql;

import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class AbstractMySQLCheck
implements SupportHealthCheck {
    protected static final String TEST_TABLE_NAME = "supporthealthchecks";
    private static final String DB_NAME_QUERY = "SELECT DATABASE() FROM DUAL;";
    private static final String DB_NAME_KEY = "DATABASE()";
    protected final DatabaseService databaseService;

    protected AbstractMySQLCheck(DatabaseService databaseService) {
        this.databaseService = Objects.requireNonNull(databaseService);
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Nonnull
    protected final ResultSet openAndExecuteQuery(Connection connection, String query) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement(query);
        return pStatement.executeQuery();
    }

    @Nonnull
    protected final ResultSet openAndExecuteQuery(Connection connection, String query, String param1, String[] param2) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement(query);
        pStatement.setString(1, param1);
        for (int index = 0; index < param2.length; ++index) {
            pStatement.setString(index + 2, param2[index]);
        }
        return pStatement.executeQuery();
    }

    protected final String getDatabaseName(Connection connection) throws SQLException {
        try (ResultSet resultSet = this.openAndExecuteQuery(connection, DB_NAME_QUERY);){
            if (resultSet.next()) {
                String string = resultSet.getString(DB_NAME_KEY);
                return string;
            }
        }
        return null;
    }

    protected final void createTable(Connection connection, String tableName) throws SQLException {
        String query = String.format("CREATE TABLE IF NOT EXISTS `%s`.%s (id VARCHAR(45));", this.getDatabaseName(connection), tableName);
        this.openAndExecute(connection, query);
    }

    protected final void dropTestTable(Connection connection) throws SQLException {
        String query = String.format("DROP TABLE IF EXISTS `%s`.%s;", this.getDatabaseName(connection), TEST_TABLE_NAME);
        this.openAndExecute(connection, query);
    }

    private void openAndExecute(Connection connection, String query) throws SQLException {
        try (PreparedStatement pStatement = connection.prepareStatement(query);){
            pStatement.execute();
        }
    }
}

