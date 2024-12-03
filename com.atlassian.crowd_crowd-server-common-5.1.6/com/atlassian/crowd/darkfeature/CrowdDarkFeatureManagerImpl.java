/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.sal.api.features.DarkFeatureManager
 */
package com.atlassian.crowd.darkfeature;

import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.sal.api.features.DarkFeatureManager;

public class CrowdDarkFeatureManagerImpl
implements CrowdDarkFeatureManager {
    private final DarkFeatureManager darkFeatureManager;

    public CrowdDarkFeatureManagerImpl(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    public boolean isAvatarsEnabled() {
        return this.isEnabledForAllUsers("browseprincipals.usedirectoryavatars");
    }

    public boolean isAzureADAdditionalRegionsEnabled() {
        return this.isEnabledForAllUsers("azure.ad.additional.regions");
    }

    public boolean isDeleteUserMembershipsBatchingEnabled() {
        return this.isEnabledForAllUsers("crowd.sync.delete.user.memberships.batching.enabled");
    }

    public boolean isNestedGroupsGroupMembershipChangesBatchedEnabled() {
        return this.isEnabledForAllUsers("crowd.sync.nested.groups.group.membership.changes.batching.enabled");
    }

    public boolean isEventTransformerDirectoryManagerCacheEnabled() {
        return this.isEnabledForAllUsers("crowd.event.transformer.directory.manager.cache");
    }

    private boolean isEnabledForAllUsers(String darkFeatureKey) {
        return this.darkFeatureManager.isEnabledForAllUsers(darkFeatureKey).orElse(false);
    }
}

