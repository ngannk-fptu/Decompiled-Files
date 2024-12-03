/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.codec.binary.Base64
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.ApplicationLinkResolver;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.FlexigridResponseGenerator;
import com.atlassian.confluence.extra.jira.JiraIssuesMacro;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.helper.JiraJqlHelper;
import com.atlassian.confluence.extra.jira.request.JiraRequestData;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImagePlaceHolderHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImagePlaceHolderHelper.class);
    private static final String JIRA_TABLE_DISPLAY_PLACEHOLDER_IMG_PATH = "/download/resources/confluence.extra.jira/jira-table.png";
    private static final String JIRA_ISSUES_RESOURCE_PATH = "jiraissues-xhtml";
    private static final String JIRA_ISSUES_SINGLE_MACRO_TEMPLATE = "{jiraissues:key=%s}";
    private static final String JIRA_SINGLE_MACRO_TEMPLATE = "{jira:key=%s}";
    private static final String JIRA_SINGLE_ISSUE_IMG_SERVLET_PATH_TEMPLATE = "/plugins/servlet/confluence/placeholder/macro?definition=%s&locale=%s";
    private static final String PLACEHOLDER_SERVLET = "/plugins/servlet/image-generator";
    private static final String XML_SEARCH_REQUEST_URI = "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml";
    private String DARK_FEATURE_IGNORE_EDIT_MODE_COUNT = "confluence.extra.jira.edit.ignore.count";
    private LocaleManager localeManager;
    private ApplicationLinkResolver applicationLinkResolver;
    private JiraIssuesManager jiraIssuesManager;
    private FlexigridResponseGenerator flexigridResponseGenerator;
    private I18nResolver i18nResolver;
    private DarkFeatureManager darkFeatureManager;

    public ImagePlaceHolderHelper(JiraIssuesManager jiraIssuesManager, LocaleManager localeManager, I18nResolver i18nResolver, ApplicationLinkResolver applicationLinkResolver, FlexigridResponseGenerator flexigridResponseGenerator, DarkFeatureManager darkFeatureManager) {
        this.localeManager = localeManager;
        this.i18nResolver = i18nResolver;
        this.applicationLinkResolver = applicationLinkResolver;
        this.jiraIssuesManager = jiraIssuesManager;
        this.flexigridResponseGenerator = flexigridResponseGenerator;
        this.darkFeatureManager = darkFeatureManager;
    }

    public ImagePlaceholder getJiraMacroImagePlaceholder(JiraRequestData jiraRequestData, Map<String, String> parameters, String resourcePath) {
        String requestData = jiraRequestData.getRequestData();
        JiraIssuesMacro.Type requestType = jiraRequestData.getRequestType();
        JiraIssuesMacro.JiraIssuesType issuesType = JiraUtil.getJiraIssuesType(parameters, jiraRequestData.getRequestType(), requestData);
        switch (issuesType) {
            case SINGLE: {
                String key = requestData;
                if (requestType == JiraIssuesMacro.Type.URL) {
                    key = JiraJqlHelper.getKeyFromURL(requestData);
                }
                return this.getSingleImagePlaceHolder(key, resourcePath);
            }
            case COUNT: {
                return this.getCountImagePlaceHolder(parameters, requestType, requestData);
            }
            case TABLE: {
                return new DefaultImagePlaceholder(JIRA_TABLE_DISPLAY_PLACEHOLDER_IMG_PATH, null, false);
            }
        }
        return null;
    }

    private ImagePlaceholder getSingleImagePlaceHolder(String key, String resourcePath) {
        String macro = resourcePath.contains(JIRA_ISSUES_RESOURCE_PATH) ? String.format(JIRA_ISSUES_SINGLE_MACRO_TEMPLATE, key) : String.format(JIRA_SINGLE_MACRO_TEMPLATE, key);
        byte[] encoded = Base64.encodeBase64((byte[])macro.getBytes());
        String locale = this.localeManager.getSiteDefaultLocale().toString();
        String placeHolderUrl = String.format(JIRA_SINGLE_ISSUE_IMG_SERVLET_PATH_TEMPLATE, new String(encoded), locale);
        return new DefaultImagePlaceholder(placeHolderUrl, null, false);
    }

    private ImagePlaceholder getCountImagePlaceHolder(Map<String, String> params, JiraIssuesMacro.Type requestType, String requestData) {
        String totalIssues;
        Object url = requestData;
        ReadOnlyApplicationLink appLink = null;
        if (this.darkFeatureManager.isEnabledForCurrentUser(this.DARK_FEATURE_IGNORE_EDIT_MODE_COUNT).orElse(false).booleanValue()) {
            totalIssues = "-1";
        } else {
            try {
                String jql = null;
                appLink = this.applicationLinkResolver.resolve(requestType, requestData, params);
                switch (requestType) {
                    case JQL: {
                        jql = requestData;
                        break;
                    }
                    case URL: {
                        if (JiraJqlHelper.isUrlFilterType(requestData)) {
                            jql = JiraJqlHelper.getJQLFromFilter(appLink, (String)url, this.jiraIssuesManager, this.i18nResolver);
                            break;
                        }
                        if (!requestData.matches(".+(jqlQuery|jql)=([^&]+)")) break;
                        jql = JiraJqlHelper.getJQLFromJQLURL((String)url);
                        break;
                    }
                }
                if (jql != null) {
                    url = appLink.getRpcUrl() + "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=" + JiraUtil.utf8Encode(jql) + "&tempMax=0&returnMax=true";
                }
                boolean forceAnonymous = params.get("anonymous") != null && Boolean.parseBoolean(params.get("anonymous"));
                Channel channel = this.jiraIssuesManager.retrieveXMLAsChannel((String)url, new LinkedHashSet<String>(), appLink, forceAnonymous, true, true);
                totalIssues = this.flexigridResponseGenerator.generate(channel, new LinkedHashSet<String>(), 0, true, true);
            }
            catch (CredentialsRequiredException e) {
                LOGGER.info("Continues request by anonymous user");
                totalIssues = this.getTotalIssuesByAnonymous((String)url, appLink);
            }
            catch (Exception e) {
                LOGGER.error("Error generate count macro placeholder: " + e.getMessage(), (Throwable)e);
                totalIssues = "-1";
            }
        }
        return new DefaultImagePlaceholder("/plugins/servlet/image-generator?totalIssues=" + totalIssues, null, false);
    }

    private String getTotalIssuesByAnonymous(String url, ReadOnlyApplicationLink appLink) {
        try {
            Channel channel = this.jiraIssuesManager.retrieveXMLAsChannelByAnonymous(url, new LinkedHashSet<String>(), appLink, false, true, true);
            return this.flexigridResponseGenerator.generate(channel, new LinkedHashSet<String>(), 0, true, true);
        }
        catch (Exception e) {
            LOGGER.info("Can't retrive issues by anonymous");
            return "-1";
        }
    }
}

