/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.BaseAppCloudMigrationListener;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.PaginatedContainers;
import com.atlassian.migration.app.PaginatedMapping;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AppCloudMigrationGateway {
    public void registerListener(BaseAppCloudMigrationListener var1);

    public void deregisterListener(BaseAppCloudMigrationListener var1);

    public OutputStream createAppData(String var1);

    public OutputStream createAppData(String var1, String var2);

    public Map<String, Object> getCloudFeedback(String var1);

    public Optional<Map<String, Object>> getCloudFeedbackIfPresent(String var1);

    public PaginatedMapping getPaginatedMapping(String var1, String var2, int var3);

    public Map<String, String> getMappingById(String var1, String var2, Set<String> var3);

    public PaginatedContainers getPaginatedContainers(String var1, ContainerType var2, int var3);
}

