/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mentions.notifications;

import com.atlassian.confluence.plugins.mentions.notifications.MentionContentPayload;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleMentionContentPayload
implements MentionContentPayload {
    @JsonProperty
    private long contentId;
    @JsonProperty
    private ContentTypeEnum contentType;
    @JsonProperty
    private String mentionedUserKey;
    @JsonProperty
    private String mentionedHtml;
    @JsonProperty
    private String originatingUserKey;
    @JsonProperty
    private UserKey authorUserKey;

    public SimpleMentionContentPayload() {
    }

    @Deprecated
    public SimpleMentionContentPayload(long contentId, ContentTypeEnum contentType, UserKey originatingUserKey, String mentionedUserKey, String mentionedHtml) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.mentionedUserKey = mentionedUserKey;
        this.mentionedHtml = mentionedHtml;
        this.originatingUserKey = originatingUserKey.getStringValue();
    }

    public SimpleMentionContentPayload(long contentId, ContentTypeEnum contentType, UserKey originatingUserKey, UserKey mentionedUserKey, String mentionedHtml) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.mentionedUserKey = mentionedUserKey != null ? mentionedUserKey.getStringValue() : null;
        this.mentionedHtml = mentionedHtml;
        this.originatingUserKey = originatingUserKey != null ? originatingUserKey.getStringValue() : null;
        this.authorUserKey = originatingUserKey;
    }

    @Override
    public long getContentId() {
        return this.contentId;
    }

    @Override
    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    @Override
    public UserKey getMentionedUserKey() {
        return new UserKey(this.mentionedUserKey);
    }

    @Override
    public Option<String> getMentionHtml() {
        if (StringUtils.isBlank((CharSequence)this.mentionedHtml)) {
            return Option.none();
        }
        return Option.some((Object)this.mentionedHtml);
    }

    @Override
    public Maybe<UserKey> getAuthorUserKey() {
        return Option.option((Object)this.authorUserKey);
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.originatingUserKey);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return StringUtils.isEmpty((CharSequence)this.originatingUserKey) ? Optional.empty() : Optional.of(new UserKey(this.originatingUserKey));
    }
}

