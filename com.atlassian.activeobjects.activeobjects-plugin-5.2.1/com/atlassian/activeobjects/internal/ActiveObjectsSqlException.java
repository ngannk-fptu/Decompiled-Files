/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import net.java.ao.ActiveObjectsException;
import net.java.ao.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ActiveObjectsSqlException
extends ActiveObjectsException {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Database database;
    private Driver driver;

    public ActiveObjectsSqlException(EntityManager entityManager, SQLException cause) {
        super(cause);
        if (!ActiveObjectsSqlException.isConnectionException(cause)) {
            this.getInformation(entityManager);
        }
    }

    public SQLException getSqlException() {
        return (SQLException)this.getCause();
    }

    @Override
    public String getMessage() {
        return "There was a SQL exception thrown by the Active Objects library:\n" + this.database + "\n" + this.driver + "\n\n" + super.getMessage();
    }

    private void getInformation(EntityManager entityManager) {
        try (Connection connection = entityManager.getProvider().getConnection();){
            if (connection != null && !connection.isClosed()) {
                DatabaseMetaData metaData = connection.getMetaData();
                this.database = ActiveObjectsSqlException.getDatabase(metaData);
                this.driver = ActiveObjectsSqlException.getDriver(metaData);
            }
        }
        catch (SQLException e) {
            this.logger.debug("Could not load database connection meta data", (Throwable)e);
            this.addSuppressed(e);
        }
    }

    private static Database getDatabase(DatabaseMetaData metaData) throws SQLException {
        return new Database(metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion(), String.valueOf(metaData.getDatabaseMinorVersion()), String.valueOf(metaData.getDatabaseMajorVersion()));
    }

    private static Driver getDriver(DatabaseMetaData metaData) throws SQLException {
        return new Driver(metaData.getDriverName(), metaData.getDriverVersion());
    }

    private static boolean isConnectionException(SQLException e) {
        return e instanceof SQLNonTransientConnectionException || e instanceof SQLTransientConnectionException;
    }

    private static final class Driver {
        private final String name;
        private final String version;

        Driver(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String toString() {
            return "Driver:\n\t- name:" + this.name + "\n\t- version:" + this.version;
        }
    }

    private static final class Database {
        private final String name;
        private final String version;
        private final String minorVersion;
        private final String majorVersion;

        Database(String name, String version, String minorVersion, String majorVersion) {
            this.name = name;
            this.version = version;
            this.minorVersion = minorVersion;
            this.majorVersion = majorVersion;
        }

        public String toString() {
            return "Database:\n\t- name:" + this.name + "\n\t- version:" + this.version + "\n\t- minor version:" + this.minorVersion + "\n\t- major version:" + this.majorVersion;
        }
    }
}

