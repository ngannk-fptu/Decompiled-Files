/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class DefaultAccessNotificationPayload
implements NotificationPayload {
    private final UserKey sourceUserKey;
    private final UserKey targetUserKey;
    private final long contentId;
    private final ContentType contentType;
    private final String userRole;
    private final String accessType;
    private final boolean draft;
    private final String spaceKey;
    private String notificationKey;

    @JsonCreator
    public DefaultAccessNotificationPayload(@JsonProperty(value="sourceUserKey") UserKey sourceUserKey, @JsonProperty(value="targetUserKey") UserKey targetUserKey, @JsonProperty(value="contentId") long contentId, @JsonProperty(value="contentType") ContentType contentType, @JsonProperty(value="userRole") String userRole, @JsonProperty(value="accessType") String accessType, @JsonProperty(value="draft") boolean draft, @JsonProperty(value="spaceKey") String spaceKey) {
        this.sourceUserKey = sourceUserKey;
        this.targetUserKey = targetUserKey;
        this.contentId = contentId;
        this.contentType = contentType;
        this.userRole = userRole;
        this.accessType = accessType;
        this.draft = draft;
        this.spaceKey = spaceKey;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.sourceUserKey.getStringValue());
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return Optional.of(this.sourceUserKey);
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public Maybe<String> getNotificationKey() {
        return Option.option((Object)this.notificationKey);
    }

    public UserKey getTargetUserKey() {
        return this.targetUserKey;
    }

    public UserKey getSourceUserKey() {
        return this.sourceUserKey;
    }

    public long getContentId() {
        return this.contentId;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public String getUserRole() {
        return this.userRole;
    }

    public String getAccessType() {
        return this.accessType;
    }

    public boolean isDraft() {
        return this.draft;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

