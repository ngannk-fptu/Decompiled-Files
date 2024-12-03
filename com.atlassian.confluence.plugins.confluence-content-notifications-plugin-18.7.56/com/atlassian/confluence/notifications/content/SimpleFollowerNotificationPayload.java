/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.content.FollowerPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SimpleFollowerNotificationPayload
implements FollowerPayload {
    private final String userBeingFollowed;
    private final String follower;

    @JsonCreator
    public SimpleFollowerNotificationPayload(@JsonProperty(value="userBeingFollowed") String userBeingFollowed, @JsonProperty(value="follower") String follower) {
        this.userBeingFollowed = userBeingFollowed;
        this.follower = follower;
    }

    @Override
    public String getFollower() {
        return this.follower;
    }

    @Override
    public String getUserBeingFollowed() {
        return this.userBeingFollowed;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.some((Object)this.follower);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return StringUtils.isEmpty((CharSequence)this.follower) ? Optional.empty() : Optional.of(new UserKey(this.follower));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleFollowerNotificationPayload{");
        sb.append("userBeingFollowed='").append(this.userBeingFollowed).append('\'');
        sb.append(", follower='").append(this.follower).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

