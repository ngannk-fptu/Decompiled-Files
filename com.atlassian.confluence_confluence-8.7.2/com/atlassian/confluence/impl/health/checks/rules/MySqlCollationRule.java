/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.johnson.event.Event
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractDatabaseCollationRule;
import com.atlassian.confluence.impl.util.db.SingleConnectionProvider;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.DatabaseCollationVerifier;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.johnson.event.Event;
import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class MySqlCollationRule
extends AbstractDatabaseCollationRule {
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/doc/database-setup-for-mysql-128747.html");

    public MySqlCollationRule(ErrorMessageProvider errorMessageProvider, DatabaseCollationVerifier databaseCollationVerifier, SingleConnectionProvider databaseHelper, HibernateConfig hibernateConfig, String[] supportedCollations) {
        super(errorMessageProvider, KB_URL, databaseCollationVerifier, databaseHelper, hibernateConfig, supportedCollations);
    }

    @Override
    protected String getCollationScript() {
        return "SELECT DEFAULT_COLLATION_NAME \nFROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ? AND DEFAULT_COLLATION_NAME NOT IN (" + StringUtils.repeat((String)"?", (String)",", (int)this.supportedCollations.length) + ")";
    }

    @Override
    protected Event getFailureEvent(String errorMessage) {
        return new Event(JohnsonEventType.DATABASE.eventType(), errorMessage, JohnsonEventLevel.ERROR.level());
    }
}

