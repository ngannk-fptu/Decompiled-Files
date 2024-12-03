/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.module.macros;

import com.atlassian.plugin.notifications.api.macros.Macro;
import com.atlassian.plugin.notifications.spi.UserRole;
import java.util.Map;

public class UserRoleMacro
implements Macro {
    @Override
    public String getName() {
        return "userRole";
    }

    @Override
    public String resolve(Map<String, Object> context) {
        UserRole role = (UserRole)context.get("role");
        return role == null ? null : role.getID();
    }
}

