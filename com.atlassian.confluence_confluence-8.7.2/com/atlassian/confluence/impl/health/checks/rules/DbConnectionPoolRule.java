/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractHealthCheckRule;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.util.db.DatabaseConfigHelper;
import com.atlassian.confluence.web.UrlBuilder;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class DbConnectionPoolRule
extends AbstractHealthCheckRule {
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/confkb/startup-check-database-connection-pool-size-960713815.html");
    @VisibleForTesting
    static final String FAILURE_CAUSE = "db-connection-pool-incorrect-configuration";
    @VisibleForTesting
    static final String UNCONFIGURED_FAILURE_MESSAGE_KEY = "johnson.message.unconfigured.db.pool.size";
    @VisibleForTesting
    static final String MINIMUM_NOT_SATISFIED_FAILURE_MESSAGE_KEY = "johnson.message.minimum.not.satisfied.db.pool.size";
    @VisibleForTesting
    static final int RECOMMENDED_MINIMUM_CONNECTION_POOL_SIZE = 60;
    private final DatabaseConfigHelper databaseConfigHelper;

    public DbConnectionPoolRule(DatabaseConfigHelper databaseConfigHelper, ErrorMessageProvider errorMessageProvider) {
        super(errorMessageProvider, KB_URL, FAILURE_CAUSE, JohnsonEventType.DATABASE);
        this.databaseConfigHelper = Objects.requireNonNull(databaseConfigHelper);
    }

    @Override
    protected Optional<String> doValidation() {
        Optional<Integer> maybeDbConnectionPoolSize = this.databaseConfigHelper.getConnectionPoolSize();
        if (!maybeDbConnectionPoolSize.isPresent()) {
            return Optional.of(this.getErrorMessage(UNCONFIGURED_FAILURE_MESSAGE_KEY, 60));
        }
        return maybeDbConnectionPoolSize.filter(dbConnectionPoolSize -> dbConnectionPoolSize < 60).map(dbConnectionPoolSize -> this.getErrorMessage(MINIMUM_NOT_SATISFIED_FAILURE_MESSAGE_KEY, dbConnectionPoolSize, 60));
    }
}

