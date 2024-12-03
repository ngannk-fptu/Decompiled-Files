/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.johnson.event.Event
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractDatabaseSetupRule;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlServerSetupRule
extends AbstractDatabaseSetupRule {
    private static final Logger log = LoggerFactory.getLogger(SqlServerSetupRule.class);
    private Connection connection;
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/display/CONFKB/Startup+check%3A+Microsoft+SQL+Server+database+isolation+level");
    @VisibleForTesting
    static final String FAILURE_MESSAGE_KEY = "johnson.message.sql.server.isolation.level.incorrect";

    public SqlServerSetupRule(ErrorMessageProvider errorMessageProvider, SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig) {
        super(errorMessageProvider, KB_URL, databaseHelper, hibernateConfig);
    }

    @Override
    protected Optional<String> doValidation() {
        try {
            this.createDatabaseConnection();
            if (!this.isolationSetCorrectly()) {
                return Optional.of(this.getErrorMessage(FAILURE_MESSAGE_KEY, new Object[0]));
            }
        }
        catch (SQLException e) {
            log.info("Verification failed because of: {0}", (Object)e.getMessage(), (Object)e);
        }
        return Optional.empty();
    }

    private void createDatabaseConnection() throws SQLException {
        this.connection = this.databaseHelper.getConnection(this.hibernateConfig.getHibernateProperties());
    }

    private boolean isolationSetCorrectly() throws SQLException {
        return this.checkSnapshot(this.getReadCommittedSnapshot());
    }

    private Boolean checkSnapshot(String snapshotOn) {
        return snapshotOn.equals("1");
    }

    private String getReadCommittedSnapshot() throws SQLException {
        try (PreparedStatement stmt = this.connection.prepareStatement("SELECT is_read_committed_snapshot_on FROM sys.databases WHERE name = ?");){
            stmt.setString(1, this.getDatabaseName());
            String string = this.doQuery(stmt);
            return string;
        }
    }

    private String getDatabaseName() throws SQLException {
        try (PreparedStatement stmt = this.connection.prepareStatement("SELECT DB_NAME() AS DataBaseName");){
            String string = this.doQuery(stmt);
            return string;
        }
    }

    private String doQuery(PreparedStatement statement) throws SQLException {
        try (ResultSet rs = statement.executeQuery();){
            rs.next();
            String string = rs.getString(1);
            return string;
        }
    }

    @Override
    protected Event getFailureEvent(String errorMessage) {
        return new Event(JohnsonEventType.DATABASE.eventType(), errorMessage, JohnsonEventLevel.FATAL.level());
    }
}

