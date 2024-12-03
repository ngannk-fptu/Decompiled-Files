/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.WillClose
 *  javax.persistence.PersistenceException
 *  liquibase.Contexts
 *  liquibase.LabelExpression
 *  liquibase.Liquibase
 *  liquibase.database.Database
 *  liquibase.database.DatabaseConnection
 *  liquibase.database.DatabaseFactory
 *  liquibase.database.jvm.JdbcConnection
 *  liquibase.exception.DatabaseException
 *  liquibase.exception.LiquibaseException
 *  liquibase.resource.ClassLoaderResourceAccessor
 *  liquibase.resource.ResourceAccessor
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.WillClose;
import javax.persistence.PersistenceException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;

class LiquibaseSchemaUpgrader {
    private static final String CHANGE_LOG_TABLE = "MIG_DB_CHANGELOG";
    private static final String CHANGE_LOG_LOCK_TABLE = "MIG_DB_CHANGELOG_LOCK";
    private static final Logger log = ContextLoggerFactory.getLogger(LiquibaseSchemaUpgrader.class);

    private LiquibaseSchemaUpgrader() {
        throw new IllegalStateException("LiquibaseSchemaUpgrader class");
    }

    static void upgrade(@WillClose Connection connection, String changeLogPath) {
        try (Connection conn = connection;){
            ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
            Database database = LiquibaseSchemaUpgrader.createDatabase(conn);
            log.info("starting Liquibase setup");
            try (Liquibase liquibase = new Liquibase(changeLogPath, (ResourceAccessor)resourceAccessor, database);){
                liquibase.update(new Contexts(), new LabelExpression());
                log.info("Liquibase initialization completed");
            }
        }
        catch (SQLException | LiquibaseException e) {
            throw new PersistenceException("Failed to update database schema", e);
        }
    }

    private static Database createDatabase(Connection c) throws DatabaseException {
        JdbcConnection liquibaseConnection = new JdbcConnection(c);
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation((DatabaseConnection)liquibaseConnection);
        database.setDatabaseChangeLogTableName(CHANGE_LOG_TABLE);
        database.setDatabaseChangeLogLockTableName(CHANGE_LOG_LOCK_TABLE);
        return database;
    }
}

