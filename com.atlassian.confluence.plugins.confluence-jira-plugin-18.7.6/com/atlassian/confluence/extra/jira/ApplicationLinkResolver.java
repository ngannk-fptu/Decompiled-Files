/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.google.common.base.Function
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.google.common.base.Function;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ApplicationLinkResolver {
    private static final String XML_JQL_REGEX = ".+searchrequest-xml/temp/SearchRequest.+";
    private ReadOnlyApplicationLinkService readOnlyApplicationLinkService;

    public ApplicationLinkResolver(ReadOnlyApplicationLinkService readOnlyApplicationLinkService) {
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
    }

    public ReadOnlyApplicationLink resolve(JiraIssuesMacro.Type requestType, String requestData, Map<String, String> typeSafeParams) throws TypeNotInstalledException {
        ReadOnlyApplicationLink primaryAppLink = this.readOnlyApplicationLinkService.getPrimaryApplicationLink(JiraApplicationType.class);
        if (primaryAppLink == null) {
            return null;
        }
        if (StringUtils.isBlank((CharSequence)requestData)) {
            String errorMessage = "No request data supplied";
            throw new TypeNotInstalledException(errorMessage);
        }
        if (requestType == JiraIssuesMacro.Type.URL) {
            Iterable applicationLinks = this.readOnlyApplicationLinkService.getApplicationLinks(JiraApplicationType.class);
            for (ReadOnlyApplicationLink applicationLink : applicationLinks) {
                if (!requestData.startsWith(applicationLink.getRpcUrl().toString()) && !requestData.startsWith(applicationLink.getDisplayUrl().toString())) continue;
                return applicationLink;
            }
            if (requestData.matches(XML_JQL_REGEX)) {
                return null;
            }
            String errorMessage = "Can not find an application link base on url of request data.";
            throw new TypeNotInstalledException(errorMessage);
        }
        String serverName = typeSafeParams.get("server");
        ReadOnlyApplicationLink appLink = this.getAppLinkForServer(serverName, typeSafeParams.get("serverId"));
        if (appLink != null) {
            return appLink;
        }
        if (StringUtils.isBlank((CharSequence)serverName)) {
            return primaryAppLink;
        }
        String errorMessage = "Can not find an application link base on server name :" + serverName;
        throw new TypeNotInstalledException(errorMessage);
    }

    public ReadOnlyApplicationLink getAppLinkForServer(String serverName, String serverId) {
        ReadOnlyApplicationLink appLink = null;
        if (StringUtils.isNotBlank((CharSequence)serverId)) {
            appLink = this.getAppLink(serverId, (Function<ReadOnlyApplicationLink, String>)((Function)input -> input != null ? input.getId().toString() : null));
        }
        if (appLink == null && StringUtils.isNotBlank((CharSequence)serverName)) {
            appLink = this.getAppLink(serverName, (Function<ReadOnlyApplicationLink, String>)((Function)input -> input != null ? input.getName() : null));
        }
        return appLink;
    }

    private ReadOnlyApplicationLink getAppLink(String matcher, Function<ReadOnlyApplicationLink, String> getProperty) {
        for (ReadOnlyApplicationLink applicationLink : this.readOnlyApplicationLinkService.getApplicationLinks(JiraApplicationType.class)) {
            if (!matcher.equals(getProperty.apply((Object)applicationLink))) continue;
            return applicationLink;
        }
        return null;
    }
}

