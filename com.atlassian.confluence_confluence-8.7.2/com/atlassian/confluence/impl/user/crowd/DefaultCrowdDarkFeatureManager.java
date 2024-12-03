/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;

public class DefaultCrowdDarkFeatureManager
implements CrowdDarkFeatureManager {
    private DarkFeaturesManager darkFeaturesManager;

    public DefaultCrowdDarkFeatureManager(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    public boolean isAvatarsEnabled() {
        return this.isEnabled("browseprincipals.usedirectoryavatars");
    }

    public boolean isAzureADAdditionalRegionsEnabled() {
        return this.isEnabled("azure.ad.additional.regions");
    }

    public boolean isDeleteUserMembershipsBatchingEnabled() {
        return this.isEnabled("crowd.sync.delete.user.memberships.batching.enabled");
    }

    public boolean isNestedGroupsGroupMembershipChangesBatchedEnabled() {
        return this.isEnabled("crowd.sync.nested.groups.group.membership.changes.batching.enabled");
    }

    public boolean isEventTransformerDirectoryManagerCacheEnabled() {
        return this.isEnabled("crowd.event.transformer.directory.manager.cache");
    }

    private boolean isEnabled(String crowdDarkFeature) {
        return this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(crowdDarkFeature);
    }
}

