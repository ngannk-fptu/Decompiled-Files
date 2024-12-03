/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.oauth.consumer.ConsumerToken
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.annotations.Internal;
import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.oauth.consumer.ConsumerToken;

@Internal
public interface ConsumerTokenStoreService {
    public void addConsumerToken(ApplicationLink var1, String var2, ConsumerToken var3);

    public void removeAllConsumerTokens(ApplicationLink var1);

    public boolean removeConsumerToken(ApplicationId var1, String var2);

    public ConsumerToken getConsumerToken(ApplicationLink var1, String var2);

    public boolean isOAuthOutgoingEnabled(ApplicationId var1);
}

