/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PayloadBasedNotification<PAYLOAD extends NotificationPayload>
implements Notification<PAYLOAD> {
    private final PAYLOAD payload;
    private final ModuleCompleteKey key;
    private final UUID id;

    public PayloadBasedNotification(PAYLOAD payload, ModuleCompleteKey moduleCompleteKey) {
        this.key = moduleCompleteKey;
        this.payload = payload;
        this.id = UUID.randomUUID();
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public ModuleCompleteKey getKey() {
        return this.key;
    }

    @Override
    public PAYLOAD getPayload() {
        return this.payload;
    }

    @Override
    public Maybe<UserKey> getOriginator() {
        Optional<UserKey> originatingUserKey = this.payload.getOriginatorUserKey();
        return (Maybe)originatingUserKey.map(Option::some).orElse(Option.none());
    }

    public String toString() {
        Optional<UserKey> originatingUserKey = this.payload.getOriginatorUserKey();
        ToStringBuilder toStringBuilder = new ToStringBuilder((Object)this).append("key", (Object)this.key).append("payload", this.payload).append("id", (Object)this.id).append("originator", originatingUserKey.isPresent() ? originatingUserKey.get() : "null");
        return toStringBuilder.toString();
    }
}

