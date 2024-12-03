/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.darkfeature;

public interface CrowdDarkFeatureManager {
    public static final String AVATARS_DARK_FEATURE_KEY = "browseprincipals.usedirectoryavatars";
    public static final String AZURE_AD_ADDITIONAL_REGIONS = "azure.ad.additional.regions";
    public static final String DELETE_USER_MEMBERSHIPS_BATCHING_ENABLED = "crowd.sync.delete.user.memberships.batching.enabled";
    public static final String NESTED_GROUPS_GROUP_MEMBERSHIP_CHANGES_BATCHING_ENABLED = "crowd.sync.nested.groups.group.membership.changes.batching.enabled";
    public static final String EVENT_TRANSFORMER_DIRECTORY_MANAGER_CACHE_ENABLED = "crowd.event.transformer.directory.manager.cache";

    public boolean isAvatarsEnabled();

    public boolean isAzureADAdditionalRegionsEnabled();

    public boolean isDeleteUserMembershipsBatchingEnabled();

    public boolean isNestedGroupsGroupMembershipChangesBatchedEnabled();

    public boolean isEventTransformerDirectoryManagerCacheEnabled();
}

