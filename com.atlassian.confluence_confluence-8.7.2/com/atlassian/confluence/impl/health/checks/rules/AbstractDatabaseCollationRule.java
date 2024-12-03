/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractHealthCheckRule;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.DatabaseCollationVerifier;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDatabaseCollationRule
extends AbstractHealthCheckRule {
    private static final Logger log = LoggerFactory.getLogger(AbstractDatabaseCollationRule.class);
    @VisibleForTesting
    static final String FAILURE_CAUSE = "db-collation-incorrect-configuration";
    @VisibleForTesting
    static final String FAILURE_MESSAGE_KEY = "johnson.message.minimum.not.satisfied.db.collation";
    @VisibleForTesting
    static final String DOC_LINK = "https://confluence.atlassian.com/x/IYRdL";
    private final DatabaseCollationVerifier databaseCollationVerifier;
    private final SingleConnectionProvider databaseHelper;
    private final HibernateConfig hibernateConfig;
    protected final String[] supportedCollations;

    protected AbstractDatabaseCollationRule(ErrorMessageProvider errorMessageProvider, URL kbUrl, DatabaseCollationVerifier databaseCollationVerifier, SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig, String[] supportedCollations) {
        super(errorMessageProvider, kbUrl, FAILURE_CAUSE, JohnsonEventType.DATABASE);
        this.databaseCollationVerifier = Objects.requireNonNull(databaseCollationVerifier);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
        this.supportedCollations = Objects.requireNonNull(supportedCollations);
    }

    @Override
    protected Optional<String> doValidation() {
        Optional<String> optional;
        block8: {
            Connection connection = this.databaseHelper.getConnection(this.hibernateConfig.getHibernateProperties());
            try {
                optional = this.databaseCollationVerifier.verifyCollationOfDatabase(connection, this.getCollationScript(), this.supportedCollations, DOC_LINK).map(result -> this.getErrorMessage(this.getFailureMessageKey(), result.getParameters()));
                if (connection == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (SQLException e) {
                    log.info(String.format("verification failed because of: [%s]", e.getMessage()), (Throwable)e);
                    return Optional.empty();
                }
            }
            connection.close();
        }
        return optional;
    }

    protected abstract String getCollationScript();

    protected String getFailureMessageKey() {
        return FAILURE_MESSAGE_KEY;
    }
}

