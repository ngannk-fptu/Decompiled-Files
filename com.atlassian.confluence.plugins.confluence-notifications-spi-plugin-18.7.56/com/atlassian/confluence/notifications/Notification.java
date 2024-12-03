/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import java.util.UUID;

@ExperimentalApi
public interface Notification<PAYLOAD extends NotificationPayload> {
    public UUID getId();

    public ModuleCompleteKey getKey();

    public PAYLOAD getPayload();

    @Deprecated
    public Maybe<UserKey> getOriginator();

    default public Optional<UserKey> optionalOriginator() {
        return Optional.ofNullable((UserKey)this.getOriginator().getOrNull());
    }
}

