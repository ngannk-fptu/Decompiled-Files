/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import java.util.Map;

public class UserMappingsManager {
    private final boolean isGlobalEmailFixesEnabled;
    private final Map<String, String> mappings;

    public UserMappingsManager(MigrationDarkFeaturesManager migrationDarkFeaturesManager, Map<String, String> mappings) {
        this.isGlobalEmailFixesEnabled = migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes();
        this.mappings = mappings;
    }

    public UserMappingsManager(MigrationDarkFeaturesManager migrationDarkFeaturesManager, EnterpriseGatekeeperClient enterpriseGatekeeperClient, String cloudId, String migrationScopeId) {
        this.isGlobalEmailFixesEnabled = migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes();
        String prefix = this.isGlobalEmailFixesEnabled ? "confluence.userkey/" : "email/";
        this.mappings = enterpriseGatekeeperClient.getMappings(cloudId, migrationScopeId, "identity:user", prefix);
    }

    public Map<String, String> getMappings() {
        return this.mappings;
    }

    public String getAaid(String userkey, String email, String defaultValue) {
        if (this.isGlobalEmailFixesEnabled) {
            return this.mappings.getOrDefault(userkey, defaultValue);
        }
        return this.mappings.getOrDefault(email, defaultValue);
    }
}

