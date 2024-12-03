/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Preconditions;
import java.util.Map;

public class UserIsSysadminCondition
implements Condition {
    private final UserManager userManager;

    public UserIsSysadminCondition(UserManager userManager) {
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        return remoteUserKey != null && this.userManager.isSystemAdmin(remoteUserKey);
    }
}

