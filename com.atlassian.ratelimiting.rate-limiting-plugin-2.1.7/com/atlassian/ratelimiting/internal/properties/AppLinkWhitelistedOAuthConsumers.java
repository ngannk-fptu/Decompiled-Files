/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.properties;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLinkWhitelistedOAuthConsumers
implements WhitelistedOAuthConsumers {
    private static final Logger logger = LoggerFactory.getLogger(AppLinkWhitelistedOAuthConsumers.class);
    private static final Set<String> ATLASSIAN_APPLICATIONS = ImmutableSet.of((Object)"bitbucket", (Object)"stash", (Object)"confluence", (Object)"crowd", (Object)"jira", (Object)"bamboo", (Object[])new String[]{"crucible", "fisheye", "refapp"});
    private final ApplicationLinkService applicationLinkService;

    public AppLinkWhitelistedOAuthConsumers(ApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    @Override
    public Set<String> getConsumers() {
        HashSet<String> atlassianOauthConsumers = new HashSet<String>();
        for (ApplicationLink applicationLink : this.applicationLinkService.getApplicationLinks()) {
            String oauthConsumerKey = this.getConsumerKey(applicationLink);
            if (oauthConsumerKey == null || !this.isTrelloConsumerKey(oauthConsumerKey) && !this.isAtlassianOauthApplink(applicationLink)) continue;
            logger.debug("Whitelisting consumer key [{}] for application link [{}] type [{}]", new Object[]{oauthConsumerKey, applicationLink.getName(), applicationLink.getType()});
            atlassianOauthConsumers.add(oauthConsumerKey);
        }
        return atlassianOauthConsumers;
    }

    private String getConsumerKey(ApplicationLink applicationLink) {
        Object storedConsumerKey = applicationLink.getProperty("oauth.incoming.consumerkey");
        return storedConsumerKey != null ? storedConsumerKey.toString() : null;
    }

    private boolean isTrelloConsumerKey(String oauthConsumerKey) {
        return StringUtils.equalsIgnoreCase((CharSequence)oauthConsumerKey, (CharSequence)"trello-connector-for-jira-server");
    }

    private boolean isAtlassianOauthApplink(ApplicationLink applicationLink) {
        ApplicationType applicationType = applicationLink.getType();
        return applicationType != null && ATLASSIAN_APPLICATIONS.stream().anyMatch(it -> StringUtils.containsIgnoreCase((CharSequence)applicationType.getClass().getName().toLowerCase(), (CharSequence)it));
    }
}

