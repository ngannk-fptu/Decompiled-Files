/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractDatabaseCollationRule;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.setup.DatabaseCollationVerifier;
import com.atlassian.confluence.web.UrlBuilder;
import java.net.URL;
import java.util.Arrays;

public class PostgresCollationRule
extends AbstractDatabaseCollationRule {
    @VisibleForTesting
    static final String PSQL_COLLATION_FAILURE_MESSAGE_KEY = "johnson.message.minimum.not.satisfied.db.postgres.collation";
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/doc/database-setup-for-postgresql-173244522.html");

    public PostgresCollationRule(ErrorMessageProvider errorMessageProvider, DatabaseCollationVerifier databaseCollationVerifier, SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig, String[] supportedCollations) {
        super(errorMessageProvider, KB_URL, databaseCollationVerifier, databaseHelper, hibernateConfig, supportedCollations);
    }

    @Override
    protected String getCollationScript() {
        StringBuilder query = new StringBuilder().append("SELECT datcollate \n").append("FROM pg_database WHERE datname = ? \n");
        Arrays.stream(this.supportedCollations).forEach(col -> query.append(" AND datcollate NOT ILIKE ?"));
        query.append(';');
        return query.toString();
    }

    @Override
    protected String getFailureMessageKey() {
        return PSQL_COLLATION_FAILURE_MESSAGE_KEY;
    }
}

