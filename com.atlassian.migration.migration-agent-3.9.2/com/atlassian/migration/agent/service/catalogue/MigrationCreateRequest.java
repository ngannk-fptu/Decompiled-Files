/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.CloudLocation;
import com.atlassian.migration.agent.service.catalogue.ServerLocation;
import java.util.Map;
import lombok.Generated;

public class MigrationCreateRequest {
    private final String name;
    private final ServerLocation source;
    private final CloudLocation destination;
    private final boolean testMigration;
    private final boolean forceReset;
    private final Map<String, Object> properties;
    private final String type = "S2C_MIGRATION";

    @Generated
    public MigrationCreateRequest(String name, ServerLocation source, CloudLocation destination, boolean testMigration, boolean forceReset, Map<String, Object> properties) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.testMigration = testMigration;
        this.forceReset = forceReset;
        this.properties = properties;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public ServerLocation getSource() {
        return this.source;
    }

    @Generated
    public CloudLocation getDestination() {
        return this.destination;
    }

    @Generated
    public boolean isTestMigration() {
        return this.testMigration;
    }

    @Generated
    public boolean isForceReset() {
        return this.forceReset;
    }

    @Generated
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Generated
    public String getType() {
        return this.type;
    }
}

