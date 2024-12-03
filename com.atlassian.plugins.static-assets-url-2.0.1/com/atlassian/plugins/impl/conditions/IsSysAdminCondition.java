/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.plugins.impl.conditions;

import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;

public class IsSysAdminCondition
implements Condition {
    private UserManager userManager;

    public IsSysAdminCondition(UserManager userManager) {
        this.userManager = userManager;
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.isSystemAdmin();
    }

    private boolean isSystemAdmin() {
        UserKey userKey = this.userManager.getRemoteUserKey();
        return userKey != null && this.userManager.isSystemAdmin(userKey);
    }
}

