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

public class JiraRemoteEpicLinkManager
extends JiraRemoteLinkManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraRemoteEpicLinkManager.class);

    public JiraRemoteEpicLinkManager(ReadOnlyApplicationLinkService applicationLinkService, HostApplication hostApplication, SettingsManager settingsManager, JiraMacroFinderService macroFinderService, RequestFactory requestFactory) {
        super(applicationLinkService, hostApplication, settingsManager, macroFinderService, requestFactory);
    }

    public boolean createLinkToEpic(AbstractPage page, String applinkId, String issueKey, String fallbackUrl, String creationToken) {
        String baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
        ReadOnlyApplicationLink applicationLink = this.findApplicationLink(applinkId, fallbackUrl);
        if (applicationLink != null) {
            return this.createRemoteEpicLink(applicationLink, baseUrl + GeneralUtil.getIdBasedPageUrl((AbstractPage)page), page.getIdAsString(), issueKey, creationToken);
        }
        LOGGER.warn("Failed to create a remote link to {} for the application link ID '{}'. Reason: Application link not found.", (Object)issueKey, (Object)applinkId);
        return false;
    }

    private boolean createRemoteEpicLink(ReadOnlyApplicationLink applicationLink, String canonicalPageUrl, String pageId, String issueKey, String creationToken) {
        JsonObject requestJson = this.createJsonData(pageId, canonicalPageUrl, creationToken);
        String requestUrl = applicationLink.getRpcUrl() + "/rest/greenhopper/1.0/api/epics/" + GeneralUtil.urlEncode((String)issueKey) + "/remotelinkchecked";
        Request request = this.requestFactory.createRequest(Request.MethodType.PUT, requestUrl);
        return this.executeRemoteLinkRequest(applicationLink, (Json)requestJson, request, issueKey, JiraRemoteLinkManager.OperationType.CREATE);
    }
}

