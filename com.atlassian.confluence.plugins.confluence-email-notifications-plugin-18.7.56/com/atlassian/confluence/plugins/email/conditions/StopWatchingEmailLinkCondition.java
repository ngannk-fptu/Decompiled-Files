/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.confluence.plugins.email.conditions;

import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;

public class StopWatchingEmailLinkCondition
extends AbstractNotificationCondition {
    private Notification.WatchType watchType;

    public void init(Map<String, String> params) throws PluginParseException {
        this.watchType = Notification.WatchType.valueOf((String)params.get("watchType"));
    }

    protected boolean shouldDisplay(NotificationContext context) {
        return this.watchType == context.getWatchType();
    }
}

