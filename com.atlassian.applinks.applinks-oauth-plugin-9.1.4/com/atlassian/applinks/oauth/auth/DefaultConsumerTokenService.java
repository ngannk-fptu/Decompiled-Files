/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.oauth.ConsumerTokenService
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.oauth.ConsumerTokenService;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultConsumerTokenService
implements ConsumerTokenService {
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final ApplicationLinkService applicationLinkService;

    @Autowired
    public DefaultConsumerTokenService(ConsumerTokenStoreService consumerTokenStoreService, ApplicationLinkService applicationLinkService) {
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.applicationLinkService = applicationLinkService;
    }

    public void removeAllTokensForUsername(String username) {
        Iterable applicationLinks = this.applicationLinkService.getApplicationLinks();
        for (ApplicationLink applicationLink : applicationLinks) {
            ApplicationId applicationId = applicationLink.getId();
            if (!this.consumerTokenStoreService.isOAuthOutgoingEnabled(applicationId)) continue;
            this.consumerTokenStoreService.removeConsumerToken(applicationId, username);
        }
    }
}

