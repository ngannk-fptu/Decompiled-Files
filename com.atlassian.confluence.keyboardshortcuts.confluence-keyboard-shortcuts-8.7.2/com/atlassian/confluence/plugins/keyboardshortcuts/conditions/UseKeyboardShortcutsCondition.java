/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.keyboardshortcuts.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public class UseKeyboardShortcutsCondition
extends BaseConfluenceCondition {
    private final UserAccessor userAccessor;

    public UseKeyboardShortcutsCondition(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        UserPreferences userPreferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        return !userPreferences.getBoolean("confluence.user.keyboard.shortcuts.disabled");
    }
}

