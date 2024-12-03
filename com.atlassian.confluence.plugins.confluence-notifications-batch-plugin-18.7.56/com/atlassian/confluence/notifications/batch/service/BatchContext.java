/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;

@ExperimentalApi
public interface BatchContext {
    public UserKey getOriginator();

    @Deprecated
    public Maybe<String> getNotificationKey();

    default public Optional<String> optionalNotificationKey() {
        return Optional.ofNullable((String)this.getNotificationKey().getOrNull());
    }
}

