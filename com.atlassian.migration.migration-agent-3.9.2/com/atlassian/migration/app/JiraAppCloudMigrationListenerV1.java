/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppCloudMigrationListenerV1;
import com.atlassian.migration.app.ServerAppCustomField;
import java.util.HashMap;
import java.util.Map;

public interface JiraAppCloudMigrationListenerV1
extends AppCloudMigrationListenerV1 {
    default public Map<String, String> getSupportedWorkflowRuleMappings() {
        return new HashMap<String, String>();
    }

    default public Map<ServerAppCustomField, String> getSupportedCustomFieldMappings() {
        return new HashMap<ServerAppCustomField, String>();
    }
}

