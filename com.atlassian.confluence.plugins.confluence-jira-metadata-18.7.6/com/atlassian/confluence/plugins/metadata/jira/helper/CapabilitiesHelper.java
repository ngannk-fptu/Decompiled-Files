/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.plugins.capabilities.api.LinkedAppWithCapabilities
 *  com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.helper;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.plugins.capabilities.api.LinkedAppWithCapabilities;
import com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CapabilitiesHelper {
    public static final String REMOTE_LINK_AGGREGATION = "remote-link-aggregation";
    public static final String AGGREGATE_CACHE_INVALIDATION = "confluence-jira-metadata-aggregate-cache-invalidation";
    public static final String AGGREGATE_CACHE_INVALIDATION_FOR_SPRINTS = "remote-sprint-link-events";
    public static final String REMOTE_SPRINT_LINKING = "gh-remote-sprint-link";
    private static final String JIRA_TYPE = "jira";
    private final LinkedApplicationCapabilities linkedApplicationCapabilities;
    private final ReadOnlyApplicationLinkService appLinkService;

    @Autowired
    public CapabilitiesHelper(LinkedApplicationCapabilities linkedApplicationCapabilities, ReadOnlyApplicationLinkService appLinkService) {
        this.linkedApplicationCapabilities = linkedApplicationCapabilities;
        this.appLinkService = appLinkService;
    }

    public boolean isSupportedByAppLink(String capability, ReadOnlyApplicationLink appLink) {
        String appLinkId = appLink.getId().get();
        for (LinkedAppWithCapabilities linkedApp : this.linkedApplicationCapabilities.capableOf(capability)) {
            if (!appLinkId.equals(linkedApp.getApplicationLinkId())) continue;
            return true;
        }
        return false;
    }

    public Iterable<ReadOnlyApplicationLink> getAggregateCapableJiraLinks() {
        Set capableApps = this.linkedApplicationCapabilities.capableOf(REMOTE_LINK_AGGREGATION);
        HashSet<ReadOnlyApplicationLink> links = new HashSet<ReadOnlyApplicationLink>();
        for (LinkedAppWithCapabilities app : capableApps) {
            ReadOnlyApplicationLink applicationLink;
            if (!JIRA_TYPE.equals(app.getType()) || (applicationLink = this.appLinkService.getApplicationLink(new ApplicationId(app.getApplicationLinkId()))) == null) continue;
            links.add(applicationLink);
        }
        return links;
    }
}

