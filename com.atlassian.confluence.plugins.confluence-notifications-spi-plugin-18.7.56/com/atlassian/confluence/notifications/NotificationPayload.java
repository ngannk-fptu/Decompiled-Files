/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

@ExperimentalApi
public interface NotificationPayload {
    @Deprecated
    public Maybe<String> getOriginatingUserKey();

    default public Optional<UserKey> getOriginatorUserKey() {
        String userKey = (String)this.getOriginatingUserKey().getOrNull();
        return StringUtils.isEmpty((CharSequence)userKey) ? Optional.empty() : Optional.of(new UserKey(userKey));
    }

    @Deprecated
    default public Maybe<String> getNotificationKey() {
        return Option.none();
    }

    default public Optional<String> optionalNotificationKey() {
        return Optional.ofNullable((String)this.getNotificationKey().getOrNull());
    }

    default public void setNotificationKey(String notificationKey) {
    }
}

