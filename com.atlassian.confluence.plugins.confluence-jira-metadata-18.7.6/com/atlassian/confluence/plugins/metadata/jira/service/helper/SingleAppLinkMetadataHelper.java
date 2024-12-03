/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.spi.application.ApplicationIdUtil
 *  com.atlassian.sal.api.net.Request$MethodType
 */
package com.atlassian.confluence.plugins.metadata.jira.service.helper;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.spi.application.ApplicationIdUtil;
import com.atlassian.confluence.plugins.metadata.jira.exception.JiraMetadataException;
import com.atlassian.confluence.plugins.metadata.jira.helper.CapabilitiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.helper.JiraMetadataErrorHelper;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataSingleGroup;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraIssuesMetadataDelegate;
import com.atlassian.confluence.plugins.metadata.jira.service.JiraMetadataDelegate;
import com.atlassian.confluence.plugins.metadata.jira.service.helper.JiraEpicPropertiesHelper;
import com.atlassian.confluence.plugins.metadata.jira.util.GlobalPageIdUtil;
import com.atlassian.sal.api.net.Request;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class SingleAppLinkMetadataHelper {
    private static final String EPIC_CAPABILITY = "gh-epic-properties";
    private final List<JiraMetadataDelegate> jiraMetadataDelegates;
    private final JiraEpicPropertiesHelper epicPropertiesHelper;
    private final ReadOnlyApplicationLink appLink;
    private final Collection<Callable<List<JiraMetadataSingleGroup>>> tasks;
    private final ApplicationLinkRequestFactory requestFactory;
    private final Map<String, String> epicConfig;
    private final JiraIssuesMetadataDelegate issuesMetadataDelegate;
    private final CapabilitiesHelper capabilitiesHelper;
    private final List<String> globalIds;
    private final JiraMetadataErrorHelper errorHelper;

    public SingleAppLinkMetadataHelper(List<JiraMetadataDelegate> jiraMetadataDelegates, JiraEpicPropertiesHelper epicPropertiesHelper, ReadOnlyApplicationLink appLink, long pageId, JiraIssuesMetadataDelegate issuesMetadataDelegate, CapabilitiesHelper capabilitiesHelper, JiraMetadataErrorHelper errorHelper, HostApplication hostApplication) {
        this.jiraMetadataDelegates = jiraMetadataDelegates;
        this.epicPropertiesHelper = epicPropertiesHelper;
        this.appLink = appLink;
        this.capabilitiesHelper = capabilitiesHelper;
        this.tasks = new ArrayList<Callable<List<JiraMetadataSingleGroup>>>();
        this.issuesMetadataDelegate = issuesMetadataDelegate;
        this.errorHelper = errorHelper;
        this.requestFactory = appLink.createAuthenticatedRequestFactory();
        this.epicConfig = epicPropertiesHelper.getCachedEpicProperties(appLink);
        this.globalIds = Collections.unmodifiableList(Arrays.asList(GlobalPageIdUtil.generateGlobalPageId(ApplicationIdUtil.generate((URI)hostApplication.getBaseUrl()), pageId), GlobalPageIdUtil.generateGlobalPageId(hostApplication.getId(), pageId)));
    }

    public Collection<Callable<List<JiraMetadataSingleGroup>>> process() {
        try {
            this.queueFuturesForJIRAMetadata();
            if (this.capabilitiesHelper.isSupportedByAppLink(EPIC_CAPABILITY, this.appLink)) {
                this.tasks.add(new Callable<List<JiraMetadataSingleGroup>>(){
                    final ApplicationLinkRequest epicPropertiesRequest;
                    final ApplicationLinkRequest issuesRequestForAllFields;
                    {
                        this.epicPropertiesRequest = SingleAppLinkMetadataHelper.this.createApplinkRequest(Request.MethodType.GET, "/rest/greenhopper/1.0/api/epicproperties");
                        this.issuesRequestForAllFields = SingleAppLinkMetadataHelper.this.createApplinkRequest(Request.MethodType.GET, SingleAppLinkMetadataHelper.this.issuesMetadataDelegate.getUrl(SingleAppLinkMetadataHelper.this.globalIds, SingleAppLinkMetadataHelper.this.epicConfig));
                    }

                    @Override
                    public List<JiraMetadataSingleGroup> call() {
                        try {
                            Map<String, String> newEpicConfig = SingleAppLinkMetadataHelper.this.epicPropertiesHelper.getEpicProperties(SingleAppLinkMetadataHelper.this.appLink, this.epicPropertiesRequest);
                            if (SingleAppLinkMetadataHelper.this.epicPropertiesHelper.epicPropertiesDifferent(SingleAppLinkMetadataHelper.this.epicConfig, newEpicConfig)) {
                                return SingleAppLinkMetadataHelper.this.issuesMetadataDelegate.getGroups(SingleAppLinkMetadataHelper.this.appLink, this.issuesRequestForAllFields, newEpicConfig, SingleAppLinkMetadataHelper.this.globalIds);
                            }
                        }
                        catch (Exception e) {
                            SingleAppLinkMetadataHelper.this.errorHelper.handleException(e, SingleAppLinkMetadataHelper.this.appLink, SingleAppLinkMetadataHelper.this.requestFactory);
                        }
                        return Collections.emptyList();
                    }
                });
            }
        }
        catch (Exception e) {
            this.errorHelper.handleException(e, this.appLink, this.requestFactory);
        }
        return this.tasks;
    }

    private void queueFuturesForJIRAMetadata() throws Exception {
        for (final JiraMetadataDelegate jiraMetadataDelegate : this.jiraMetadataDelegates) {
            if (!jiraMetadataDelegate.isSupported(this.appLink)) continue;
            this.tasks.add(new Callable<List<JiraMetadataSingleGroup>>(){
                final ApplicationLinkRequest request;
                {
                    this.request = SingleAppLinkMetadataHelper.this.createApplinkRequest(Request.MethodType.GET, jiraMetadataDelegate.getUrl(SingleAppLinkMetadataHelper.this.globalIds, SingleAppLinkMetadataHelper.this.epicConfig));
                }

                @Override
                public List<JiraMetadataSingleGroup> call() {
                    try {
                        return jiraMetadataDelegate.getGroups(SingleAppLinkMetadataHelper.this.appLink, this.request, SingleAppLinkMetadataHelper.this.epicConfig, SingleAppLinkMetadataHelper.this.globalIds);
                    }
                    catch (Exception e) {
                        SingleAppLinkMetadataHelper.this.errorHelper.handleException(e, SingleAppLinkMetadataHelper.this.appLink, SingleAppLinkMetadataHelper.this.requestFactory);
                        return Collections.emptyList();
                    }
                }
            });
        }
    }

    private ApplicationLinkRequest createApplinkRequest(Request.MethodType type, String requestUrl) throws Exception {
        try {
            return this.requestFactory.createRequest(type, requestUrl);
        }
        catch (CredentialsRequiredException e) {
            throw e;
        }
        catch (Exception e) {
            throw new JiraMetadataException(this.appLink, JiraMetadataErrorHelper.Status.APPLINK_MISCONFIGURED, e);
        }
    }
}

