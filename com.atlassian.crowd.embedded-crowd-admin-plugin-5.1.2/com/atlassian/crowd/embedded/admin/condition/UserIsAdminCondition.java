/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.crowd.embedded.admin.condition;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;

public class UserIsAdminCondition
implements Condition {
    private final UserManager userManager;

    public UserIsAdminCondition(UserManager userManager) {
        this.userManager = userManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        return remoteUserKey != null && this.userManager.isAdmin(remoteUserKey);
    }
}

