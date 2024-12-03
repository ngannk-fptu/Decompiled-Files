/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.gson.Gson
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.jiracharts;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.extra.jira.util.JiraConnectorUtils;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugins.jira.beans.JiraServerBean;
import com.atlassian.confluence.plugins.jiracharts.JQLValidator;
import com.atlassian.confluence.plugins.jiracharts.JiraChartMacro;
import com.atlassian.confluence.plugins.jiracharts.model.JQLValidationResult;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultJQLValidator
implements JQLValidator {
    private static final String JIRA_SEARCH_URL = "/rest/api/2/search";
    private static final String JIRA_FILTER_NAV_URL = "/secure/IssueNavigator.jspa?reset=true&mode=hide";
    private static final Long START_JIRA_UNSUPPORTED_BUILD_NUMBER = 6109L;
    private static final Long END_JIRA_UNSUPPORTED_BUILD_NUMBER = 6155L;
    private static Logger log = LoggerFactory.getLogger(JiraChartMacro.class);
    private ReadOnlyApplicationLinkService applicationLinkService;
    private I18nResolver i18nResolver;
    private JiraConnectorManager jiraConnectorManager;

    public DefaultJQLValidator(ReadOnlyApplicationLinkService applicationLinkService, I18nResolver i18nResolver, JiraConnectorManager jiraConnectorManager) {
        this.applicationLinkService = applicationLinkService;
        this.jiraConnectorManager = jiraConnectorManager;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public JQLValidationResult doValidate(Map<String, String> parameters, boolean isVerifyChartSupported) throws MacroExecutionException {
        try {
            String jql = GeneralUtil.urlDecode((String)parameters.get("jql"));
            String appLinkId = parameters.get("serverId");
            ReadOnlyApplicationLink applicationLink = JiraConnectorUtils.getApplicationLink(this.applicationLinkService, appLinkId);
            if (isVerifyChartSupported) {
                this.validateJiraSupportedVersion(applicationLink);
            }
            return this.validateJQL(applicationLink, jql);
        }
        catch (TypeNotInstalledException e) {
            log.debug("AppLink is not exits", (Throwable)e);
            throw new MacroExecutionException(this.i18nResolver.getText("jirachart.error.applicationLinkNotExist"));
        }
    }

    private JQLValidationResult validateJQL(ReadOnlyApplicationLink applicationLink, String jql) throws MacroExecutionException {
        JQLValidationResult result = new JQLValidationResult();
        try {
            this.validateInternal(applicationLink, jql, result);
            String displayUrl = applicationLink.getDisplayUrl().toString();
            String rpcUrl = applicationLink.getRpcUrl().toString();
            UrlBuilder builder = new UrlBuilder(displayUrl + JIRA_FILTER_NAV_URL);
            builder.add("jqlQuery", jql);
            result.setFilterUrl(builder.toUrl());
            result.setDisplayUrl(displayUrl);
            result.setRpcUrl(rpcUrl);
        }
        catch (Exception e) {
            log.debug("Exception during make a call to Jira via Applink", (Throwable)e);
            throw new MacroExecutionException((Throwable)e);
        }
        return result;
    }

    private void validateJiraSupportedVersion(ReadOnlyApplicationLink appLinkId) throws MacroExecutionException {
        JiraServerBean jiraServerBean = this.jiraConnectorManager.getJiraServer(appLinkId);
        if (jiraServerBean != null) {
            Long buildNumber = jiraServerBean.getBuildNumber();
            if (buildNumber == -1L) {
                throw new MacroExecutionException(this.i18nResolver.getText("jirachart.error.cannot.call.jira"));
            }
            if (buildNumber >= START_JIRA_UNSUPPORTED_BUILD_NUMBER && buildNumber < END_JIRA_UNSUPPORTED_BUILD_NUMBER) {
                throw new MacroExecutionException(this.i18nResolver.getText("jirachart.version.unsupported"));
            }
        }
    }

    private void validateInternal(ReadOnlyApplicationLink applicationLink, String jql, JQLValidationResult result) throws CredentialsRequiredException, ResponseException {
        UrlBuilder urlBuilder = new UrlBuilder(JIRA_SEARCH_URL);
        urlBuilder.add("jql", jql).add("maxResults", 0);
        Object[] objects = JiraConnectorUtils.getApplicationLinkRequestWithOauUrl(applicationLink, Request.MethodType.GET, urlBuilder.toUrl());
        JiraResponse jiraResponse = (JiraResponse)((ApplicationLinkRequest)objects[0]).execute((ApplicationLinkResponseHandler)new JQLApplicationLinkResponseHandler());
        if (objects[1] != null) {
            result.setAuthUrl((String)objects[1]);
        }
        result.setErrorMgs(jiraResponse.getErrors());
        result.setIssueCount(jiraResponse.getIssueCount());
    }

    private class JiraResponse {
        private List<String> errors;
        private int issueCount;

        private JiraResponse() {
        }

        public List<String> getErrors() {
            return this.errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public int getIssueCount() {
            return this.issueCount;
        }

        public void setIssueCount(int issueCount) {
            this.issueCount = issueCount;
        }
    }

    private class JQLApplicationLinkResponseHandler
    implements ApplicationLinkResponseHandler<JiraResponse> {
        private JQLApplicationLinkResponseHandler() {
        }

        public JiraResponse handle(Response response) throws ResponseException {
            JiraResponse returnValue = new JiraResponse();
            int responseStatus = response.getStatusCode();
            String responseBody = response.getResponseBodyAsString();
            try {
                JSONObject json = new JSONObject(responseBody);
                if (responseStatus >= 400) {
                    String errorsStr = json.getString("errorMessages");
                    Gson gson = new Gson();
                    String[] errors = (String[])gson.fromJson(errorsStr, String[].class);
                    returnValue.setErrors(Arrays.asList(errors));
                }
                if (responseStatus == 200) {
                    returnValue.setIssueCount(json.getInt("total"));
                }
            }
            catch (JSONException ex) {
                throw new ResponseException("Could not parse json from Jira", (Throwable)ex);
            }
            return returnValue;
        }

        public JiraResponse credentialsRequired(Response paramResponse) throws ResponseException {
            return null;
        }
    }
}

