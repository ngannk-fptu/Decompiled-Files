/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugin.descriptor.mail.conditions;

import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public abstract class AbstractNotificationCondition
implements Condition {
    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map context) {
        NotificationContext notificationContext = new NotificationContext();
        notificationContext.putAll(context);
        return this.shouldDisplay(notificationContext);
    }

    protected abstract boolean shouldDisplay(NotificationContext var1);
}

