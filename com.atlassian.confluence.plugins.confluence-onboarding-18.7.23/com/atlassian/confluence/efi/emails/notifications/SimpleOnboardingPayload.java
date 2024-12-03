/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.efi.emails.notifications;

import com.atlassian.confluence.efi.emails.notifications.OnboardingPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleOnboardingPayload
implements OnboardingPayload {
    private String originatingUserKey;

    @JsonCreator
    public SimpleOnboardingPayload(@JsonProperty(value="userKey") String originatingUserKey) {
        this.originatingUserKey = originatingUserKey;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.some((Object)this.originatingUserKey);
    }
}

