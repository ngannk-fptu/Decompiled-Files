/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.DataSourceConfiguration;
import com.atlassian.confluence.impl.health.checks.rules.AbstractHealthCheckRule;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.web.UrlBuilder;
import java.net.URL;
import java.util.Optional;

public final class DataSourceSetupRule
extends AbstractHealthCheckRule {
    private final DataSourceConfiguration dataSourceConfiguration;
    static final String FAILURE_CAUSE = "db-setup-jndi-datasource";
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/display/CONFKB/Startup+check%3A+JNDI+Data+Source");
    static final String FAILURE_MESSAGE_KEY = "johnson.message.database.jndi-datasource";

    public DataSourceSetupRule(ErrorMessageProvider errorMessageProvider, DataSourceConfiguration dataSourceConfiguration) {
        super(errorMessageProvider, KB_URL, FAILURE_CAUSE, JohnsonEventType.DATABASE);
        this.dataSourceConfiguration = dataSourceConfiguration;
    }

    @Override
    protected Optional<String> doValidation() {
        if (this.dataSourceConfiguration.isDataSourceConfigured()) {
            return Optional.of(this.getErrorMessage(FAILURE_MESSAGE_KEY, new Object[0]));
        }
        return Optional.empty();
    }
}

