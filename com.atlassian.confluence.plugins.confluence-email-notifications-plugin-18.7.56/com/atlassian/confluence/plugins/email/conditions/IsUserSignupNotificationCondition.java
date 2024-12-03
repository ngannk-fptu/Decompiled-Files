/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 */
package com.atlassian.confluence.plugins.email.conditions;

import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;

public class IsUserSignupNotificationCondition
extends AbstractNotificationCondition {
    public boolean shouldDisplay(NotificationContext context) {
        return context.get("isUserSignupNotification") == Boolean.TRUE;
    }
}

