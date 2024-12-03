/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.model.application.Application;

public enum AccessBasedSynchronizationFilterType {
    NO_FILTERING,
    USER_ONLY_FILTERING,
    USER_AND_GROUP_FILTERING;


    public static AccessBasedSynchronizationFilterType fromApplication(Application application) {
        if (application.isFilteringGroupsWithAccessEnabled()) {
            return USER_AND_GROUP_FILTERING;
        }
        if (application.isFilteringUsersWithAccessEnabled()) {
            return USER_ONLY_FILTERING;
        }
        return NO_FILTERING;
    }
}

