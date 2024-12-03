/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fx3.Fx3Client
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.featureflag;

import com.atlassian.fx3.Fx3Client;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureFlagClient {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagClient.class);
    private final Fx3Client fx3Client;

    public FeatureFlagClient(Fx3Client fx3Client) {
        this.fx3Client = fx3Client;
    }

    public void updateCloudId(String cloudId) {
        try {
            this.fx3Client.updateUserIdentifier(cloudId);
            this.fx3Client.performManualFetch();
        }
        catch (Exception e) {
            log.error("Error occurred while updating cloud id", (Throwable)e);
        }
    }

    public boolean isFeatureEnabled(String flagKey) {
        try {
            return this.fx3Client.getAllEnabledFlagsForUser().contains(flagKey);
        }
        catch (Exception e) {
            log.error("Error occurred while checking feature flag", (Throwable)e);
            return false;
        }
    }

    public List<String> getAllEnabledFeatureFlags() {
        try {
            Set result = this.fx3Client.getAllEnabledFlagsForUser();
            return new ArrayList<String>(result);
        }
        catch (Exception e) {
            log.error("Error occurred while getting all enabled feature flags", (Throwable)e);
            return new ArrayList<String>();
        }
    }
}

