/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.google.common.collect.Maps
 *  org.jdom.Content
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.services;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.extra.jira.ApplicationLinkResolver;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssueBatchService;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.exception.MalformedRequestException;
import com.atlassian.confluence.extra.jira.exception.UnsupportedJiraServerException;
import com.atlassian.confluence.extra.jira.helper.JiraExceptionHelper;
import com.atlassian.confluence.extra.jira.model.ClientId;
import com.atlassian.confluence.extra.jira.request.JiraRequestData;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.jira.beans.JiraServerBean;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jdom.Content;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraIssueBatchService
implements JiraIssueBatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraIssueBatchService.class);
    private final JiraIssuesManager jiraIssuesManager;
    private final ApplicationLinkResolver applicationLinkResolver;
    private final JiraConnectorManager jiraConnectorManager;
    private final JiraExceptionHelper jiraExceptionHelper;

    public DefaultJiraIssueBatchService(JiraIssuesManager jiraIssuesManager, ApplicationLinkResolver applicationLinkResolver, JiraConnectorManager jiraConnectorManager, JiraExceptionHelper jiraExceptionHelper) {
        this.jiraIssuesManager = jiraIssuesManager;
        this.applicationLinkResolver = applicationLinkResolver;
        this.jiraConnectorManager = jiraConnectorManager;
        this.jiraExceptionHelper = jiraExceptionHelper;
    }

    @Override
    public Map<String, Object> getPlaceHolderBatchResults(ClientId clientId, String serverId, Set<String> keys, ConversionContext conversionContext) throws MacroExecutionException {
        ReadOnlyApplicationLink appLink = this.applicationLinkResolver.getAppLinkForServer("", serverId);
        if (appLink != null) {
            HashMap resultsMap = Maps.newHashMap();
            HashMap elementMap = Maps.newHashMap();
            List<Element> entries = this.createPlaceHoldersList(clientId.toString(), keys);
            for (Element item : entries) {
                elementMap.put(item.getChild("key").getValue(), item);
            }
            resultsMap.put("elementMap", elementMap);
            String jiraDisplayUrl = JiraUtil.normalizeUrl(appLink.getDisplayUrl());
            resultsMap.put("jiraDisplayUrl", jiraDisplayUrl);
            return resultsMap;
        }
        throw new MacroExecutionException(this.jiraExceptionHelper.getText("jiraissues.error.noapplinks", new Object[0]));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Map<String, Object> getBatchResults(String serverId, Set<String> keys, ConversionContext conversionContext) throws MacroExecutionException, UnsupportedJiraServerException {
        ReadOnlyApplicationLink appLink = this.applicationLinkResolver.getAppLinkForServer("", serverId);
        if (appLink == null) throw new MacroExecutionException(this.jiraExceptionHelper.getText("jiraissues.error.noapplinks", new Object[0]));
        JiraServerBean jiraServerBean = this.jiraConnectorManager.getJiraServer(appLink);
        if (jiraServerBean.getBuildNumber() != -1L && jiraServerBean.getBuildNumber() < SUPPORTED_JIRA_SERVER_BUILD_NUMBER) throw new UnsupportedJiraServerException();
        HashMap resultsMap = Maps.newHashMap();
        HashMap elementMap = Maps.newHashMap();
        JiraRequestData jiraRequestData = new JiraRequestData(this.jqlQueryFor(keys), JiraIssuesMacro.Type.JQL);
        Channel channel = this.retrieveChannel(jiraRequestData, conversionContext, appLink);
        if (channel == null) return Maps.newHashMap();
        Element element = channel.getChannelElement();
        List entries = element.getChildren("item");
        for (Element item : entries) {
            elementMap.put(item.getChild("key").getValue(), item);
        }
        resultsMap.put("elementMap", elementMap);
        String jiraDisplayUrl = JiraUtil.normalizeUrl(appLink.getDisplayUrl());
        resultsMap.put("jiraDisplayUrl", jiraDisplayUrl);
        String jiraRpcUrl = JiraUtil.normalizeUrl(appLink.getRpcUrl());
        resultsMap.put("jiraRpcUrl", jiraRpcUrl);
        return resultsMap;
    }

    private String jqlQueryFor(Set<String> keys) {
        return "KEY IN " + keys.stream().collect(Collectors.joining("', '", "('", "')"));
    }

    private List<Element> createPlaceHoldersList(String clientId, Set<String> issueKeys) {
        return issueKeys.stream().map(key -> this.createPlaceHolderElement(clientId, (String)key)).collect(Collectors.toList());
    }

    private Element createPlaceHolderElement(String clientId, String issueKey) {
        Element element = new Element("item");
        Element key = new Element("key");
        Element summary = new Element("summary");
        Element type = new Element("type");
        Element status = new Element("status");
        Element isPlaceholder = new Element("isPlaceholder");
        Element clientIdElement = new Element("clientId");
        clientIdElement.setText(clientId);
        key.setText(issueKey);
        type.setText("Task");
        element.addContent((Content)key);
        element.addContent((Content)summary);
        element.addContent((Content)type);
        element.addContent((Content)status);
        element.addContent((Content)isPlaceholder);
        element.addContent((Content)clientIdElement);
        return element;
    }

    protected Channel retrieveChannel(JiraRequestData jiraRequestData, ConversionContext conversionContext, ReadOnlyApplicationLink applicationLink) throws MacroExecutionException {
        String requestData = jiraRequestData.getRequestData();
        String url = this.getXmlUrl(requestData, applicationLink);
        boolean forceAnonymous = false;
        if (applicationLink == null) {
            forceAnonymous = true;
        }
        try {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel(url, JiraIssuesColumnManager.SINGLE_ISSUE_COLUMN_NAMES, applicationLink, forceAnonymous, false);
            return channel;
        }
        catch (CredentialsRequiredException credentialsRequiredException) {
            try {
                Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel(url, JiraIssuesColumnManager.SINGLE_ISSUE_COLUMN_NAMES, applicationLink, true, false);
                return channel;
            }
            catch (Exception e) {
                this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
                return null;
            }
        }
        catch (MalformedRequestException e) {
            LOGGER.debug("MalformedRequestException: " + e.getMessage());
            this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
        }
        catch (Exception e) {
            LOGGER.debug("Exception: " + e.getMessage());
            this.jiraExceptionHelper.throwMacroExecutionException(e, conversionContext);
        }
        return null;
    }

    private String getXmlUrl(String requestData, ReadOnlyApplicationLink appLink) {
        return JiraUtil.normalizeUrl(appLink.getRpcUrl()) + "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?tempMax=1000&returnMax=true&validateQuery=false&jqlQuery=" + JiraUtil.utf8Encode(requestData);
    }
}

