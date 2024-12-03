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
package com.atlassian.confluence.plugins.emailtopage.events;

import com.atlassian.confluence.plugins.emailtopage.events.EmailThreadStagedPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleEmailThreadStagedPayload
implements EmailThreadStagedPayload {
    private final String userKey;
    private final String pageTitle;
    private final String hash;
    private final boolean error;

    @JsonCreator
    public SimpleEmailThreadStagedPayload(@JsonProperty(value="userKey") String userKey, @JsonProperty(value="pageTitle") String pageTitle, @JsonProperty(value="hash") String hash, @JsonProperty(value="error") boolean error) {
        this.userKey = userKey;
        this.pageTitle = pageTitle;
        this.hash = hash;
        this.error = error;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.userKey);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return StringUtils.isEmpty((CharSequence)this.userKey) ? Optional.empty() : Optional.of(new UserKey(this.userKey));
    }

    @Override
    public String getPageTitle() {
        return this.pageTitle;
    }

    @Override
    public String getHash() {
        return this.hash;
    }

    @Override
    public boolean isError() {
        return this.error;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleEmailThreadStagedPayload{");
        sb.append("userKey='").append(this.userKey).append('\'');
        sb.append(", pageTitle='").append(this.pageTitle).append('\'');
        sb.append(", hash='").append(this.hash).append('\'');
        sb.append(", error='").append(this.error).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

