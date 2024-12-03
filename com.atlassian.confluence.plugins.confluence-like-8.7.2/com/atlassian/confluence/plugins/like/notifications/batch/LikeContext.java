/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.like.notifications.batch;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.sal.api.user.UserKey;
import java.util.HashSet;
import java.util.Set;

public class LikeContext {
    private final Set<UserKey> userKeys = new HashSet<UserKey>();
    private final ContentType contentType;
    private final long contentId;

    public LikeContext(ContentType contentType, long contentId) {
        this.contentType = contentType;
        this.contentId = contentId;
    }

    public Set<UserKey> getUserKeys() {
        return this.userKeys;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public long getContentId() {
        return this.contentId;
    }
}

