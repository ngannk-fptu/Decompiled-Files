/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.extra.jira.applink;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.extra.jira.util.JiraConnectorUtils;
import com.atlassian.confluence.plugins.jira.beans.JiraServerBean;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class DefaultJiraConnectorManager
implements JiraConnectorManager {
    public static final long EXCEPTION_WHILE_CALLING_JIRA = -1L;
    private static final String JSON_PATH_BUILD_NUMBER = "buildNumber";
    private static final String REST_URL_SERVER_INFO = "/rest/api/2/serverInfo";
    private ReadOnlyApplicationLinkService appLinkService;
    private AuthenticationConfigurationManager authenticationConfigurationManager;
    private LoadingCache<ReadOnlyApplicationLink, JiraServerBean> jiraServersCache;

    public DefaultJiraConnectorManager(ReadOnlyApplicationLinkService appLinkService, AuthenticationConfigurationManager authenticationConfigurationManager) {
        this.appLinkService = appLinkService;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
    }

    @Override
    public List<JiraServerBean> getJiraServers() {
        Iterable appLinks = this.appLinkService.getApplicationLinks(JiraApplicationType.class);
        if (appLinks == null) {
            return Collections.emptyList();
        }
        ArrayList<JiraServerBean> servers = new ArrayList<JiraServerBean>();
        for (ReadOnlyApplicationLink applicationLink : appLinks) {
            servers.add(this.getInternalJiraServer(applicationLink));
        }
        return servers;
    }

    @Override
    public JiraServerBean getJiraServer(ReadOnlyApplicationLink applicationLink) {
        return this.getInternalJiraServer(applicationLink);
    }

    @Override
    public void updateDetailJiraServerInfor(ReadOnlyApplicationLink applicationLink) {
        JiraServerBean jiraServerBean = this.getInternalJiraServer(applicationLink);
        if (jiraServerBean != null) {
            jiraServerBean.setName(applicationLink.getName());
            jiraServerBean.setUrl(applicationLink.getDisplayUrl().toString());
        }
    }

    @Override
    public void updatePrimaryServer(ReadOnlyApplicationLink applicationLink) {
        List<JiraServerBean> jiraServerBeans = this.getJiraServers();
        for (JiraServerBean jiraServerBean : jiraServerBeans) {
            jiraServerBean.setSelected(applicationLink.getId().toString().equals(jiraServerBean.getId()));
        }
    }

    private JiraServerBean createJiraServerBean(ReadOnlyApplicationLink applicationLink) {
        return new JiraServerBean(applicationLink.getId().toString(), applicationLink.getDisplayUrl().toString(), applicationLink.getName(), applicationLink.isPrimary(), null, this.getServerBuildNumber(applicationLink), applicationLink.getRpcUrl().toString(), applicationLink.getDisplayUrl().toString());
    }

    private long getServerBuildNumber(ReadOnlyApplicationLink appLink) {
        try {
            ApplicationLinkRequest request = JiraConnectorUtils.getApplicationLinkRequest(appLink, Request.MethodType.GET, REST_URL_SERVER_INFO);
            request.addHeader("Content-Type", "application/json");
            String responseString = request.execute();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseString);
            return rootNode.path(JSON_PATH_BUILD_NUMBER).getLongValue();
        }
        catch (ResponseStatusException e) {
            return -1L;
        }
        catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private JiraServerBean getInternalJiraServer(ReadOnlyApplicationLink applicationLink) {
        if (null != applicationLink) {
            JiraServerBean jiraServerBean = (JiraServerBean)this.getJiraServersCache().getUnchecked((Object)applicationLink);
            jiraServerBean.setAuthUrl(JiraConnectorUtils.getAuthUrl(this.authenticationConfigurationManager, applicationLink));
            if (jiraServerBean.getBuildNumber() == -1L) {
                jiraServerBean.setBuildNumber(this.getServerBuildNumber(applicationLink));
            }
            return jiraServerBean;
        }
        return null;
    }

    private LoadingCache<ReadOnlyApplicationLink, JiraServerBean> getJiraServersCache() {
        if (this.jiraServersCache == null) {
            this.jiraServersCache = CacheBuilder.newBuilder().expireAfterWrite(4L, TimeUnit.HOURS).build((CacheLoader)new CacheLoader<ReadOnlyApplicationLink, JiraServerBean>(){

                public JiraServerBean load(ReadOnlyApplicationLink applicationLink) {
                    return DefaultJiraConnectorManager.this.createJiraServerBean(applicationLink);
                }
            });
        }
        return this.jiraServersCache;
    }
}

