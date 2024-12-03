/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.frontend.conditions;

import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UserIsSystemAdminCondition
implements Condition {
    private final UserManager userManager;

    public UserIsSystemAdminCondition(@Nonnull UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager);
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        return userKey != null && this.userManager.isSystemAdmin(userKey);
    }
}

