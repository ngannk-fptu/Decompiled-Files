/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.analytics.client.conditions;

import com.atlassian.analytics.client.UserPermissionsHelper;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class UserIsSysAdmin
implements Condition {
    private final UserPermissionsHelper userPermissionsHelper;

    public UserIsSysAdmin(UserPermissionsHelper userPermissionsHelper) {
        this.userPermissionsHelper = userPermissionsHelper;
    }

    public void init(Map<String, String> params) {
    }

    public boolean shouldDisplay(Map<String, Object> params) {
        return this.userPermissionsHelper.isCurrentUserSystemAdmin();
    }
}

