/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.plugins.custom_apps.ui;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;

public class HasAdminPermissionCondition
implements Condition {
    private final UserManager userManager;

    public HasAdminPermissionCondition(UserManager userManager) {
        this.userManager = userManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return this.userManager.isAdmin(this.userManager.getRemoteUsername());
    }
}

