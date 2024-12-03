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
import com.atlassian.confluence.impl.health.checks.rules.AbstractHealthCheckRule;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import java.net.URL;
import java.util.Objects;

public abstract class AbstractDatabaseSetupRule
extends AbstractHealthCheckRule {
    @VisibleForTesting
    static final String FAILURE_CAUSE = "db-setup-incorrect-configuration";
    protected final SingleConnectionProvider databaseHelper;
    protected final HibernateConfig hibernateConfig;

    AbstractDatabaseSetupRule(ErrorMessageProvider errorMessageProvider, URL kbUrl, SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig) {
        super(errorMessageProvider, kbUrl, FAILURE_CAUSE, JohnsonEventType.DATABASE);
        this.databaseHelper = Objects.requireNonNull(databaseHelper);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
    }
}

