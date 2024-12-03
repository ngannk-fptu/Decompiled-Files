/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.analytics.client.report;

import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserKey;

public class EventReportPermissionManager {
    private static final String DARK_FEATURE_KEY = "com.atlassian.analytics.eventreport";
    private final UserPermissionsHelper userPermissionsHelper;
    private final DarkFeatureManager darkFeatureManager;

    public EventReportPermissionManager(UserPermissionsHelper userPermissionsHelper, DarkFeatureManager darkFeatureManager) {
        this.userPermissionsHelper = userPermissionsHelper;
        this.darkFeatureManager = darkFeatureManager;
    }

    public boolean hasPermission(UserKey userKey) {
        return this.userPermissionsHelper.isUserSystemAdmin(userKey) || this.darkFeatureManager.isFeatureEnabledForUser(userKey, DARK_FEATURE_KEY);
    }
}

