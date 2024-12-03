/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.CloudLocation;
import com.atlassian.migration.agent.service.catalogue.ServerLocation;
import lombok.Generated;

public class MigrationScopeCreateRequest {
    private final ServerLocation source;
    private final CloudLocation destination;

    @Generated
    public MigrationScopeCreateRequest(ServerLocation source, CloudLocation destination) {
        this.source = source;
        this.destination = destination;
    }

    @Generated
    public ServerLocation getSource() {
        return this.source;
    }

    @Generated
    public CloudLocation getDestination() {
        return this.destination;
    }
}

