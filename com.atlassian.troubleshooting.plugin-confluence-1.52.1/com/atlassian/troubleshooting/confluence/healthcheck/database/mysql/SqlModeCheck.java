/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.database.mysql;

import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.AbstractMySQLCheck;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlModeCheck
extends AbstractMySQLCheck {
    @VisibleForTesting
    static final String GLOBAL_VARS = "SHOW GLOBAL VARIABLES LIKE 'sql_mode';";
    @VisibleForTesting
    static final String SESSION_VARS = "SHOW VARIABLES LIKE 'sql_mode';";
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    SqlModeCheck(DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        super(databaseService);
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public SupportHealthStatus check() {
        return this.databaseService.runInConnection(connection -> {
            try {
                if (!this.isValidSqlMode((Connection)connection, GLOBAL_VARS) || !this.isValidSqlMode((Connection)connection, SESSION_VARS)) {
                    return this.supportHealthStatusBuilder.critical(this, "confluence.healthcheck.mysql.sqlmode.fail", new Serializable[0]);
                }
                return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.mysql.sqlmode.valid", new Serializable[0]);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean isValidSqlMode(Connection connection, String sql) throws SQLException {
        try (ResultSet resultSet = this.openAndExecuteQuery(connection, sql);){
            if (resultSet.next()) {
                String sqlMode = resultSet.getString("VALUE");
                boolean bl = sqlMode == null || !sqlMode.contains("NO_AUTO_VALUE_ON_ZERO");
                return bl;
            }
            boolean bl = true;
            return bl;
        }
    }
}

