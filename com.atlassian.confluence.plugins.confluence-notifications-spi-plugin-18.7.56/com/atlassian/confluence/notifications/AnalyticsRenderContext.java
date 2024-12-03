/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.Internal;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;
import java.util.Optional;

@Internal
public interface AnalyticsRenderContext {
    public Context getContext();

    public static interface Context {
        public Date getTimestamp();

        public String getMediumKey();

        public ModuleCompleteKey getNotificationKey();

        @Deprecated
        public Option<UserKey> getRecipient();

        default public Optional<UserKey> optionalRecipient() {
            return Optional.ofNullable((UserKey)this.getRecipient().getOrNull());
        }
    }
}

