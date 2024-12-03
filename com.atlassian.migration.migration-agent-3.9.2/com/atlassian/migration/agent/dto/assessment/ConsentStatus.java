/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.dto.assessment;

import org.codehaus.jackson.annotate.JsonValue;

public enum ConsentStatus {
    CONSENT_GIVEN("ConsentGiven"),
    CONSENT_NOT_GIVEN("ConsentNotGiven"),
    CONSENT_OUTDATED("ConsentOutdated"),
    NO_MIGRATION_NEEDED("NoMigrationNeeded"),
    NO_MIGRATING_ALTERNATIVE("NoMigratingAlternative"),
    NO_AUTOMATED_MIGRATION_PATH("NoAutomatedMigrationPath"),
    SERVER_APP_OUTDATED("ServerAppOutdated");

    private final String statusName;

    private ConsentStatus(String statusName) {
        this.statusName = statusName;
    }

    @JsonValue
    public String getStatusName() {
        return this.statusName;
    }
}

