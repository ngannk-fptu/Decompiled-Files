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
import com.atlassian.confluence.notifications.content.ForgotPasswordPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
public class SimpleForgotPasswordPayload
implements ForgotPasswordPayload {
    private final String userKey;
    private final String resetPasswordLink;
    private final String forgotPasswordLink;

    @JsonCreator
    public SimpleForgotPasswordPayload(@JsonProperty(value="userKey") String userKey, @JsonProperty(value="resetPasswordLink") String resetPasswordLink, @JsonProperty(value="forgotPasswordLink") String forgotPasswordLink) {
        this.userKey = userKey;
        this.resetPasswordLink = resetPasswordLink;
        this.forgotPasswordLink = forgotPasswordLink;
    }

    @Override
    public String getResetPasswordLink() {
        return this.resetPasswordLink;
    }

    @Override
    public String getForgotPasswordLink() {
        return this.forgotPasswordLink;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.userKey);
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return StringUtils.isEmpty((CharSequence)this.userKey) ? Optional.empty() : Optional.of(new UserKey(this.userKey));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleForgotPasswordPayload{");
        sb.append("userKey='").append(this.userKey).append('\'');
        sb.append(", resetPasswordLink='").append(this.resetPasswordLink).append('\'');
        sb.append(", forgotPasswordLink='").append(this.forgotPasswordLink).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

