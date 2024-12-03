/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.MigrationLocation;
import com.atlassian.migration.agent.service.catalogue.MigrationLocationType;
import java.util.Map;

public class ServerLocation
extends MigrationLocation {
    private final String serverId;
    private final Map<String, String> sens;
    private static final MigrationLocationType TYPE = MigrationLocationType.server;

    public ServerLocation(String url, String serverId, Map<String, String> sens) {
        super(TYPE, url);
        this.serverId = serverId;
        this.sens = sens;
    }

    public String getServerId() {
        return this.serverId;
    }

    public Map<String, String> getSens() {
        return this.sens;
    }
}

