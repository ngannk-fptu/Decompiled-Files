/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.favicon.plugin;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;

public class CustomFaviconAdminCondition
implements Condition {
    private final UserManager userManager;

    public CustomFaviconAdminCondition(@ComponentImport UserManager anUserManager) {
        this.userManager = anUserManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return this.userManager.isAdmin(this.userManager.getRemoteUserKey());
    }
}

