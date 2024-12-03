/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.tenancy.api.TenantAccessor
 *  com.atlassian.tenancy.api.helper.PerTenantInitialiser
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.schema;

import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.atlassian.confluence.core.persistence.schema.api.SchemaComparison;
import com.atlassian.confluence.core.persistence.schema.api.SchemaComparisonService;
import com.atlassian.confluence.core.persistence.schema.event.SchemaInconsistencyWarningEvent;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.helper.PerTenantInitialiser;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupSchemaChecker {
    private static final Logger log = LoggerFactory.getLogger(StartupSchemaChecker.class);
    private final SchemaComparisonService schemaComparisonService;
    private final EventPublisher eventPublisher;
    private final VersionHistoryDao versionHistoryDao;
    private final PerTenantInitialiser perTenantInitialiser;

    public StartupSchemaChecker(SchemaComparisonService schemaComparisonService, EventPublisher eventPublisher, VersionHistoryDao versionHistoryDao, TenantAccessor tenantAccessor) {
        this.versionHistoryDao = (VersionHistoryDao)Preconditions.checkNotNull((Object)versionHistoryDao);
        this.schemaComparisonService = (SchemaComparisonService)Preconditions.checkNotNull((Object)schemaComparisonService);
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.perTenantInitialiser = new PerTenantInitialiser(eventPublisher, tenantAccessor, this::checkSchemaIfBuildNumbersMatch);
    }

    @PostConstruct
    public void runTenantInitialiser() {
        this.perTenantInitialiser.init();
    }

    @PreDestroy
    public void destroyTenantInitialiser() {
        this.perTenantInitialiser.destroy();
    }

    @VisibleForTesting
    void checkSchemaIfBuildNumbersMatch() {
        if (this.doDatabaseAndAppBuildNumbersMatch()) {
            this.checkSchema();
        } else {
            log.debug("Database build number {} doesn't match application build number {}, so no meaningful schema comparison can be performed", (Object)this.versionHistoryDao.getLatestBuildNumber(), (Object)BuildInformation.INSTANCE.getBuildNumber());
        }
    }

    private void checkSchema() {
        log.debug("Verifying consistency between expected and actual database schemata");
        try {
            SchemaComparison schemaComparison = this.schemaComparisonService.compareExpectedWithActualSchema();
            this.handleWarnings(schemaComparison.getWarnings());
        }
        catch (Exception ex) {
            log.error("Failed to verify schema consistency", (Throwable)ex);
        }
    }

    private boolean doDatabaseAndAppBuildNumbersMatch() {
        String databaseBuildNumber = String.valueOf(this.versionHistoryDao.getLatestBuildNumber());
        return Objects.equals(BuildInformation.INSTANCE.getBuildNumber(), databaseBuildNumber);
    }

    private void handleWarnings(Collection<String> warnings) {
        if (warnings.isEmpty()) {
            log.info("Database schema consistency checks out OK");
        } else {
            log.warn("Database schema is inconsistent with expectations. {} potential issues found, see below for details.", (Object)warnings.size());
            for (String warning : warnings) {
                log.warn(warning);
            }
            this.eventPublisher.publish((Object)new SchemaInconsistencyWarningEvent(warnings.size()));
        }
    }
}

