/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleLikePayload
implements LikePayload {
    @JsonProperty
    private long contentId;
    @JsonProperty
    private ContentType contentType;
    @JsonProperty
    private String originator;

    SimpleLikePayload() {
    }

    SimpleLikePayload(long contentId, ContentType contentType, String originator) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.originator = originator;
    }

    @Override
    public long getContentId() {
        return this.contentId;
    }

    @Override
    public ContentType getContentType() {
        return this.contentType;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.some((Object)this.originator);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return Optional.ofNullable(this.originator).map(UserKey::new);
    }
}

