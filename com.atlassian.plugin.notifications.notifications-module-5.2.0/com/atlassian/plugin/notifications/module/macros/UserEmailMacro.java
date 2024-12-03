/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.plugin.notifications.module.macros;

import com.atlassian.plugin.notifications.api.macros.Macro;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Map;

public class UserEmailMacro
implements Macro {
    private final UserManager userManager;

    public UserEmailMacro(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public String getName() {
        return "userEmail";
    }

    @Override
    public String resolve(Map<String, Object> context) {
        UserProfile profile = this.userManager.getUserProfile((UserKey)context.get("userKey"));
        return profile == null ? null : profile.getEmail();
    }
}

