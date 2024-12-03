/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.notifications.AnalyticsRenderContext;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;

@Internal
public final class AnalyticsContext
implements AnalyticsRenderContext.Context {
    private final Date timestamp;
    private final String mediumKey;
    private final Option<UserKey> recipient;
    private final ModuleCompleteKey notificationKey;

    public AnalyticsContext(Date timestamp, String mediumKey, Option<UserKey> recipient, ModuleCompleteKey notificationKey) {
        this.timestamp = timestamp;
        this.mediumKey = mediumKey;
        this.recipient = recipient;
        this.notificationKey = notificationKey;
    }

    @Override
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getMediumKey() {
        return this.mediumKey;
    }

    @Override
    public ModuleCompleteKey getNotificationKey() {
        return this.notificationKey;
    }

    @Override
    public Option<UserKey> getRecipient() {
        return this.recipient;
    }
}

