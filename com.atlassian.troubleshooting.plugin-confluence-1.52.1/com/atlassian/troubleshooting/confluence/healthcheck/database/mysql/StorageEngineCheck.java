/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.database.mysql;

import com.atlassian.troubleshooting.api.healthcheck.DatabaseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.AbstractMySQLCheck;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class StorageEngineCheck
extends AbstractMySQLCheck {
    private static final String DATABASE_ENGINE_QUERY = "SELECT ENGINE FROM information_schema.TABLES where TABLE_SCHEMA = '%s' and TABLE_NAME = '%s';";
    private static final String TABLE_NAME = "storageenginetest";
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    StorageEngineCheck(DatabaseService databaseService, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        super(databaseService);
        this.supportHealthStatusBuilder = Objects.requireNonNull(supportHealthStatusBuilder);
    }

    @Override
    public SupportHealthStatus check() {
        return this.databaseService.runInConnection(connection -> {
            try {
                try {
                    this.createTable((Connection)connection, TABLE_NAME);
                    Optional<String> databaseEngine = this.getDatabaseEngine((Connection)connection);
                    if (!databaseEngine.isPresent()) {
                        SupportHealthStatus supportHealthStatus = this.supportHealthStatusBuilder.critical(this, "confluence.healthcheck.database.query.no.results", new Serializable[0]);
                        return supportHealthStatus;
                    }
                    SupportHealthStatus supportHealthStatus = "InnoDB".equals(databaseEngine.get()) ? this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.mysql.storage.engine.valid", new Serializable[0]) : this.supportHealthStatusBuilder.critical(this, "confluence.healthcheck.mysql.storage.engine.fail", new Serializable[0]);
                    return supportHealthStatus;
                }
                finally {
                    this.dropTestTable((Connection)connection);
                }
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private Optional<String> getDatabaseEngine(Connection connection) throws SQLException {
        String storageEngineQuery = String.format(DATABASE_ENGINE_QUERY, this.getDatabaseName(connection), TABLE_NAME);
        try (ResultSet resultSet = this.openAndExecuteQuery(connection, storageEngineQuery);){
            if (resultSet.next()) {
                Optional<String> optional = Optional.ofNullable(resultSet.getString("ENGINE"));
                return optional;
            }
            Optional<String> optional = Optional.empty();
            return optional;
        }
    }
}

