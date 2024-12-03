/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.notifications.batch.service.BatchContext
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.content.batching;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.batch.service.BatchContext;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;

@ExperimentalSpi
public class ContentBatchContext
implements BatchContext {
    private final UserKey originator;
    private final long contentID;
    private final Maybe<String> notificationKey;

    public ContentBatchContext(UserKey originator, long contentID) {
        this(originator, contentID, (Maybe<String>)Option.none());
    }

    public ContentBatchContext(UserKey originator, long contentID, Maybe<String> notificationKey) {
        this.originator = originator;
        this.contentID = contentID;
        this.notificationKey = notificationKey;
    }

    public UserKey getOriginator() {
        return this.originator;
    }

    public Maybe<String> getNotificationKey() {
        return this.notificationKey;
    }

    public long getContentID() {
        return this.contentID;
    }
}

