/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.pats.db;

import com.atlassian.pats.db.NotificationState;
import com.atlassian.pocketknife.api.querydsl.configuration.ConfigurationEnrichment;
import com.atlassian.pocketknife.spi.querydsl.configuration.ConfigurationEnricher;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.types.EnumByNameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class PersonalTokenConfigEnricher
implements ConfigurationEnricher,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(PersonalTokenConfigEnricher.class);
    private final ConfigurationEnrichment configurationEnrichment;

    public PersonalTokenConfigEnricher(ConfigurationEnrichment configurationEnrichment) {
        this.configurationEnrichment = configurationEnrichment;
    }

    @Override
    public Configuration enrich(Configuration configuration) {
        log.debug("Registering enum types for Querydsl");
        configuration.register(new EnumByNameType<NotificationState>(NotificationState.class));
        return configuration;
    }

    public void afterPropertiesSet() throws Exception {
        this.configurationEnrichment.setEnricher(this);
    }
}

