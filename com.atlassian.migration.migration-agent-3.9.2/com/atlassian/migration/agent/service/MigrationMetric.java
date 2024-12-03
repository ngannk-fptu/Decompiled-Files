/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import lombok.Generated;

public enum MigrationMetric {
    MIGRATION_PLAN_COUNT("migrations.sli.migration.count"),
    MIGRATION_ERRORS("migrations.sli.errors"),
    SPACE_MIGRATION_COUNT("migrations.sli.space.migration"),
    USERS_MIGRATION_COUNT("migrations.sli.users.migration"),
    GLOBAL_ENTITIES_MIGRATION_COUNT("migrations.sli.global-entities.migration"),
    PREFLIGHT_CHECK_METRIC("migrations.sli.preflight.check"),
    MIGRATION_STEP("migrations.sli.migration.step"),
    PREFLIGHT_CHECK_METRIC_TIMER("migrations.sli.preflight.check.timer"),
    MIGRATIONS_OVERALL_SUCCESS_RATE("migrations.sr.confluence.s2c.migration"),
    MIGRATIONS_COMPONENT_LEVEL_SUCCESS_RATE("migrations.sr.confluence.s2c.operation"),
    MAPI_ATTACH_JOB_TIMER_METRIC_EVENT_NAME("migrations.mapi.confluence.s2c.attachjob.timer"),
    MAPI_EXECUTE_CHECKS_JOB_TIMER_METRIC_EVENT_NAME("migrations.mapi.confluence.s2c.executeChecks.timer"),
    MAPI_EXECUTE_CHECKS_AND_MIGRATE_JOB_TIMER_METRIC_EVENT_NAME("migrations.mapi.confluence.s2c.executeChecksAndMigrate.timer");

    public final String metricName;

    @Generated
    public String getMetricName() {
        return this.metricName;
    }

    @Generated
    private MigrationMetric(String metricName) {
        this.metricName = metricName;
    }
}

