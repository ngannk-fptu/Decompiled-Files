/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.sharepage.notifications.payload;

import com.atlassian.confluence.plugins.sharepage.api.ShareRequest;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.ShareContentPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleShareContentPayload
extends ShareRequest
implements ShareContentPayload {
    private String originatingUserKey;
    private Map<String, Set<String>> emailsWithGroups = new HashMap<String, Set<String>>();
    private Set<String> requestEmails = new HashSet<String>();

    @JsonCreator
    public SimpleShareContentPayload(@JsonProperty(value="originatingUserKey") String originatingUserKey, @JsonProperty(value="entityId") Long entityId, @JsonProperty(value="contextualPageId") Long contextualPageId, @JsonProperty(value="users") Set<String> users, @JsonProperty(value="groups") Set<String> groups, @JsonProperty(value="emails") Set<String> emails, @JsonProperty(value="note") String note, @JsonProperty(value="emailsWithGroups") Map<String, Set<String>> emailsWithGroups, @JsonProperty(value="requestEmails") Set<String> requestEmails) {
        super(users, emails, groups, entityId, contextualPageId, note, "");
        this.originatingUserKey = originatingUserKey;
        this.emailsWithGroups = emailsWithGroups;
        this.requestEmails = requestEmails;
    }

    @Override
    public Map<String, Set<String>> getEmailsWithGroups() {
        return this.emailsWithGroups;
    }

    @Override
    public Set<String> getOriginalRequestEmails() {
        return this.requestEmails;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.originatingUserKey);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return StringUtils.isEmpty((CharSequence)this.originatingUserKey) ? Optional.empty() : Optional.of(new UserKey(this.originatingUserKey));
    }
}

