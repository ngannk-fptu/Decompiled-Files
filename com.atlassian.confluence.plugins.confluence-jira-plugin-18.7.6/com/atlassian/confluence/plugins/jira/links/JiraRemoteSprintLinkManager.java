/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.confluence.json.json.Json
 *  com.atlassian.confluence.json.json.JsonObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira.links;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.jira.links.JiraRemoteLinkManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraRemoteSprintLinkManager
extends JiraRemoteLinkManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraRemoteSprintLinkManager.class);

    public JiraRemoteSprintLinkManager(ReadOnlyApplicationLinkService applicationLinkService, HostApplication hostApplication, SettingsManager settingsManager, JiraMacroFinderService macroFinderService, RequestFactory requestFactory) {
        super(applicationLinkService, hostApplication, settingsManager, macroFinderService, requestFactory);
    }

    public boolean createLinkToSprint(AbstractPage page, String applinkId, String sprintId, String fallbackUrl, String creationToken) {
        String baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
        ReadOnlyApplicationLink applicationLink = this.findApplicationLink(applinkId, fallbackUrl);
        if (applicationLink != null) {
            return this.createRemoteSprintLink(applicationLink, baseUrl + GeneralUtil.getIdBasedPageUrl((AbstractPage)page), page.getIdAsString(), sprintId, creationToken);
        }
        LOGGER.warn("Failed to create a remote link to the sprint with ID '{}' for the application link ID '{}'. Reason: Application link not found.", (Object)sprintId, (Object)applinkId);
        return false;
    }

    private boolean createRemoteSprintLink(ReadOnlyApplicationLink applicationLink, String canonicalPageUrl, String pageId, String sprintId, String creationToken) {
        JsonObject requestJson = this.createJsonData(pageId, canonicalPageUrl, creationToken);
        String requestUrl = applicationLink.getRpcUrl() + "/rest/greenhopper/1.0/api/sprints/" + GeneralUtil.urlEncode((String)sprintId) + "/remotelinkchecked";
        Request request = this.requestFactory.createRequest(Request.MethodType.PUT, requestUrl);
        return this.executeRemoteLinkRequest(applicationLink, (Json)requestJson, request, sprintId, JiraRemoteLinkManager.OperationType.CREATE);
    }
}

