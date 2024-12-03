/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.MigrationLocation;
import com.atlassian.migration.agent.service.catalogue.MigrationLocationType;

public class CloudLocation
extends MigrationLocation {
    private final String cloudId;
    private static final MigrationLocationType TYPE = MigrationLocationType.cloud;

    public CloudLocation(String cloudId, String url) {
        super(TYPE, url);
        this.cloudId = cloudId;
    }

    public String getCloudId() {
        return this.cloudId;
    }
}

