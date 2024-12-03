/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.confluence.json.json.Json
 *  com.atlassian.confluence.json.json.JsonObject
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira.links;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JiraRemoteLinkManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraRemoteLinkManager.class);
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final HostApplication hostApplication;
    private final SettingsManager settingsManager;
    protected final JiraMacroFinderService macroFinderService;
    protected RequestFactory requestFactory;

    public JiraRemoteLinkManager(ReadOnlyApplicationLinkService applicationLinkService, HostApplication hostApplication, SettingsManager settingsManager, JiraMacroFinderService macroFinderService, RequestFactory requestFactory) {
        this.applicationLinkService = applicationLinkService;
        this.hostApplication = hostApplication;
        this.settingsManager = settingsManager;
        this.macroFinderService = macroFinderService;
        this.requestFactory = requestFactory;
    }

    protected JsonObject createJsonData(String pageId, String canonicalPageUrl, String creationToken) {
        return this.createJsonData(pageId, canonicalPageUrl).setProperty("creationToken", creationToken);
    }

    protected JsonObject createJsonData(String pageId, String canonicalPageUrl) {
        return new JsonObject().setProperty("globalId", this.getGlobalId(pageId)).setProperty("application", (Json)new JsonObject().setProperty("type", "com.atlassian.confluence").setProperty("name", this.settingsManager.getGlobalSettings().getSiteTitle())).setProperty("relationship", "mentioned in").setProperty("object", (Json)new JsonObject().setProperty("url", canonicalPageUrl).setProperty("title", "Page"));
    }

    protected String getGlobalId(String pageId) {
        return "appId=" + this.hostApplication.getId().get() + "&pageId=" + pageId;
    }

    protected boolean executeRemoteLinkRequest(ReadOnlyApplicationLink applicationLink, Json requestBody, Request request, String entityId, OperationType operationType) {
        String operation = operationType.equals((Object)OperationType.CREATE) ? "create" : "delete";
        try {
            request.addHeader("Content-Type", "application/json");
            if (requestBody != null) {
                request.setRequestBody(requestBody.serialize());
            }
            request.execute(response -> {
                switch (response.getStatusCode()) {
                    case 200: {
                        break;
                    }
                    case 201: {
                        break;
                    }
                    case 404: {
                        LOGGER.info("Failed to {} a remote link in {}. Reason: Remote links are not supported.", (Object)operation, (Object)applicationLink.getName());
                        throw new LoggingResponseException();
                    }
                    case 403: {
                        LOGGER.warn("Failed to {} a remote link to {} in {}. Reason: Forbidden", new Object[]{operation, entityId, applicationLink.getName()});
                        throw new LoggingResponseException();
                    }
                    default: {
                        LOGGER.warn("Failed to {} a remote link to {} in {}. Reason: {} - {}", (Object[])new String[]{operation, entityId, applicationLink.getName(), Integer.toString(response.getStatusCode()), response.getStatusText()});
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Response body: {}", (Object)response.getResponseBodyAsString());
                        }
                        throw new LoggingResponseException();
                    }
                }
            });
        }
        catch (LoggingResponseException loggingResponseException) {
        }
        catch (ResponseException e) {
            LOGGER.info("Could not {} Jira Remote Link", (Object)operation, (Object)e);
            return false;
        }
        return true;
    }

    protected ReadOnlyApplicationLink findApplicationLink(MacroDefinition macroDefinition) {
        return (ReadOnlyApplicationLink)Iterables.find((Iterable)this.applicationLinkService.getApplicationLinks(JiraApplicationType.class), input -> input.getName().equals(macroDefinition.getParameters().get("server")), (Object)this.applicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class));
    }

    protected ReadOnlyApplicationLink findApplicationLink(String applinkId, String fallbackUrl) {
        ReadOnlyApplicationLink applicationLink = this.applicationLinkService.getApplicationLink(new ApplicationId(applinkId));
        if (applicationLink == null && StringUtils.isNotBlank((CharSequence)fallbackUrl)) {
            applicationLink = (ReadOnlyApplicationLink)Iterables.find((Iterable)this.applicationLinkService.getApplicationLinks(JiraApplicationType.class), input -> StringUtils.containsIgnoreCase((CharSequence)fallbackUrl, (CharSequence)input.getDisplayUrl().toString()));
        }
        return applicationLink;
    }

    private class LoggingResponseException
    extends ResponseException {
        private LoggingResponseException() {
        }
    }

    protected static enum OperationType {
        CREATE,
        DELETE;

    }
}

