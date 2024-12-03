/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.json.json.Json
 *  com.atlassian.confluence.json.json.JsonObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.google.common.collect.Sets
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jira.links;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.extra.jira.executor.JiraExecutorFactory;
import com.atlassian.confluence.extra.jira.model.PageDTO;
import com.atlassian.confluence.extra.jira.util.JiraIssuePredicates;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.jira.links.JiraRemoteLinkManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraRemoteIssueLinkManager
extends JiraRemoteLinkManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraRemoteIssueLinkManager.class);
    private static final int THREAD_POOL_SIZE = Integer.getInteger("jira.remotelink.threadpool.size", 10);
    private final ExecutorService jiraLinkExecutorService;

    public JiraRemoteIssueLinkManager(ReadOnlyApplicationLinkService applicationLinkService, HostApplication hostApplication, SettingsManager settingsManager, JiraMacroFinderService macroFinderService, RequestFactory requestFactory, JiraExecutorFactory executorFactory) {
        super(applicationLinkService, hostApplication, settingsManager, macroFinderService, requestFactory);
        this.jiraLinkExecutorService = executorFactory.newLimitedThreadPool(THREAD_POOL_SIZE, "Jira remote link executor");
    }

    public void updateIssueLinksForEmbeddedMacros(AbstractPage prevPage, AbstractPage page) {
        Set<JiraIssueLinkMacro> macros = this.getRemoteLinkMacros(page);
        Set<JiraIssueLinkMacro> prevMacros = this.getRemoteLinkMacros(prevPage);
        this.updateRemoteLinks(page, (Iterable<JiraIssueLinkMacro>)Sets.difference(prevMacros, macros), JiraRemoteLinkManager.OperationType.DELETE);
        this.updateRemoteLinks(page, (Iterable<JiraIssueLinkMacro>)Sets.difference(macros, prevMacros), JiraRemoteLinkManager.OperationType.CREATE);
    }

    public void createIssueLinksForEmbeddedMacros(AbstractPage page) {
        Set<JiraIssueLinkMacro> macros = this.getRemoteLinkMacros(page);
        this.updateRemoteLinks(page, macros, JiraRemoteLinkManager.OperationType.CREATE);
    }

    public void deleteIssueLinksForEmbeddedMacros(AbstractPage page) {
        Set<JiraIssueLinkMacro> macros = this.getRemoteLinkMacros(page);
        this.updateRemoteLinks(page, macros, JiraRemoteLinkManager.OperationType.DELETE);
    }

    private Set<JiraIssueLinkMacro> getRemoteLinkMacros(AbstractPage page) {
        Set<MacroDefinition> macroDefinitions;
        HashSet remoteLinkMacros = Sets.newHashSet();
        try {
            macroDefinitions = this.macroFinderService.findJiraIssueMacros(page, JiraIssuePredicates.isSingleIssue);
        }
        catch (XhtmlException ex) {
            throw new IllegalStateException("Could not parse Create Jira Issue macros", ex);
        }
        remoteLinkMacros.addAll(macroDefinitions.stream().map(x$0 -> new JiraIssueLinkMacro((MacroDefinition)x$0)).collect(Collectors.toList()));
        return remoteLinkMacros;
    }

    private void updateRemoteLinks(AbstractPage page, Iterable<JiraIssueLinkMacro> jiraIssueLinkMacros, JiraRemoteLinkManager.OperationType operationType) {
        String baseUrl = GeneralUtil.getGlobalSettings().getBaseUrl();
        PageDTO pageDTO = new PageDTO();
        pageDTO.setId(page.getId());
        Callable<Object> jiraRemoteLinkCallable = () -> {
            for (JiraIssueLinkMacro jiraIssueLinkMacro : jiraIssueLinkMacros) {
                MacroDefinition macroDefinition = jiraIssueLinkMacro.getMacroDefinition();
                String defaultParam = macroDefinition.getDefaultParameterValue();
                String keyVal = (String)macroDefinition.getParameters().get("key");
                String issueKey = defaultParam != null ? defaultParam : keyVal;
                ReadOnlyApplicationLink applicationLink = this.findApplicationLink(macroDefinition);
                if (applicationLink == null) {
                    LOGGER.warn("Failed to update a remote link to {} in {}. Reason: Application link not found.", (Object)issueKey, macroDefinition.getParameters().get("server"));
                    continue;
                }
                if (operationType == JiraRemoteLinkManager.OperationType.CREATE) {
                    this.createRemoteIssueLink(applicationLink, baseUrl + GeneralUtil.getIdBasedPageUrl((AbstractPage)pageDTO), pageDTO.getIdAsString(), issueKey);
                    continue;
                }
                this.deleteRemoteIssueLink(applicationLink, pageDTO.getIdAsString(), issueKey);
            }
            return null;
        };
        this.jiraLinkExecutorService.submit(jiraRemoteLinkCallable);
    }

    private void createRemoteIssueLink(ReadOnlyApplicationLink applicationLink, String canonicalPageUrl, String pageId, String issueKey) {
        try {
            JsonObject remoteLink = this.createJsonData(pageId, canonicalPageUrl);
            String requestUrl = "rest/api/latest/issue/" + issueKey + "/remotelink";
            ApplicationLinkRequest request = applicationLink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.POST, requestUrl);
            this.executeRemoteLinkRequest(applicationLink, (Json)remoteLink, (Request)request, issueKey, JiraRemoteLinkManager.OperationType.CREATE);
        }
        catch (CredentialsRequiredException e) {
            LOGGER.warn("Authentication was required, but credentials were not available when creating a Jira Remote Link", (Throwable)e);
        }
    }

    private void deleteRemoteIssueLink(ReadOnlyApplicationLink applicationLink, String pageId, String issueKey) {
        try {
            String globalId = this.getGlobalId(pageId);
            String requestUrl = "rest/api/latest/issue/" + issueKey + "/remotelink?globalId=" + GeneralUtil.urlEncode((String)globalId);
            ApplicationLinkRequest request = applicationLink.createAuthenticatedRequestFactory().createRequest(Request.MethodType.DELETE, requestUrl);
            this.executeRemoteLinkRequest(applicationLink, null, (Request)request, issueKey, JiraRemoteLinkManager.OperationType.DELETE);
        }
        catch (CredentialsRequiredException e) {
            LOGGER.info("Authentication was required, but credentials were not available when creating a Jira Remote Link", (Throwable)e);
        }
    }

    @PreDestroy
    public void destroy() {
        this.jiraLinkExecutorService.shutdown();
    }

    private class JiraIssueLinkMacro {
        private final MacroDefinition macroDefinition;
        private final String issueKey;
        private final String serverName;

        JiraIssueLinkMacro(MacroDefinition macroDefinition) {
            this.macroDefinition = macroDefinition;
            String defaultParam = macroDefinition.getDefaultParameterValue();
            String keyVal = (String)macroDefinition.getParameters().get("key");
            this.issueKey = defaultParam != null ? defaultParam : keyVal;
            this.serverName = (String)macroDefinition.getParameters().get("server");
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            JiraIssueLinkMacro that = (JiraIssueLinkMacro)o;
            return Objects.equals(this.issueKey, that.issueKey) && Objects.equals(this.serverName, that.serverName);
        }

        public int hashCode() {
            return Objects.hash(this.issueKey, this.serverName);
        }

        public MacroDefinition getMacroDefinition() {
            return this.macroDefinition;
        }
    }
}

