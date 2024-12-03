/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.plugins.whitelist.NotAuthorizedException
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Throwables
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.columns;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesUrlManager;
import com.atlassian.confluence.extra.jira.api.services.JiraResponseHandler;
import com.atlassian.confluence.extra.jira.applink.JiraAppLinkResponseHandler;
import com.atlassian.confluence.extra.jira.request.JiraChannelResponseHandler;
import com.atlassian.confluence.extra.jira.request.JiraStringResponseHandler;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.plugins.jira.beans.BasicJiraIssueBean;
import com.atlassian.confluence.plugins.jira.beans.JiraIssueBean;
import com.atlassian.plugins.whitelist.NotAuthorizedException;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraIssuesManager
implements JiraIssuesManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraIssuesManager.class);
    private static final String CREATE_JIRA_ISSUE_URL = "/rest/api/2/issue/";
    private static final String CREATE_JIRA_ISSUE_BATCH_URL = "/rest/api/2/issue/bulk";
    private static final int CONNECTION_TIMEOUT = Integer.parseInt(System.getProperty("confluence.jira.connection.timeout", "30000"));
    private final JiraIssuesColumnManager jiraIssuesColumnManager;
    private final JiraIssuesUrlManager jiraIssuesUrlManager;
    private final RequestFactory<?> requestFactory;
    private final OutboundWhitelist outboundWhitelist;
    private LoadingCache<ReadOnlyApplicationLink, Boolean> batchIssueCapableCache;
    private boolean idParamPresent;

    public DefaultJiraIssuesManager(JiraIssuesColumnManager jiraIssuesColumnManager, JiraIssuesUrlManager jiraIssuesUrlManager, RequestFactory<?> requestFactory, OutboundWhitelist outboundWhitelist) {
        this.jiraIssuesColumnManager = jiraIssuesColumnManager;
        this.jiraIssuesUrlManager = jiraIssuesUrlManager;
        this.requestFactory = requestFactory;
        this.outboundWhitelist = outboundWhitelist;
    }

    @Override
    public Map<String, String> getColumnMap(String jiraIssuesUrl) {
        return this.jiraIssuesColumnManager.getColumnMap(this.jiraIssuesUrlManager.getRequestUrl(jiraIssuesUrl));
    }

    @Override
    public void setColumnMap(String jiraIssuesUrl, Map<String, String> columnMap) {
        this.jiraIssuesColumnManager.setColumnMap(this.jiraIssuesUrlManager.getRequestUrl(jiraIssuesUrl), columnMap);
    }

    protected JiraResponseHandler retrieveXML(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous, JiraResponseHandler.HandlerType handlerType, boolean checkCacheBeforeLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLResponse(url, columns, appLink, forceAnonymous, isAnonymous, handlerType, checkCacheBeforeLookup, false);
    }

    protected JiraResponseHandler retrieveXML(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous, JiraResponseHandler.HandlerType handlerType, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        return this.retrieveXMLResponse(url, columns, appLink, forceAnonymous, isAnonymous, handlerType, checkCacheBeforeLookup, updateCacheAfterLookup);
    }

    private JiraResponseHandler retrieveXMLResponse(String url, Set<String> columns, ReadOnlyApplicationLink appLink, boolean forceAnonymous, boolean isAnonymous, JiraResponseHandler.HandlerType handlerType, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        String absoluteUrl;
        String finalUrl = this.getFieldRestrictedUrl(columns, url);
        if (appLink != null && !forceAnonymous) {
            ApplicationLinkRequestFactory requestFactory = this.createRequestFactory(appLink, isAnonymous);
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, finalUrl);
            request.setConnectionTimeout(CONNECTION_TIMEOUT);
            try {
                JiraAppLinkResponseHandler jiraApplinkResponseHandler = new JiraAppLinkResponseHandler(handlerType, url, (AuthorisationURIGenerator)requestFactory);
                request.execute((ApplicationLinkResponseHandler)jiraApplinkResponseHandler);
                return jiraApplinkResponseHandler.getResponseHandler();
            }
            catch (ResponseException e) {
                Throwable t = e.getCause();
                if (t instanceof IOException) {
                    throw (IOException)t;
                }
                if (t instanceof CredentialsRequiredException) {
                    throw (CredentialsRequiredException)t;
                }
                throw e;
            }
        }
        boolean isRelativeUrl = !finalUrl.startsWith("http");
        boolean isValidAppLink = appLink != null;
        String string = absoluteUrl = isRelativeUrl && isValidAppLink ? appLink.getRpcUrl() + finalUrl : finalUrl;
        if (this.outboundWhitelist.isAllowed(URI.create(absoluteUrl))) {
            Request req = this.requestFactory.createRequest(Request.MethodType.GET, absoluteUrl);
            try {
                return (JiraResponseHandler)req.executeAndReturn(resp -> {
                    try {
                        JiraUtil.checkForErrors(resp, url);
                        JiraResponseHandler responseHandler = JiraUtil.createResponseHandler(handlerType, url);
                        responseHandler.handleJiraResponse(resp.getResponseBodyAsStream(), null);
                        return responseHandler;
                    }
                    catch (IOException ex) {
                        throw new ResponseException((Throwable)ex);
                    }
                });
            }
            catch (ResponseException ex) {
                Throwables.propagateIfPossible((Throwable)ex.getCause(), IOException.class);
                throw ex;
            }
        }
        throw new NotAuthorizedException(absoluteUrl);
    }

    protected ApplicationLinkRequestFactory createRequestFactory(ReadOnlyApplicationLink applicationLink, boolean isAnonymous) {
        if (isAnonymous) {
            return applicationLink.createAuthenticatedRequestFactory(Anonymous.class);
        }
        return applicationLink.createAuthenticatedRequestFactory();
    }

    public String getFieldRestrictedUrl(Set<String> columns, String url) {
        StringBuilder urlBuffer = new StringBuilder(url);
        boolean queryAllCustom = false;
        for (String column : columns) {
            if (this.isIdParamPresent()) {
                urlBuffer.append("&field=").append(JiraUtil.utf8Encode(column));
                continue;
            }
            String key = this.jiraIssuesColumnManager.getCanonicalFormOfBuiltInField(column);
            if (key.equals("key")) continue;
            if (key.equalsIgnoreCase("fixversion")) {
                urlBuffer.append("&field=").append("fixVersions");
                continue;
            }
            if (!this.jiraIssuesColumnManager.isColumnBuiltIn(key) && !queryAllCustom) {
                urlBuffer.append("&field=allcustom");
                queryAllCustom = true;
            }
            urlBuffer.append("&field=").append(JiraUtil.utf8Encode(key));
        }
        urlBuffer.append("&field=link");
        urlBuffer.append("&field=type");
        String urlAppended = urlBuffer.toString();
        LOGGER.debug("Jira issues request url is: " + urlAppended);
        return urlAppended;
    }

    @Override
    public Channel retrieveXMLAsChannel(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup) throws IOException, CredentialsRequiredException, ResponseException {
        JiraChannelResponseHandler handler = (JiraChannelResponseHandler)this.retrieveXML(url, columns, applink, forceAnonymous, false, JiraResponseHandler.HandlerType.CHANNEL_HANDLER, checkCacheBeforeLookup);
        return handler.getResponseChannel();
    }

    @Override
    public Channel retrieveXMLAsChannel(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        JiraChannelResponseHandler handler = (JiraChannelResponseHandler)this.retrieveXML(url, columns, applink, forceAnonymous, false, JiraResponseHandler.HandlerType.CHANNEL_HANDLER, checkCacheBeforeLookup, updateCacheAfterLookup);
        return handler.getResponseChannel();
    }

    @Override
    public Channel retrieveXMLAsChannelByAnonymous(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup) throws IOException, CredentialsRequiredException, ResponseException {
        JiraChannelResponseHandler handler = (JiraChannelResponseHandler)this.retrieveXML(url, columns, applink, forceAnonymous, true, JiraResponseHandler.HandlerType.CHANNEL_HANDLER, checkCacheBeforeLookup);
        return handler.getResponseChannel();
    }

    @Override
    public Channel retrieveXMLAsChannelByAnonymous(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        JiraChannelResponseHandler handler = (JiraChannelResponseHandler)this.retrieveXML(url, columns, applink, forceAnonymous, true, JiraResponseHandler.HandlerType.CHANNEL_HANDLER, checkCacheBeforeLookup, updateCacheAfterLookup);
        return handler.getResponseChannel();
    }

    @Override
    public String retrieveXMLAsString(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup) throws IOException, CredentialsRequiredException, ResponseException {
        JiraStringResponseHandler handler = (JiraStringResponseHandler)this.retrieveXML(url, columns, applink, forceAnonymous, false, JiraResponseHandler.HandlerType.STRING_HANDLER, checkCacheBeforeLookup);
        return handler.getResponseBody();
    }

    @Override
    public String retrieveXMLAsString(String url, Set<String> columns, ReadOnlyApplicationLink applink, boolean forceAnonymous, boolean checkCacheBeforeLookup, boolean updateCacheAfterLookup) throws IOException, CredentialsRequiredException, ResponseException {
        JiraStringResponseHandler handler = (JiraStringResponseHandler)this.retrieveXML(url, columns, applink, forceAnonymous, false, JiraResponseHandler.HandlerType.STRING_HANDLER, checkCacheBeforeLookup, updateCacheAfterLookup);
        return handler.getResponseBody();
    }

    @Override
    public String retrieveJQLFromFilter(String filterId, ReadOnlyApplicationLink appLink) throws ResponseException {
        JsonObject jsonObject;
        String url = appLink.getRpcUrl() + "/rest/api/2/filter/" + filterId;
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, url);
            jsonObject = (JsonObject)new JsonParser().parse(request.execute());
        }
        catch (CredentialsRequiredException e) {
            jsonObject = this.retrieveFilerByAnonymous(appLink, url);
        }
        catch (Exception e) {
            throw new ResponseException((Throwable)e);
        }
        return jsonObject.get("jql").getAsString();
    }

    @Override
    public String executeJqlQuery(String jqlQuery, ReadOnlyApplicationLink applicationLink) throws CredentialsRequiredException, ResponseException {
        String restUrl = "/rest/api/2/search?" + jqlQuery;
        ApplicationLinkRequestFactory applicationLinkRequestFactory = applicationLink.createAuthenticatedRequestFactory();
        ApplicationLinkRequest applicationLinkRequest = applicationLinkRequestFactory.createRequest(Request.MethodType.GET, restUrl);
        return (String)applicationLinkRequest.executeAndReturn(Response::getResponseBodyAsString);
    }

    private JsonObject retrieveFilerByAnonymous(ReadOnlyApplicationLink appLink, String url) throws ResponseException {
        try {
            ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, url);
            return (JsonObject)new JsonParser().parse(request.execute());
        }
        catch (Exception e) {
            throw new ResponseException((Throwable)e);
        }
    }

    @Override
    public List<JiraIssueBean> createIssues(List<JiraIssueBean> jiraIssueBeans, ReadOnlyApplicationLink appLink) throws CredentialsRequiredException, ResponseException {
        if (jiraIssueBeans == null || jiraIssueBeans.size() == 0) {
            throw new IllegalArgumentException("List of Jira issues cannot be empty");
        }
        if (jiraIssueBeans.size() > 1 && this.isSupportBatchIssue(appLink).booleanValue()) {
            return this.createIssuesInBatch(jiraIssueBeans, appLink);
        }
        return this.createIssuesInSingle(jiraIssueBeans, appLink);
    }

    private List<JiraIssueBean> createIssuesInSingle(List<JiraIssueBean> jiraIssueBeans, ReadOnlyApplicationLink appLink) throws CredentialsRequiredException, ResponseException {
        ApplicationLinkRequest request = this.createRequest(appLink, Request.MethodType.POST, CREATE_JIRA_ISSUE_URL);
        request.addHeader("Content-Type", "application/json");
        for (JiraIssueBean jiraIssueBean : jiraIssueBeans) {
            this.createAndUpdateResultForJiraIssue(request, jiraIssueBean);
        }
        return jiraIssueBeans;
    }

    private List<JiraIssueBean> createIssuesInBatch(List<JiraIssueBean> jiraIssueBeans, ReadOnlyApplicationLink appLink) throws CredentialsRequiredException, ResponseException {
        ApplicationLinkRequest applinkRequest = this.createRequest(appLink, Request.MethodType.POST, CREATE_JIRA_ISSUE_BATCH_URL);
        applinkRequest.addHeader("Content-Type", "application/json");
        JsonArray jsonIssues = new JsonArray();
        for (JiraIssueBean jiraIssueBean : jiraIssueBeans) {
            String jiraIssueJson = JiraUtil.createJsonStringForJiraIssueBean(jiraIssueBean);
            JsonObject jsonObject = new JsonParser().parse(jiraIssueJson).getAsJsonObject();
            jsonIssues.add((JsonElement)jsonObject);
        }
        JsonObject rootIssueJson = new JsonObject();
        rootIssueJson.add("issueUpdates", (JsonElement)jsonIssues);
        applinkRequest.setRequestBody(rootIssueJson.toString());
        String jiraIssueResponseString = this.executeApplinkRequest(applinkRequest);
        this.updateResultForJiraIssueInBatch(jiraIssueBeans, jiraIssueResponseString);
        return jiraIssueBeans;
    }

    private ApplicationLinkRequest createRequest(ReadOnlyApplicationLink appLink, Request.MethodType methodType, String baseRestUrl) throws CredentialsRequiredException {
        ApplicationLinkRequest request;
        String url = appLink.getRpcUrl() + baseRestUrl;
        ApplicationLinkRequestFactory requestFactory = appLink.createAuthenticatedRequestFactory();
        try {
            request = requestFactory.createRequest(methodType, url);
        }
        catch (CredentialsRequiredException e) {
            requestFactory = appLink.createAuthenticatedRequestFactory(Anonymous.class);
            request = requestFactory.createRequest(methodType, url);
        }
        return request;
    }

    private String executeApplinkRequest(ApplicationLinkRequest appLinkRequest) throws ResponseException {
        return (String)appLinkRequest.executeAndReturn(response -> {
            if (response.isSuccessful() || response.getStatusCode() == 400) {
                return response.getResponseBodyAsString();
            }
            throw new ResponseException(String.format("Execute applink with error! [statusCode=%s, statusText=%s]", response.getStatusCode(), response.getStatusText()));
        });
    }

    private Boolean isCreateIssueBatchUrlAvailable(ReadOnlyApplicationLink appLink) throws CredentialsRequiredException {
        ApplicationLinkRequest applinkRequest = this.createRequest(appLink, Request.MethodType.GET, CREATE_JIRA_ISSUE_BATCH_URL);
        try {
            return (Boolean)applinkRequest.executeAndReturn(response -> response.getStatusCode() == 405 || response.isSuccessful());
        }
        catch (ResponseException e) {
            return false;
        }
    }

    private void updateResultForJiraIssueInBatch(List<JiraIssueBean> jiraIssueBeansInput, String jiraIssueResponseString) throws ResponseException {
        JsonObject returnIssuesJson = new JsonParser().parse(jiraIssueResponseString).getAsJsonObject();
        JsonArray errorsJson = returnIssuesJson.getAsJsonArray("errors");
        for (JsonElement errorElement : errorsJson) {
            JsonObject errorObj = errorElement.getAsJsonObject();
            int errorAt = errorObj.get("failedElementNumber").getAsInt();
            Map<String, String> errorMessages = this.parseErrorMessages(errorObj.getAsJsonObject("elementErrors").getAsJsonObject("errors"));
            jiraIssueBeansInput.get(errorAt).setErrors(errorMessages);
        }
        JsonArray issuesJson = returnIssuesJson.getAsJsonArray("issues");
        int successItemIndex = 0;
        for (JiraIssueBean jiraIssueBean : jiraIssueBeansInput) {
            if (jiraIssueBean.getErrors() != null && !jiraIssueBean.getErrors().isEmpty()) continue;
            String jsonIssueString = issuesJson.get(successItemIndex++).toString();
            try {
                BasicJiraIssueBean basicJiraIssueBeanReponse = JiraUtil.createBasicJiraIssueBeanFromResponse(jsonIssueString);
                JiraUtil.updateJiraIssue(jiraIssueBean, basicJiraIssueBeanReponse);
            }
            catch (IOException e) {
                throw new ResponseException("There is a problem processing the response from Jira: unrecognisable response:" + jsonIssueString, (Throwable)e);
            }
        }
    }

    private void createAndUpdateResultForJiraIssue(ApplicationLinkRequest applinkRequest, JiraIssueBean jiraIssueBean) throws ResponseException {
        String jiraIssueJson = JiraUtil.createJsonStringForJiraIssueBean(jiraIssueBean);
        applinkRequest.setRequestBody(jiraIssueJson);
        String jiraIssueResponseString = this.executeApplinkRequest(applinkRequest);
        JsonObject returnIssueJson = new JsonParser().parse(jiraIssueResponseString).getAsJsonObject();
        if (returnIssueJson.has("errors")) {
            jiraIssueBean.setErrors(this.parseErrorMessages(returnIssueJson.getAsJsonObject("errors")));
        } else {
            try {
                BasicJiraIssueBean basicJiraIssueBeanReponse = JiraUtil.createBasicJiraIssueBeanFromResponse(jiraIssueResponseString);
                JiraUtil.updateJiraIssue(jiraIssueBean, basicJiraIssueBeanReponse);
            }
            catch (IOException e) {
                throw new ResponseException("There is a problem processing the response from Jira: unrecognisable response:" + returnIssueJson, (Throwable)e);
            }
        }
    }

    private Map<String, String> parseErrorMessages(JsonObject jsonError) {
        HashMap errors = Maps.newHashMap();
        for (Map.Entry errorEntry : jsonError.entrySet()) {
            String field = (String)errorEntry.getKey();
            String errorMessage = ((JsonElement)errorEntry.getValue()).getAsString();
            errors.put(field, errorMessage);
        }
        return errors;
    }

    protected Boolean isSupportBatchIssue(ReadOnlyApplicationLink appLink) {
        return (Boolean)this.getBatchIssueCapableCache().getUnchecked((Object)appLink);
    }

    private LoadingCache<ReadOnlyApplicationLink, Boolean> getBatchIssueCapableCache() {
        if (this.batchIssueCapableCache == null) {
            this.batchIssueCapableCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build((CacheLoader)new CacheLoader<ReadOnlyApplicationLink, Boolean>(){

                public Boolean load(ReadOnlyApplicationLink appLink) {
                    try {
                        return DefaultJiraIssuesManager.this.isCreateIssueBatchUrlAvailable(appLink);
                    }
                    catch (CredentialsRequiredException e) {
                        return false;
                    }
                }
            });
        }
        return this.batchIssueCapableCache;
    }

    @Override
    public void setIdParamPresent(boolean idParamPresent) {
        this.idParamPresent = idParamPresent;
    }

    @Override
    public boolean isIdParamPresent() {
        return this.idParamPresent;
    }
}

