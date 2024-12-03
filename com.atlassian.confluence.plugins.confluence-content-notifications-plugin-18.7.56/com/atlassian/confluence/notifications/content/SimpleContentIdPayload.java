/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SimpleContentIdPayload
implements ContentIdPayload {
    private final long contentId;
    private final ContentType contentType;
    private final String originatingUserKey;
    private String notificationKey;

    @JsonCreator
    public SimpleContentIdPayload(@JsonProperty(value="contentType") ContentType contentType, @JsonProperty(value="contentId") long contentId, @JsonProperty(value="originatingUserKey") String originatingUserKey) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.originatingUserKey = originatingUserKey;
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
        return Option.option((Object)this.originatingUserKey);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return StringUtils.isEmpty((CharSequence)this.originatingUserKey) ? Optional.empty() : Optional.of(new UserKey(this.originatingUserKey));
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public Maybe<String> getNotificationKey() {
        return Option.option((Object)this.notificationKey);
    }

    public String toString() {
        return "SimpleContentIdPayload{contentId=" + this.contentId + ", contentType=" + this.contentType + ", originatingUserKey='" + this.originatingUserKey + "'}";
    }
}

