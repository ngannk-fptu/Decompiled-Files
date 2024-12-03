/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.createcontent.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.user.User;

public class ShowSpaceWelcomeDialogCondition
extends BaseConfluenceCondition {
    public static final String SPACE_WELCOME_DIALOG_DISMISSED_KEY = "confluence.user.create.content.space.welcome.dialog.dismissed";
    private final UserAccessor userAccessor;

    public ShowSpaceWelcomeDialogCondition(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        User user = (User)context.getParameter("remoteuser");
        if (user == null) {
            return false;
        }
        UserPreferences pref = this.userAccessor.getUserPreferences(user);
        if (pref == null) {
            return false;
        }
        return !pref.getBoolean(SPACE_WELCOME_DIALOG_DISMISSED_KEY);
    }
}

