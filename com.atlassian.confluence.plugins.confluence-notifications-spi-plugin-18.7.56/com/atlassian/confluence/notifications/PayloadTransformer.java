/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.fugue.Maybe;
import java.util.Optional;

@ExperimentalApi
public interface PayloadTransformer<SOURCE, PAYLOAD extends NotificationPayload>
extends Participant<PAYLOAD> {
    @Deprecated
    public Maybe<PAYLOAD> create(SOURCE var1);

    default public Optional<PAYLOAD> optionalCreate(SOURCE source) {
        return Optional.ofNullable((NotificationPayload)this.create(source).getOrNull());
    }

    public Class<SOURCE> getSourceType();
}

