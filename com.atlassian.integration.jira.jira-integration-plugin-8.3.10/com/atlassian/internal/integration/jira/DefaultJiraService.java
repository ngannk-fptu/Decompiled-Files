/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.applinks.api.application.jira.JiraProjectEntityType
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  io.atlassian.fugue.Either
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.ArrayNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.application.jira.JiraProjectEntityType;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.integration.jira.JiraAuthenticationRequiredException;
import com.atlassian.integration.jira.JiraCommunicationException;
import com.atlassian.integration.jira.JiraErrors;
import com.atlassian.integration.jira.JiraException;
import com.atlassian.integration.jira.JiraFeature;
import com.atlassian.integration.jira.JiraIssueUrlsRequest;
import com.atlassian.integration.jira.JiraIssuesRequest;
import com.atlassian.integration.jira.JiraKeyScanner;
import com.atlassian.integration.jira.JiraMultipleAuthenticationException;
import com.atlassian.integration.jira.JiraMultipleCommunicationException;
import com.atlassian.integration.jira.JiraValidationException;
import com.atlassian.integration.jira.applinks.LinkableEntityResolver;
import com.atlassian.integration.jira.applinks.UserAccessResolver;
import com.atlassian.internal.integration.jira.IconRequest;
import com.atlassian.internal.integration.jira.InternalJiraService;
import com.atlassian.internal.integration.jira.JiraConfig;
import com.atlassian.internal.integration.jira.JiraJsonObjectEntityLinkBasedComparator;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProvider;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProviderRegistry;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import com.atlassian.internal.integration.jira.request.AbstractJiraPagedRequest;
import com.atlassian.internal.integration.jira.request.MyAssignedJiraIssuesRequest;
import com.atlassian.internal.integration.jira.request.ProjectIssueTypeMetaRequest;
import com.atlassian.internal.integration.jira.request.ProjectIssueTypeRequest;
import com.atlassian.internal.integration.jira.util.JiraIconPathUtils;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.atlassian.fugue.Either;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJiraService
implements InternalJiraService {
    private static final String CAPABILITY_LIST_ISSUETYPE_FIELDS = "list-issuetype-fields";
    private static final String CAPABILITY_LIST_PROJECT_ISSUETYPES = "list-project-issuetypes";
    private static final String EMPTY_JSON_LIST = "[]";
    private static final String EMPTY_JSON_OBJECT = "{}";
    private static final String EXPAND_DEFAULT_VALUE = "renderedFields,transitions";
    private static final String FIELD_META_TYPES = "projects.issuetypes";
    private static final String FIELD_META_TYPES_FIELDS = "projects.issuetypes.fields";
    private static final String FIELD_TRANSITIONS = "transitions";
    private static final Set<String> FIELDS_DEFAULT_VALUE = ImmutableSet.of((Object)"assignee", (Object)"description", (Object)"issuetype", (Object)"priority", (Object)"project", (Object)"status", (Object[])new String[]{"summary", "transitions"});
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final long ISSUE_CREATION_SUPPORTED_BUILD_NUMBER = 710L;
    private static final String ISSUE_KEY_JQL_FORMAT = "issuekey IN (%s) ORDER BY issuekey";
    private static final String JSON_PATH_BUILD_NUMBER = "buildNumber";
    private static final String MY_ASSIGNED_ISSUES_JQL = "assignee = currentUser() AND statusCategory != \"done\" ORDER BY updated DESC";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String PARAM_EXPAND = "expand";
    private static final String PARAM_FIELDS = "fields";
    private static final String PARAM_ICON_TYPE = "iconType";
    private static final String PARAM_JQL = "jql";
    private static final String PARAM_MAX_RESULTS = "maxResults";
    private static final String PARAM_SERVER_ID = "serverId";
    private static final String PARAM_VALIDATE_QUERY = "validateQuery";
    private static final URI REST_CAPABILITIES_URI = UriBuilder.fromUri((String)"/rest/capabilities").build(new Object[0]);
    private static final String REST_CREATE_ISSUE_BULK_PATH = "/rest/api/2/issue/bulk";
    private static final String REST_CREATEMETA_PATH = "/rest/api/2/issue/createmeta";
    private static final String REST_CREATEMETA_ISSUETYPES_PATH = "issuetypes";
    private static final String REST_ISSUE_PATH = "/rest/api/2/issue";
    private static final URI REST_GET_PROJECTS_URI = UriBuilder.fromUri((String)"/rest/api/2/project").build(new Object[0]);
    private static final String REST_JQL_PATH = "/rest/api/2/search";
    private static final String REST_SERVER_INFO_PATH = "/rest/api/2/serverInfo";
    private static final Set<String> SUMMARY_FIELDS = ImmutableSet.of((Object)"summary");
    private static final Logger log = LoggerFactory.getLogger(DefaultJiraService.class);
    private ApplicationLinkService applicationLinkService;
    private AutoCompleteDataProviderRegistry autoCompleteRegistry;
    private JiraConfig config;
    private EntityLinkService entityLinkService;
    private LinkableEntityResolver entityResolver;
    private I18nResolver i18nResolver;
    private JiraKeyScanner keyScanner;
    private UserAccessResolver userAccessResolver = new NoAnonymousAccessResolver();
    private UserManager userManager;

    @Override
    @Nonnull
    public String createIssue(@Nonnull ApplicationId id, @Nonnull String createIssueRequestJson) {
        Preconditions.checkNotNull((Object)id, (Object)"id");
        Preconditions.checkNotNull((Object)createIssueRequestJson, (Object)"createIssueRequestJson");
        JSONObject request = new JSONObject();
        try {
            request.put("issues", new JSONObject(createIssueRequestJson));
        }
        catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON input: " + e.getMessage());
        }
        return this.createIssues(id, request.toString());
    }

    @Override
    @Nonnull
    public Set<String> findValidIssues(@Nonnull Set<String> issueKeys, @Nonnull ApplicationId id) {
        Preconditions.checkNotNull(issueKeys, (Object)"issueKeys");
        Preconditions.checkNotNull((Object)id, (Object)"id");
        if (issueKeys.isEmpty()) {
            return Collections.emptySet();
        }
        ApplicationLink jiraLink = this.getJiraLink(id);
        if (jiraLink == null) {
            throw new NoSuchElementException("Application link " + id + " not found");
        }
        List batchedIssuesKeys = Lists.partition(new ArrayList<String>(issueKeys), (int)this.config.getMaxIssues());
        ArrayList communicationExceptions = Lists.newArrayList();
        ArrayList authenticationExceptions = Lists.newArrayList();
        ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        HashSet result = Sets.newHashSet();
        batchedIssuesKeys.forEach(batch -> {
            try {
                Either<String, JiraErrors> responseResult = this.retrieveIssuesFromJira((Set<String>)ImmutableSet.copyOf((Collection)batch), SUMMARY_FIELDS, false, jiraLink, requestFactory);
                if (responseResult.isRight()) {
                    throw this.newCommunicationException(jiraLink);
                }
                result.addAll(this.extractKeys((String)responseResult.left().get()));
            }
            catch (CredentialsRequiredException e) {
                throw this.newAuthenticationRequiredException(jiraLink, requestFactory);
            }
            catch (JiraCommunicationException e) {
                throw this.newCommunicationException(jiraLink);
            }
            catch (ResponseException e) {
                DefaultJiraService.logCommunicationIssue(jiraLink, e.getMessage());
                throw this.newCommunicationException(jiraLink, e.getMessage());
            }
        });
        return result;
    }

    @Override
    @Nonnull
    public String createIssues(@Nonnull ApplicationId id, @Nonnull String createIssueRequestJson) {
        JSONArray issuesJson;
        JSONObject issueJson;
        ApplicationLink jiraLink;
        block19: {
            Preconditions.checkNotNull((Object)id);
            Preconditions.checkArgument((!((String)Preconditions.checkNotNull((Object)createIssueRequestJson, (Object)"jsonCreateIssueRequest")).trim().isEmpty() ? 1 : 0) != 0, (Object)"A non-blank json request required");
            jiraLink = this.getJiraLink(id);
            issueJson = null;
            issuesJson = null;
            try {
                JSONObject rootObjectJson = new JSONObject(createIssueRequestJson);
                Preconditions.checkArgument((boolean)rootObjectJson.has("issues"), (Object)"Missing key 'issues'");
                if (rootObjectJson.get("issues") instanceof JSONArray) {
                    issuesJson = rootObjectJson.getJSONArray("issues");
                    break block19;
                }
                if (rootObjectJson.get("issues") instanceof JSONObject) {
                    issueJson = rootObjectJson.getJSONObject("issues");
                    break block19;
                }
                throw new IllegalArgumentException("Invalid JSON input: issues value was not a JSON object or JSON array");
            }
            catch (JSONException e) {
                throw new IllegalArgumentException("Invalid JSON input: " + e.getMessage());
            }
        }
        JSONArray issues = new JSONArray();
        JSONArray errors = new JSONArray();
        if (issuesJson != null) {
            Either<String, JiraErrors> bulkCreateResult = this.createIssues(id, issuesJson);
            if (bulkCreateResult.isLeft()) {
                return (String)bulkCreateResult.left().get();
            }
            int responseStatusCode = ((JiraErrors)bulkCreateResult.right().get()).getResponseCode();
            if (responseStatusCode != 404 && responseStatusCode != 405) {
                throw this.newCommunicationException(jiraLink);
            }
            try {
                for (int elementNumber = 0; elementNumber < issuesJson.length(); ++elementNumber) {
                    JSONObject loopingIssueJson = issuesJson.getJSONObject(elementNumber);
                    Either<String, JiraErrors> createResult = this.createIssue(id, loopingIssueJson);
                    if (createResult.isRight()) {
                        JSONObject error = new JSONObject();
                        error.put("elementErrors", this.getErrorJson((JiraErrors)createResult.right().get()));
                        error.put("failedElement", loopingIssueJson);
                        error.put("failedElementNumber", elementNumber);
                        errors.put(error);
                        continue;
                    }
                    JSONObject singleIssueCreateResult = new JSONObject((String)createResult.left().get());
                    JSONObject issue = new JSONObject();
                    issue.put("elementNumber", elementNumber);
                    issue.put("issue", singleIssueCreateResult);
                    issues.put(issue);
                }
            }
            catch (JSONException e) {
                throw this.newCommunicationException(jiraLink);
            }
        }
        Either<String, JiraErrors> createResult = this.createIssue(id, issueJson);
        if (createResult.isLeft()) {
            try {
                JSONObject issue = new JSONObject();
                issue.put("issue", new JSONObject((String)createResult.left().get()));
                issue.put("elementNumber", 0);
                issues.put(issue);
            }
            catch (JSONException e) {
                throw this.newCommunicationException(jiraLink);
            }
        }
        try {
            JSONObject errorJson = new JSONObject();
            errorJson.put("elementErrors", this.getErrorJson((JiraErrors)createResult.right().get()));
            errorJson.put("failedElement", issueJson);
            errorJson.put("failedElementNumber", 0);
            errors.put(errorJson);
        }
        catch (JSONException e) {
            throw new IllegalStateException("Exception while rendering errors");
        }
        JSONObject result = new JSONObject();
        try {
            if (errors.length() > 0) {
                result.put("errors", errors);
            }
            result.put("issues", issues);
        }
        catch (JSONException e) {
            throw new IllegalStateException("Exception while rendering errors");
        }
        return result.toString();
    }

    @Override
    public URI findIssue(@Nonnull String issueKey, String firstApplicationId) {
        Preconditions.checkArgument((!((String)Preconditions.checkNotNull((Object)issueKey, (Object)"issueKey")).trim().isEmpty() ? 1 : 0) != 0, (Object)"A non-blank issue key is required");
        List<ApplicationLink> jiraLinks = this.getJiraLinks(null);
        if (jiraLinks.isEmpty()) {
            return null;
        }
        if (jiraLinks.size() == 1) {
            return URI.create(this.buildIssueUrl(jiraLinks.get(0), issueKey));
        }
        ArrayList communicationExceptions = Lists.newArrayList();
        for (ApplicationLink jiraLink : jiraLinks) {
            if (firstApplicationId != null) {
                if (!jiraLink.getId().get().equals(firstApplicationId)) continue;
                firstApplicationId = null;
            }
            ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
            try {
                try {
                    Either<String, JiraErrors> issueResult = this.retrieveIssuesFromJira(Sets.newHashSet((Object[])new String[]{issueKey}), SUMMARY_FIELDS, false, jiraLink, requestFactory);
                    if (!this.foundIssue(issueResult)) continue;
                    return URI.create(this.buildIssueUrl(jiraLink, issueKey));
                }
                catch (CredentialsRequiredException e) {
                    throw this.newAuthenticationRequiredException(jiraLink, requestFactory);
                }
            }
            catch (JiraAuthenticationRequiredException e) {
                return e.getAuthenticationUri(this.buildJumpUrl(issueKey, jiraLink));
            }
            catch (JiraCommunicationException e) {
                communicationExceptions.add(this.newCommunicationException(jiraLink));
            }
            catch (ResponseException e) {
                DefaultJiraService.logCommunicationIssue(jiraLink, e.getMessage());
                communicationExceptions.add(this.newCommunicationException(jiraLink, e.getMessage()));
            }
            catch (JSONException e) {
                log.warn("Problem parsing JSON response from Jira instance '{}' at '{}'. Error: {}", new Object[]{jiraLink.getName(), jiraLink.getDisplayUrl(), e.getMessage()});
                communicationExceptions.add(this.newCommunicationException(jiraLink, e.getMessage()));
            }
        }
        if (!communicationExceptions.isEmpty()) {
            throw this.newCommunicationException(communicationExceptions);
        }
        return null;
    }

    @Override
    @Nonnull
    public Collection<AutoCompleteItem> getAutoCompleteItems(@Nonnull RestAutoCompleteContext context) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        ApplicationId id = new ApplicationId(context.getServerId());
        return this.communicateWithJira(id, (jiraLink, requestFactory) -> {
            try {
                return this.getAutoCompleteItems(context, jiraLink, requestFactory);
            }
            catch (JSONException e) {
                return Collections.emptyList();
            }
        });
    }

    @Override
    @Nonnull
    public String getIssuesAsJson(@Nonnull JiraIssuesRequest request) {
        Preconditions.checkNotNull((Object)request, (Object)"request");
        List<ApplicationLink> jiraLinks = this.getJiraLinks(request.getEntityKey());
        if (jiraLinks.isEmpty()) {
            log.debug("No application link with Jira found");
            return EMPTY_JSON_LIST;
        }
        Set<Object> issueKeys = Sets.newLinkedHashSet((Iterable)Iterables.limit(request.getIssueKeys(), (int)this.config.getMaxIssues()));
        int minimum = request.hasMinimum() ? request.getMinimum() : issueKeys.size();
        ArrayList communicationExceptions = Lists.newArrayList();
        ArrayList authenticationExceptions = Lists.newArrayList();
        JSONArray response = new JSONArray();
        for (ApplicationLink jiraLink : jiraLinks) {
            ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
            try {
                boolean canTransition = jiraLink.createImpersonatingAuthenticatedRequestFactory() != null;
                Either<String, JiraErrors> responseResult = this.retrieveIssuesFromJira(issueKeys, request.getFields(), canTransition, jiraLink, requestFactory);
                if (responseResult.isRight()) {
                    Set<String> invalidKeys = this.extractInvalidKeys(((JiraErrors)responseResult.right().get()).errorMessages);
                    issueKeys.removeAll(invalidKeys);
                    if (issueKeys.size() > 0) {
                        responseResult = this.retrieveIssuesFromJira(issueKeys, request.getFields(), canTransition, jiraLink, requestFactory);
                        if (responseResult.isRight()) {
                            log.warn("The constructed JQL could not be parsed: {}", responseResult.right().get());
                        } else {
                            this.addToJsonArray(response, (String)responseResult.left().get(), jiraLink.getId(), canTransition);
                        }
                    }
                    issueKeys = invalidKeys;
                } else {
                    this.addToJsonArray(response, (String)responseResult.left().get(), jiraLink.getId(), canTransition);
                }
                if (response.length() < minimum) continue;
                break;
            }
            catch (CredentialsRequiredException e) {
                authenticationExceptions.add(this.newAuthenticationRequiredException(jiraLink, requestFactory));
            }
            catch (JiraAuthenticationRequiredException e) {
                authenticationExceptions.add(e);
            }
            catch (JiraCommunicationException e) {
                communicationExceptions.add(this.newCommunicationException(jiraLink));
            }
            catch (ResponseException e) {
                DefaultJiraService.logCommunicationIssue(jiraLink, e.getMessage());
                communicationExceptions.add(this.newCommunicationException(jiraLink, e.getMessage()));
            }
        }
        if (response.length() == 0) {
            if (!authenticationExceptions.isEmpty()) {
                throw this.newAuthenticationException(authenticationExceptions);
            }
            if (!communicationExceptions.isEmpty()) {
                throw this.newCommunicationException(communicationExceptions);
            }
            log.debug("None of the requested issues were found on any linked Jira server: {}", (Object)issueKeys);
        }
        response = this.sortResponse(response, request);
        if (request.showErrors()) {
            JSONObject responseObject = new JSONObject();
            try {
                responseObject.append("issues", response);
                JSONArray errors = this.getExceptionsJson(authenticationExceptions, communicationExceptions);
                if (errors.length() > 0) {
                    responseObject.append("errors", errors);
                }
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            return responseObject.toString();
        }
        return response.toString();
    }

    @Override
    @Nonnull
    public String getIssueTransitionsAsJson(@Nonnull String issueKey, @Nonnull ApplicationId applicationId) {
        Preconditions.checkArgument((!((String)Preconditions.checkNotNull((Object)issueKey, (Object)"issueKey")).trim().isEmpty() ? 1 : 0) != 0, (Object)"A non-blank issue key is required");
        Preconditions.checkNotNull((Object)applicationId, (Object)"applicationId");
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            Either<String, JiraErrors> jsonOrErrors = this.getTransitions(issueKey, jiraLink, requestFactory);
            if (jsonOrErrors.isRight()) {
                log.warn("Available transitions could not be retrieved: {}", jsonOrErrors.right().get());
                return "";
            }
            return (String)jsonOrErrors.left().get();
        });
    }

    @Override
    @Nonnull
    public Map<String, String> getIssueUrls(@Nonnull JiraIssueUrlsRequest request) {
        Preconditions.checkNotNull((Object)request, (Object)"request");
        List<ApplicationLink> jiraLinks = this.getJiraLinks(request.getEntityKey());
        if (jiraLinks.isEmpty()) {
            log.debug("No application link with Jira found");
            return Collections.emptyMap();
        }
        Set<String> issueKeys = request.getIssueKeys();
        HashMap urls = Maps.newHashMapWithExpectedSize((int)issueKeys.size());
        if (jiraLinks.size() == 1) {
            ApplicationLink jira = jiraLinks.get(0);
            for (String issueKey : issueKeys) {
                urls.put(issueKey, this.buildIssueUrl(jira, issueKey));
            }
        } else {
            for (String issueKey : issueKeys) {
                urls.put(issueKey, this.buildJumpUrl(issueKey, null).toString());
            }
        }
        return urls;
    }

    @Override
    @Nonnull
    public List<ApplicationLink> getJiraLinksForEntity(@Nonnull String entityKey) {
        Object entityObject;
        Preconditions.checkNotNull((Object)entityKey, (Object)"entityKey");
        ArrayList jiraLinks = Lists.newArrayList();
        if (this.entityResolver != null && StringUtils.isNotBlank((CharSequence)entityKey) && (entityObject = this.entityResolver.resolve(entityKey)) != null) {
            Iterable entityLinks = this.entityLinkService.getEntityLinks(entityObject, JiraProjectEntityType.class);
            for (EntityLink entityLink : entityLinks) {
                ApplicationLink link = entityLink.getApplicationLink();
                if (jiraLinks.contains(link)) continue;
                jiraLinks.add(link);
            }
        }
        return jiraLinks;
    }

    @Override
    @Nonnull
    public String getIssueTypesAsJson(@Nonnull ApplicationId applicationId, @Nonnull String project) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> (String)(this.checkCapability(applicationId, CAPABILITY_LIST_PROJECT_ISSUETYPES) ? this.getProjectIssueTypesAsJson(applicationId, project) : this.getProjectIssueTypesAsJsonLegacy(applicationId, project)).leftOr(JiraErrors::toString));
    }

    @Override
    @Nonnull
    public String getIssueTypeMetaAsJson(@Nonnull ApplicationId applicationId, @Nonnull String project, int issueTypeId) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> (String)(this.checkCapability(applicationId, CAPABILITY_LIST_ISSUETYPE_FIELDS) ? this.getProjectIssueTypeMetaAsJson(applicationId, project, issueTypeId) : this.getProjectIssueTypeMetaAsJsonLegacy(applicationId, project, issueTypeId)).leftOr(JiraErrors::toString));
    }

    @Override
    @Nonnull
    public String getProjectsAsJson(@Nonnull ApplicationId applicationId) {
        Preconditions.checkNotNull((Object)applicationId, (Object)"applicationId");
        return (String)(this.checkCapability(applicationId, CAPABILITY_LIST_PROJECT_ISSUETYPES) ? this.getProjectListAsJson(applicationId) : this.getProjectListAsJsonLegacy(applicationId)).leftOr(JiraErrors::toString);
    }

    @Override
    @Nonnull
    public String getServersAsJson() {
        boolean includeId;
        List<ApplicationLink> appLinks = this.getJiraLinks(null);
        JSONArray serverArray = new JSONArray();
        UserProfile remoteUser = this.userManager.getRemoteUser();
        try {
            if (remoteUser == null) {
                includeId = (Boolean)this.userManager.getClass().getMethod("isAnonymousAccessEnabled", new Class[0]).invoke((Object)this.userManager, new Object[0]);
            } else {
                boolean isLicensed = (Boolean)this.userManager.getClass().getMethod("isLicensed", UserKey.class).invoke((Object)this.userManager, remoteUser.getUserKey());
                boolean isLimitedUnlicensedUser = (Boolean)this.userManager.getClass().getMethod("isLimitedUnlicensedUser", UserKey.class).invoke((Object)this.userManager, remoteUser.getUserKey());
                includeId = isLicensed || isLimitedUnlicensedUser;
            }
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            log.debug("Need to update SAL UserManger version to 4.8 or 5.1. Error Message: {}", (Object)ex.getMessage());
            includeId = remoteUser != null || this.userAccessResolver.isAnonymousAccessAllowed();
        }
        for (ApplicationLink link : appLinks) {
            try {
                JSONObject server = new JSONObject().put("name", link.getName()).put("displayUrl", link.getDisplayUrl().toString()).put("rpcUrl", link.getRpcUrl().toString()).put("authenticated", this.isAuthenticated(link)).put("selected", link.isPrimary());
                if (includeId) {
                    server.put("id", link.getId().toString());
                }
                serverArray.put(server);
            }
            catch (JSONException e) {
                log.error("Unexpected exception while rendering JSON object: {}", (Object)e.getMessage());
            }
        }
        return serverArray.toString();
    }

    @Override
    @Nonnull
    public Set<JiraFeature> getSupportedFeatures(@Nonnull ApplicationId id) {
        Preconditions.checkNotNull((Object)id, (Object)"applicationId");
        return this.communicateWithJira(id, (jiraLink, requestFactory) -> {
            ApplicationLinkRequest appLinkRequest = requestFactory.createRequest(Request.MethodType.GET, REST_SERVER_INFO_PATH);
            this.config.configure(appLinkRequest);
            JsonNode serverInfo = (JsonNode)appLinkRequest.execute((ApplicationLinkResponseHandler)new ServerInfoResponseHandler(jiraLink));
            if (serverInfo != null && serverInfo.path(JSON_PATH_BUILD_NUMBER).getLongValue() >= 710L) {
                return ImmutableSet.of((Object)((Object)JiraFeature.CREATE_ISSUE), (Object)((Object)JiraFeature.TRANSITION_ISSUE));
            }
            return ImmutableSet.of((Object)((Object)JiraFeature.TRANSITION_ISSUE));
        });
    }

    @Override
    public boolean isLinked() {
        Iterable links = this.applicationLinkService.getApplicationLinks(JiraApplicationType.class);
        return links != null && !Iterables.isEmpty((Iterable)links);
    }

    @Override
    public void streamIcon(@Nonnull IconRequest request, @Nonnull HttpServletResponse response) {
        Preconditions.checkNotNull((Object)request.getServerId(), (Object)"applicationId");
        Preconditions.checkNotNull((Object)request.getIconType(), (Object)PARAM_ICON_TYPE);
        ApplicationId applicationId = new ApplicationId(request.getServerId());
        this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            try {
                ApplicationLinkRequest appLinkRequest = requestFactory.createRequest(Request.MethodType.GET, JiraIconPathUtils.getIconPath(request));
                this.config.configure(appLinkRequest);
                appLinkRequest.execute((ApplicationLinkResponseHandler)new JiraImageResponseHandler(requestFactory, jiraLink, response));
            }
            catch (UnsupportedEncodingException e) {
                log.error("Could not build Jira icon path: {}", (Object)e.getMessage());
            }
            return null;
        });
    }

    @Override
    @Nonnull
    public String transitionIssue(@Nonnull String issueKey, Set<String> fields, @Nonnull ApplicationId applicationId, @Nonnull String jsonTransitionRequest) {
        Preconditions.checkNotNull((Object)applicationId, (Object)"applicationId");
        Preconditions.checkArgument((!((String)Preconditions.checkNotNull((Object)issueKey, (Object)"issueKey")).trim().isEmpty() ? 1 : 0) != 0, (Object)"A non-blank issue key is required");
        Preconditions.checkArgument((!((String)Preconditions.checkNotNull((Object)jsonTransitionRequest, (Object)"jsonTransitionRequest")).trim().isEmpty() ? 1 : 0) != 0, (Object)"Non-blank JSON describing the transition to apply is required");
        log.debug("Attempting to transition {} with JSON: {}", (Object)issueKey, (Object)jsonTransitionRequest);
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            Either<String, JiraErrors> jsonOrErrors = this.transitionIssueInJira(issueKey, jsonTransitionRequest, jiraLink, requestFactory);
            if (jsonOrErrors.isRight()) {
                log.error("{} could not be transitioned: {}", (Object)issueKey, jsonOrErrors.right().get());
                throw this.newValidationException((JiraErrors)jsonOrErrors.right().get());
            }
            log.debug("Transition response for {}: {}", (Object)issueKey, jsonOrErrors.left().get());
            jsonOrErrors = this.retrieveIssuesFromJira(Sets.newHashSet((Object[])new String[]{issueKey}), fields, true, jiraLink, requestFactory);
            if (jsonOrErrors.isRight()) {
                log.error("Details for {} could not be retrieved after transitioning: {}", (Object)issueKey, jsonOrErrors.right().get());
                throw this.newValidationException((JiraErrors)jsonOrErrors.right().get());
            }
            try {
                JSONArray issues = new JSONArray((String)jsonOrErrors.left().get());
                if (issues.length() == 1) {
                    return this.populateJsonIssue(issues.getJSONObject(0), applicationId, true).toString();
                }
            }
            catch (JSONException e) {
                log.error("Issue JSON could not be parsed. JSON due to {}:\n{}", (Object)e.getMessage(), jsonOrErrors.left().get());
            }
            return "";
        });
    }

    @Override
    @Nonnull
    public String getMyAssignedIssues(@Nonnull MyAssignedJiraIssuesRequest request) {
        Preconditions.checkNotNull((Object)request, (Object)"request");
        ApplicationLink jiraLink = this.getJiraLink(request.getApplicationId());
        if (jiraLink == null) {
            throw new NoSuchElementException("Application link " + request.getApplicationId() + " not found");
        }
        ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        UtilTimerStack.push((String)this.getFetchMyIssuesProfileKey(jiraLink));
        try {
            ApplicationLinkRequest appRequest = requestFactory.createRequest(Request.MethodType.GET, DefaultJiraService.buildGetMyIssueJqlRequestUrl(request).toString());
            this.config.configure(appRequest);
            Either jiraRequestResult = (Either)appRequest.execute((ApplicationLinkResponseHandler)new JiraResponseHandler(requestFactory, jiraLink));
            if (jiraRequestResult == null) {
                throw this.newCommunicationException(jiraLink, "Request execution failed with null response");
            }
            if (jiraRequestResult.isLeft()) {
                String string = (String)jiraRequestResult.left().get();
                return string;
            }
            try {
                throw this.newValidationException((JiraErrors)jiraRequestResult.right().get());
            }
            catch (CredentialsRequiredException e) {
                throw this.newAuthenticationRequiredException(jiraLink, requestFactory);
            }
            catch (ResponseException e) {
                throw this.newCommunicationException(jiraLink, e.getMessage());
            }
        }
        finally {
            UtilTimerStack.pop((String)this.getFetchMyIssuesProfileKey(jiraLink));
        }
    }

    @Autowired
    public void setApplicationLinkService(ApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
    }

    @Autowired
    public void setAutoCompleteRegistry(AutoCompleteDataProviderRegistry autoCompleteRegistry) {
        this.autoCompleteRegistry = autoCompleteRegistry;
    }

    @Autowired
    public void setEntityLinkService(EntityLinkService entityLinkService) {
        this.entityLinkService = entityLinkService;
    }

    @Autowired
    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    @Autowired
    public void setJiraConfig(JiraConfig config) {
        this.config = config;
    }

    @Autowired
    public void setJiraKeyScanner(JiraKeyScanner keyScanner) {
        this.keyScanner = keyScanner;
    }

    @Autowired(required=false)
    public void setLinkableEntityResolver(LinkableEntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    @Autowired(required=false)
    public void setUserAccessResolver(UserAccessResolver userAccessResolver) {
        this.userAccessResolver = (UserAccessResolver)MoreObjects.firstNonNull((Object)userAccessResolver, (Object)this.userAccessResolver);
    }

    @Autowired(required=true)
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @VisibleForTesting
    static URI buildGetMyIssueJqlRequestUrl(MyAssignedJiraIssuesRequest request) {
        Set<String> fields = request.getFields().isEmpty() ? FIELDS_DEFAULT_VALUE : request.getFields();
        UriBuilder builder = UriBuilder.fromUri((String)REST_JQL_PATH).queryParam(PARAM_JQL, new Object[]{MY_ASSIGNED_ISSUES_JQL}).queryParam(PARAM_FIELDS, new Object[]{StringUtils.join(fields, (String)",")}).queryParam(PARAM_VALIDATE_QUERY, new Object[]{false});
        DefaultJiraService.addPagingParams(builder, request);
        return builder.build(new Object[0]);
    }

    @VisibleForTesting
    static URI buildJqlRequestUrl(Set<String> issueKeys, Set<String> fields, boolean canTransition) {
        Set<String> fieldList = fields == null || fields.isEmpty() ? FIELDS_DEFAULT_VALUE : fields;
        String fieldsParamValue = ((Set)MoreObjects.firstNonNull(fields, FIELDS_DEFAULT_VALUE)).stream().filter(s -> canTransition || !s.equals(FIELD_TRANSITIONS)).collect(Collectors.joining(","));
        return UriBuilder.fromUri((String)REST_JQL_PATH).queryParam(PARAM_JQL, new Object[]{String.format(ISSUE_KEY_JQL_FORMAT, StringUtils.join(issueKeys, (String)","))}).queryParam(PARAM_FIELDS, new Object[]{fieldsParamValue}).queryParam(PARAM_EXPAND, new Object[]{EXPAND_DEFAULT_VALUE}).queryParam(PARAM_VALIDATE_QUERY, new Object[]{false}).build(new Object[0]);
    }

    @VisibleForTesting
    URI buildJumpUrl(String issueKey, ApplicationLink nextLink) {
        UriBuilder builder = UriBuilder.fromUri((String)this.config.getBaseUrl()).path("/plugins/servlet/jira-integration/issues").path(issueKey);
        if (nextLink != null) {
            builder.queryParam("nextApplicationId", new Object[]{nextLink.getId()});
        }
        return builder.build(new Object[0]);
    }

    @VisibleForTesting
    static URI buildProjectIssueTypeMetaRestUrl(ProjectIssueTypeMetaRequest request) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)REST_CREATEMETA_PATH).path(request.getProject()).path(REST_CREATEMETA_ISSUETYPES_PATH).path(Integer.toString(request.getIssueTypeId()));
        DefaultJiraService.addPagingParams(uriBuilder, request);
        return uriBuilder.build(new Object[0]);
    }

    @VisibleForTesting
    static URI buildProjectIssueTypeRestUrl(ProjectIssueTypeRequest request) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)REST_CREATEMETA_PATH).path(request.getProject()).path(REST_CREATEMETA_ISSUETYPES_PATH);
        DefaultJiraService.addPagingParams(uriBuilder, request);
        return uriBuilder.build(new Object[0]);
    }

    @VisibleForTesting
    static URI buildTransitionRequestUrl(String issueKey) {
        return DefaultJiraService.buildTransitionRequestUrl(issueKey, null);
    }

    @VisibleForTesting
    static URI buildTransitionRequestUrl(String issueKey, String expand) {
        UriBuilder builder = UriBuilder.fromUri((String)REST_ISSUE_PATH).path(issueKey).path(FIELD_TRANSITIONS);
        if (expand != null) {
            builder.queryParam(PARAM_EXPAND, new Object[]{expand});
        }
        return builder.build(new Object[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Either<String, JiraErrors> retrieveIssuesFromJira(Set<String> issueKeys, Set<String> fields, boolean canTransition, ApplicationLink jiraLink, ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, ResponseException {
        UtilTimerStack.push((String)("Requesting details for " + issueKeys.size() + " issues from Jira server: " + jiraLink.getName()));
        try {
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, DefaultJiraService.buildJqlRequestUrl(issueKeys, fields, canTransition).toString());
            this.config.configure(request);
            Either jiraRequestResult = (Either)request.execute((ApplicationLinkResponseHandler)new JiraResponseHandler(requestFactory, jiraLink, "issues"));
            if (jiraRequestResult == null) {
                Either either = Either.right((Object)new JiraErrors().addError("Request execution failed with null response"));
                return either;
            }
            Either either = jiraRequestResult;
            return either;
        }
        finally {
            UtilTimerStack.push((String)("Requesting details for " + issueKeys.size() + " issues from Jira server: " + jiraLink.getName()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Either<String, JiraErrors> transitionIssueInJira(String issueKey, String jsonTransitionRequest, ApplicationLink jiraLink, ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, ResponseException {
        UtilTimerStack.push((String)("Transitioning issue " + issueKey + " on Jira server: " + jiraLink.getName()));
        try {
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.POST, DefaultJiraService.buildTransitionRequestUrl(issueKey).toString());
            this.config.configure(request);
            request.setHeader(HEADER_CONTENT_TYPE, "application/json");
            request.setRequestBody(jsonTransitionRequest);
            Either jiraRequestResult = (Either)request.execute((ApplicationLinkResponseHandler)new JiraResponseHandler(requestFactory, jiraLink));
            if (jiraRequestResult == null) {
                Either either = Either.right((Object)new JiraErrors().addError("Request execution failed with null response"));
                return either;
            }
            Either either = jiraRequestResult;
            return either;
        }
        finally {
            UtilTimerStack.pop((String)("Transitioning issue " + issueKey + " on Jira server: " + jiraLink.getName()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<AutoCompleteItem> getAutoCompleteItems(RestAutoCompleteContext context, ApplicationLink jiraLink, ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, ResponseException, JSONException {
        UtilTimerStack.push((String)("Get auto complete data for project " + context.getProjectKey() + " with issue type " + context.getIssueType() + " on Jira server: " + jiraLink.getName()));
        try {
            AutoCompleteDataProvider autoCompleteDataProvider = this.autoCompleteRegistry.getProvider(context.getRestType());
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, autoCompleteDataProvider.getUrl(context));
            this.config.configure(request);
            String json = request.execute();
            if (json == null) {
                log.warn("Applink request execution returned null");
                Collection<AutoCompleteItem> collection = autoCompleteDataProvider.parseData(EMPTY_JSON_OBJECT);
                return collection;
            }
            Collection<AutoCompleteItem> collection = autoCompleteDataProvider.parseData(json);
            return collection;
        }
        finally {
            UtilTimerStack.pop((String)("Get auto complete data for project " + context.getProjectKey() + " with issue type " + context.getIssueType() + " on Jira server: " + jiraLink.getName()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Either<String, JiraErrors> getTransitions(String issueKey, ApplicationLink jiraLink, ApplicationLinkRequestFactory requestFactory) throws CredentialsRequiredException, ResponseException {
        UtilTimerStack.push((String)("Requesting transitions for issue " + issueKey + " on Jira server: " + jiraLink.getName()));
        try {
            ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, DefaultJiraService.buildTransitionRequestUrl(issueKey, "transitions.fields").toString());
            this.config.configure(request);
            Either jiraRequestResult = (Either)request.execute((ApplicationLinkResponseHandler)new JiraResponseHandler(requestFactory, jiraLink, FIELD_TRANSITIONS));
            if (jiraRequestResult == null) {
                Either either = Either.right((Object)new JiraErrors().addError("Request execution failed with null response"));
                return either;
            }
            Either either = jiraRequestResult;
            return either;
        }
        finally {
            UtilTimerStack.pop((String)("Requesting transitions for issue " + issueKey + " on Jira server: " + jiraLink.getName()));
        }
    }

    private static void logCommunicationIssue(ApplicationLink jiraLink, String errorMessage) {
        log.warn("Problem communicating with Jira instance '{}' at '{}'. Error: {}", new Object[]{jiraLink.getName(), jiraLink.getDisplayUrl(), errorMessage});
    }

    private void addToJsonArray(JSONArray aggregateArray, String additionalJson, ApplicationId applicationId, boolean canTransition) {
        try {
            JSONArray additionalArray = new JSONArray(additionalJson);
            for (int i = 0; i < additionalArray.length(); ++i) {
                aggregateArray.put(this.populateJsonIssue(additionalArray.getJSONObject(i), applicationId, canTransition));
            }
        }
        catch (JSONException e) {
            log.error("Invalid JSON returned due to {}:\n{}", (Object)e.getMessage(), (Object)additionalJson);
        }
    }

    private String buildIssueUrl(ApplicationId applicationId, String issueKey) {
        ApplicationLink applicationLink = this.getJiraLink(applicationId);
        if (applicationLink == null) {
            throw new RuntimeException("Application link " + applicationId.toString() + " not found");
        }
        return this.buildIssueUrl(applicationLink, issueKey);
    }

    private String buildIssueUrl(ApplicationLink applicationLink, String issueKey) {
        return applicationLink.getDisplayUrl() + "/browse/" + issueKey;
    }

    private boolean checkCapability(@Nonnull ApplicationId applicationId, @Nonnull String capability) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            Either<String, JiraErrors> response = this.executeApplinkRequest(jiraLink, requestFactory, REST_CAPABILITIES_URI, new JiraResponseHandler(requestFactory, jiraLink, "capabilities"));
            return (Boolean)response.fold(responseJson -> {
                try {
                    return new JSONObject((String)responseJson).has(capability);
                }
                catch (JSONException e) {
                    return false;
                }
            }, errors -> {
                log.warn("Error when evaluating capabilities: {}", errors);
                return false;
            });
        });
    }

    private <T> T communicateWithJira(ApplicationId applicationId, JiraRequestFunction<T> makeRequest) {
        ApplicationLink jiraLink = this.getJiraLink(applicationId);
        if (jiraLink == null) {
            throw new NoSuchElementException("Application link " + applicationId.toString() + " not found");
        }
        ApplicationLinkRequestFactory requestFactory = jiraLink.createImpersonatingAuthenticatedRequestFactory();
        if (requestFactory == null && this.config.isBasicAuthenticationAllowed()) {
            requestFactory = jiraLink.createAuthenticatedRequestFactory(BasicAuthenticationProvider.class);
        }
        try {
            return makeRequest.apply(jiraLink, requestFactory);
        }
        catch (CredentialsRequiredException e) {
            throw this.newAuthenticationRequiredException(jiraLink, requestFactory);
        }
        catch (ResponseException e) {
            DefaultJiraService.logCommunicationIssue(jiraLink, e.getMessage());
            throw this.newCommunicationException(jiraLink);
        }
    }

    @Nonnull
    private Either<String, JiraErrors> createBulkIssues(@Nonnull ApplicationId id, @Nonnull JSONArray inputJson) {
        JSONObject bulkRequestJson = new JSONObject();
        try {
            bulkRequestJson.put("issueUpdates", inputJson);
        }
        catch (JSONException e) {
            throw new IllegalStateException("Exception while procssing JSONObject");
        }
        return this.communicateWithJira(id, (jiraLink, requestFactory) -> {
            ApplicationLinkRequest applicationLinkRequest = (ApplicationLinkRequest)((ApplicationLinkRequest)requestFactory.createRequest(Request.MethodType.POST, REST_CREATE_ISSUE_BULK_PATH).setHeader(HEADER_CONTENT_TYPE, "application/json")).setRequestBody(bulkRequestJson.toString());
            this.config.configure(applicationLinkRequest);
            Either jiraRequestResult = (Either)applicationLinkRequest.execute((ApplicationLinkResponseHandler)new JiraResponseHandler(requestFactory, jiraLink){

                @Override
                Either<String, JiraErrors> filter(Response response, String responseString) {
                    try {
                        JiraErrors errors = DefaultJiraService.this.getErrorFromBulkCreation(response, responseString, jiraLink);
                        if (errors.hasErrors()) {
                            return Either.right((Object)errors);
                        }
                    }
                    catch (JSONException e) {
                        throw DefaultJiraService.this.newCommunicationException(jiraLink);
                    }
                    return Either.left((Object)responseString);
                }
            });
            if (jiraRequestResult == null) {
                return Either.right((Object)new JiraErrors().addError("Request execution failed with null response"));
            }
            return jiraRequestResult;
        });
    }

    @Nonnull
    private Either<String, JiraErrors> createIssue(@Nonnull ApplicationId id, @Nonnull JSONObject inputJson) {
        return this.communicateWithJira(id, (jiraLink, requestFactory) -> {
            ApplicationLinkRequest applicationLinkRequest = (ApplicationLinkRequest)((ApplicationLinkRequest)requestFactory.createRequest(Request.MethodType.POST, REST_ISSUE_PATH).setHeader(HEADER_CONTENT_TYPE, "application/json")).setRequestBody(inputJson.toString());
            this.config.configure(applicationLinkRequest);
            Either jiraRequestResult = (Either)applicationLinkRequest.execute((ApplicationLinkResponseHandler)new JiraResponseHandler(requestFactory, jiraLink));
            if (jiraRequestResult == null) {
                return Either.right((Object)new JiraErrors().addError("Request execution failed with null response"));
            }
            return jiraRequestResult;
        });
    }

    @Nonnull
    private Either<String, JiraErrors> createIssues(@Nonnull ApplicationId id, @Nonnull JSONArray inputJson) {
        ArrayList issuesJson = Lists.newArrayList();
        ArrayList errorsJson = Lists.newArrayList();
        int bulkCount = inputJson.length();
        ArrayList bulkIssues = Lists.newArrayListWithCapacity((int)bulkCount);
        for (int i = 0; i < bulkCount; ++i) {
            try {
                bulkIssues.add(inputJson.getJSONObject(i));
                continue;
            }
            catch (JSONException e) {
                throw new IllegalStateException("Exception while processing JSONObject");
            }
        }
        int currentStartIndex = 0;
        for (List partition : Lists.partition((List)bulkIssues, (int)this.config.getMaxBulkIssues())) {
            JSONArray bulkJson = new JSONArray(partition);
            Either<String, JiraErrors> bulkCreateResult = this.createBulkIssues(id, bulkJson);
            if (bulkCreateResult.isRight()) {
                return bulkCreateResult;
            }
            try {
                JSONObject responseJson = new JSONObject((String)bulkCreateResult.left().get());
                JSONArray issuesNode = responseJson.getJSONArray("issues");
                for (int issueIndex = 0; issueIndex < issuesNode.length(); ++issueIndex) {
                    JSONObject issue = new JSONObject();
                    JSONObject issueNode = issuesNode.getJSONObject(issueIndex);
                    issue.put("issue", issueNode);
                    issue.put("elementNumber", issueIndex + currentStartIndex);
                    issuesJson.add(issue);
                }
                JSONArray errorsNode = responseJson.getJSONArray("errors");
                for (int errorIndex = 0; errorIndex < errorsNode.length(); ++errorIndex) {
                    JSONObject errorNode = errorsNode.getJSONObject(errorIndex);
                    int failedElementNumber = errorNode.getInt("failedElementNumber") + currentStartIndex;
                    JSONObject error = new JSONObject();
                    error.put("elementErrors", errorNode.get("elementErrors"));
                    error.put("failedElementNumber", failedElementNumber);
                    error.put("failedElement", inputJson.get(failedElementNumber));
                    errorsJson.add(error);
                }
                currentStartIndex += partition.size();
            }
            catch (JSONException e) {
                throw new IllegalStateException("Exception while processing JSONObject");
            }
        }
        JSONObject wrapperJson = new JSONObject();
        try {
            wrapperJson.put("issues", issuesJson);
            if (!errorsJson.isEmpty()) {
                wrapperJson.put("errors", errorsJson);
            }
            return Either.left((Object)wrapperJson.toString());
        }
        catch (JSONException e) {
            throw new IllegalStateException("Exception while processing JSONObject");
        }
    }

    private Set<String> extractInvalidKeys(List<String> errorMessages) {
        LinkedHashSet invalidKeys = Sets.newLinkedHashSet();
        for (String errorMessage : errorMessages) {
            for (String jiraKey : this.keyScanner.findAll(errorMessage)) {
                invalidKeys.add(jiraKey);
            }
        }
        return invalidKeys;
    }

    private Set<String> extractKeys(String jsonResponse) {
        HashSet keys = Sets.newHashSet();
        for (String jiraKey : this.keyScanner.findAll(jsonResponse)) {
            keys.add(jiraKey);
        }
        return keys;
    }

    private boolean foundIssue(Either<String, JiraErrors> issueResult) throws JSONException {
        return issueResult.isLeft() && new JSONArray((String)issueResult.left().get()).length() > 0;
    }

    private Either<String, JiraErrors> executeApplinkRequest(@Nonnull ApplicationLink jiraLink, @Nonnull ApplicationLinkRequestFactory requestFactory, @Nonnull URI url, @Nonnull ApplicationLinkResponseHandler<Either<String, JiraErrors>> responseHandler) throws CredentialsRequiredException, ResponseException {
        ApplicationLinkRequest appLinkRequest = requestFactory.createRequest(Request.MethodType.GET, url.toString());
        this.config.configure(appLinkRequest);
        Either result = (Either)appLinkRequest.execute(responseHandler);
        if (result == null) {
            log.warn("Applink request execution returned null");
            return Either.right((Object)new JiraErrors().addError("Request execution failed with null response"));
        }
        return result;
    }

    private Either<String, JiraErrors> executeApplinkRequestLegacy(@Nonnull ApplicationLink jiraLink, @Nonnull ApplicationLinkRequestFactory requestFactory, @Nonnull URI url, @Nonnull UnaryOperator<JsonNode> nodeFilter, @Nonnull ApplicationLinkResponseHandler<Either<String, JiraErrors>> responseHandler) throws CredentialsRequiredException, ResponseException {
        Either<String, JiraErrors> response = this.executeApplinkRequest(jiraLink, requestFactory, url, responseHandler);
        return response.leftMap(responseJson -> {
            try {
                ArrayNode arrayNode = OBJECT_MAPPER.createArrayNode();
                JsonNode root = (JsonNode)nodeFilter.apply(OBJECT_MAPPER.readTree((String)response.left().get()));
                arrayNode.addAll((Collection)Lists.newArrayList((Iterator)root.getElements()));
                return arrayNode.toString();
            }
            catch (IOException e) {
                log.error("Result retrieved from JiraResponseHandler, shouldn't contain error: {}", (Object)e.getMessage());
                return e.getMessage();
            }
        });
    }

    private Either<String, JiraErrors> getProjectIssueTypeMetaAsJson(@Nonnull ApplicationId applicationId, @Nonnull String project, int issueTypeId) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            URI url = DefaultJiraService.buildProjectIssueTypeMetaRestUrl(((ProjectIssueTypeMetaRequest.Builder)new ProjectIssueTypeMetaRequest.Builder(applicationId).issueType(issueTypeId).project(project).maxResults(this.config.getCreateMetaMaxResults())).build());
            return this.executeApplinkRequest(jiraLink, requestFactory, url, new JiraResponseHandler(requestFactory, jiraLink, "values"));
        });
    }

    private Either<String, JiraErrors> getProjectIssueTypeMetaAsJsonLegacy(@Nonnull ApplicationId applicationId, @Nonnull String project, int issueTypeId) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            UriBuilder uriBuilder = UriBuilder.fromUri((String)REST_CREATEMETA_PATH);
            uriBuilder.queryParam(PARAM_EXPAND, new Object[]{FIELD_META_TYPES_FIELDS});
            if (StringUtils.isNumeric((CharSequence)project)) {
                uriBuilder.queryParam("projectIds", new Object[]{project});
            } else {
                uriBuilder.queryParam("projectKeys", new Object[]{project});
            }
            String issueType = Integer.toString(issueTypeId);
            if (StringUtils.isNumeric((CharSequence)issueType)) {
                uriBuilder.queryParam("issuetypeIds", new Object[]{issueType});
            } else {
                uriBuilder.queryParam("issuetypeNames", new Object[]{issueType});
            }
            return this.executeApplinkRequestLegacy(jiraLink, requestFactory, uriBuilder.build(new Object[0]), node -> node.path("projects").path(0).path(REST_CREATEMETA_ISSUETYPES_PATH).path(0).path(PARAM_FIELDS), new JiraResponseHandler(requestFactory, jiraLink));
        });
    }

    private Either<String, JiraErrors> getProjectIssueTypesAsJson(@Nonnull ApplicationId applicationId, @Nonnull String project) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            URI url = DefaultJiraService.buildProjectIssueTypeRestUrl(((ProjectIssueTypeRequest.Builder)new ProjectIssueTypeRequest.Builder(applicationId).project(project).maxResults(this.config.getCreateMetaMaxResults())).build());
            return this.executeApplinkRequest(jiraLink, requestFactory, url, new JiraResponseHandler(requestFactory, jiraLink, "values"));
        });
    }

    private Either<String, JiraErrors> getProjectIssueTypesAsJsonLegacy(@Nonnull ApplicationId applicationId, @Nonnull String project) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            UriBuilder uriBuilder = UriBuilder.fromUri((String)REST_CREATEMETA_PATH);
            uriBuilder.queryParam(PARAM_EXPAND, new Object[]{FIELD_META_TYPES});
            if (StringUtils.isNumeric((CharSequence)project)) {
                uriBuilder.queryParam("projectIds", new Object[]{project});
            } else {
                uriBuilder.queryParam("projectKeys", new Object[]{project});
            }
            return this.executeApplinkRequestLegacy(jiraLink, requestFactory, uriBuilder.build(new Object[0]), node -> node.path("projects").path(0).path(REST_CREATEMETA_ISSUETYPES_PATH), new JiraProjectResponseHandler(requestFactory, jiraLink));
        });
    }

    private Either<String, JiraErrors> getProjectListAsJson(@Nonnull ApplicationId applicationId) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> this.executeApplinkRequest(jiraLink, requestFactory, REST_GET_PROJECTS_URI, new JiraProjectResponseHandler(requestFactory, jiraLink)));
    }

    private Either<String, JiraErrors> getProjectListAsJsonLegacy(@Nonnull ApplicationId applicationId) {
        return this.communicateWithJira(applicationId, (jiraLink, requestFactory) -> {
            UriBuilder uriBuilder = UriBuilder.fromUri((String)REST_CREATEMETA_PATH);
            uriBuilder.queryParam(PARAM_EXPAND, new Object[]{FIELD_META_TYPES});
            return this.executeApplinkRequestLegacy(jiraLink, requestFactory, uriBuilder.build(new Object[0]), node -> node.path("projects"), new JiraProjectResponseHandler(requestFactory, jiraLink));
        });
    }

    private JSONArray getExceptionsJson(List<JiraAuthenticationRequiredException> authenticationExceptions, List<JiraCommunicationException> communicationExceptions) {
        JSONArray exceptions = new JSONArray();
        authenticationExceptions.forEach(exception -> {
            JSONObject exceptionJSON = new JSONObject();
            try {
                exceptionJSON.append("authenticationUri", exception.getAuthenticationUri());
                exceptionJSON.append("applicationName", exception.getApplicationName());
                exceptions.put(exceptionJSON);
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
        });
        communicationExceptions.forEach(exception -> {
            JSONObject exceptionJSON = new JSONObject();
            try {
                exceptionJSON.append("applicationUrl", exception.getApplicationUrl());
                exceptionJSON.append("applicationName", exception.getApplicationName());
                exceptions.put(exceptionJSON);
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
        });
        return exceptions;
    }

    private ApplicationLink getJiraLink(ApplicationId id) {
        try {
            return this.applicationLinkService.getApplicationLink(id);
        }
        catch (TypeNotInstalledException e) {
            log.error("The type of application referred to by " + id + " is no longer installed on this server: {}", (Object)e.getMessage());
            throw new TypeNotPresentException(e.getType(), e);
        }
    }

    private List<ApplicationLink> getJiraLinks(String entityKey) {
        List<Object> jiraLinks = Lists.newArrayList();
        if (entityKey != null) {
            jiraLinks = this.getJiraLinksForEntity(entityKey);
        }
        for (ApplicationLink link : this.applicationLinkService.getApplicationLinks(JiraApplicationType.class)) {
            if (jiraLinks.contains(link)) continue;
            jiraLinks.add(link);
        }
        return jiraLinks;
    }

    private JiraErrors getErrorFromBulkCreation(Response response, String responseString, ApplicationLink jiraLink) throws JSONException {
        JiraErrors errors = new JiraErrors();
        if (response.getStatusCode() == 404 || response.getStatusCode() == 405) {
            errors.setResponseCode(response.getStatusCode());
            String error = "Bulk issue creation is not supported on (" + jiraLink.getName() + ")";
            errors.addError(error);
            log.warn(error);
            return errors;
        }
        JSONObject responseJson = new JSONObject(responseString);
        if (!responseJson.has("issues")) {
            if (responseJson.has("errors")) {
                JSONObject errorsNode = responseJson.getJSONObject("errors");
                Iterator errorKeys = errorsNode.keys();
                while (errorKeys.hasNext()) {
                    String key = (String)errorKeys.next();
                    errors.addError(key, errorsNode.getString(key));
                }
            }
            if (responseJson.has("errorMessages")) {
                JSONArray errorMessagesNode = responseJson.getJSONArray("errorMessages");
                for (int errorMessageIndex = 0; errorMessageIndex < errorMessagesNode.length(); ++errorMessageIndex) {
                    errors.addError(errorMessagesNode.getString(errorMessageIndex));
                }
            }
        }
        return errors;
    }

    private JSONObject getErrorJson(JiraErrors errors) {
        JSONObject error = new JSONObject();
        try {
            error.put("errorMessages", errors.errorMessages);
            error.put("errors", errors.errors);
        }
        catch (JSONException e) {
            throw new IllegalArgumentException("Exception while rendering Error object");
        }
        return error;
    }

    private String getFetchMyIssuesProfileKey(ApplicationLink jiraLink) {
        return "Requesting issues assigned to " + this.userManager.getRemoteUserKey() + " from Jira server: " + jiraLink.getName();
    }

    private static void addPagingParams(UriBuilder uriBuilder, AbstractJiraPagedRequest pagedRequest) {
        pagedRequest.getMaxResults().ifPresent(val -> uriBuilder.queryParam(PARAM_MAX_RESULTS, new Object[]{val}));
        pagedRequest.getStartAt().ifPresent(val -> uriBuilder.queryParam("startAt", new Object[]{val}));
    }

    private boolean isAuthenticated(ApplicationLink link) {
        try {
            ApplicationLinkRequest applicationLinkRequest = link.createAuthenticatedRequestFactory().createRequest(Request.MethodType.GET, link.getRpcUrl().toString());
        }
        catch (CredentialsRequiredException ignored) {
            return false;
        }
        return true;
    }

    private JiraException newAuthenticationException(List<JiraAuthenticationRequiredException> exceptions) {
        if (exceptions.size() == 1) {
            return exceptions.get(0);
        }
        return new JiraMultipleAuthenticationException(this.i18nResolver.getText("multiple.authentication.problems"), exceptions);
    }

    private JiraAuthenticationRequiredException newAuthenticationRequiredException(ApplicationLink jiraLink, ApplicationLinkRequestFactory requestFactory) {
        return new JiraAuthenticationRequiredException(this.i18nResolver.getText("authentication.required"), jiraLink.getName(), (AuthorisationURIGenerator)requestFactory);
    }

    private JiraCommunicationException newCommunicationException(ApplicationLink jiraLink) {
        return this.newCommunicationException(jiraLink, null);
    }

    private JiraCommunicationException newCommunicationException(ApplicationLink jiraLink, String cause) {
        return new JiraCommunicationException(StringUtils.isEmpty((CharSequence)cause) ? this.i18nResolver.getText("communication.cause.generic") : cause, jiraLink.getName(), jiraLink.getDisplayUrl());
    }

    private JiraException newCommunicationException(List<JiraCommunicationException> exceptions) {
        if (exceptions.size() == 1) {
            return exceptions.get(0);
        }
        return new JiraMultipleCommunicationException(this.i18nResolver.getText("multiple.communication.problems"), exceptions);
    }

    private JiraValidationException newValidationException(JiraErrors errors) {
        return new JiraValidationException(errors);
    }

    private JSONObject populateJsonIssue(JSONObject issue, ApplicationId applicationId, boolean canTransition) throws JSONException {
        issue.put("applicationLinkId", applicationId);
        issue.put("url", this.buildIssueUrl(applicationId, issue.getString("key")));
        issue.put("canTransition", canTransition);
        return issue;
    }

    private JSONArray sortResponse(JSONArray response, JiraIssuesRequest request) {
        if (StringUtils.isBlank((CharSequence)request.getEntityKey())) {
            return response;
        }
        Object entity = this.entityResolver.resolve(request.getEntityKey());
        if (entity == null) {
            return response;
        }
        Iterable jiraLinks = this.entityLinkService.getEntityLinks(entity, JiraProjectEntityType.class);
        if (Iterables.isEmpty((Iterable)jiraLinks)) {
            return response;
        }
        TreeSet<JSONObject> sortableIssues = new TreeSet<JSONObject>(new JiraJsonObjectEntityLinkBasedComparator(jiraLinks));
        for (int i = 0; i < response.length(); ++i) {
            try {
                sortableIssues.add(response.getJSONObject(i));
                continue;
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
        }
        return new JSONArray(sortableIssues);
    }

    private static class NoAnonymousAccessResolver
    implements UserAccessResolver {
        private NoAnonymousAccessResolver() {
        }

        @Override
        public boolean isAnonymousAccessAllowed() {
            return false;
        }
    }

    private class ServerInfoResponseHandler
    implements ApplicationLinkResponseHandler<JsonNode> {
        private final ApplicationLink jiraLink;

        private ServerInfoResponseHandler(ApplicationLink jiraLink) {
            this.jiraLink = jiraLink;
        }

        public JsonNode credentialsRequired(Response response) {
            throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
        }

        public JsonNode handle(Response response) throws ResponseException {
            try {
                return OBJECT_MAPPER.readTree(response.getResponseBodyAsStream());
            }
            catch (IOException e) {
                log.warn("Could not parse Jira server's information: {}", (Object)e.getMessage());
                return null;
            }
        }
    }

    private class JiraProjectResponseHandler
    implements ApplicationLinkResponseHandler<Either<String, JiraErrors>> {
        private final ApplicationLink jiraLink;
        private final ApplicationLinkRequestFactory requestFactory;

        private JiraProjectResponseHandler(ApplicationLinkRequestFactory requestFactory, ApplicationLink jiraLink) {
            this.requestFactory = requestFactory;
            this.jiraLink = jiraLink;
        }

        public Either<String, JiraErrors> credentialsRequired(Response response) throws ResponseException {
            throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
        }

        public Either<String, JiraErrors> handle(Response response) throws ResponseException {
            try {
                return Either.left((Object)response.getResponseBodyAsString());
            }
            catch (Exception e) {
                log.warn("Error getting response from Jira: {}", (Object)e.getMessage());
                throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
            }
        }
    }

    private class JiraImageResponseHandler
    implements ApplicationLinkResponseHandler<Either<String, JiraErrors>> {
        private final ApplicationLink jiraLink;
        private final ApplicationLinkRequestFactory requestFactory;
        private final HttpServletResponse httpResponse;

        private JiraImageResponseHandler(ApplicationLinkRequestFactory requestFactory, ApplicationLink jiraLink, HttpServletResponse httpResponse) {
            this.jiraLink = jiraLink;
            this.requestFactory = requestFactory;
            this.httpResponse = httpResponse;
        }

        public Either<String, JiraErrors> credentialsRequired(Response response) {
            throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
        }

        public Either<String, JiraErrors> handle(Response response) {
            try {
                if (response.getStatusCode() == 204) {
                    throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
                }
                InputStream inputStream = response.getResponseBodyAsStream();
                this.httpResponse.setContentType(response.getHeader(DefaultJiraService.HEADER_CONTENT_TYPE));
                this.httpResponse.setHeader(DefaultJiraService.HEADER_CACHE_CONTROL, response.getHeader(DefaultJiraService.HEADER_CACHE_CONTROL));
                ServletOutputStream outputStream = this.httpResponse.getOutputStream();
                IOUtils.copy((InputStream)inputStream, (OutputStream)outputStream);
                return Either.left((Object)"");
            }
            catch (Exception e) {
                log.warn("Could not get image from Jira: {}", (Object)e.getMessage());
                log.debug("Could not get image from Jira", (Throwable)e);
                throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
            }
        }
    }

    @VisibleForTesting
    class JiraResponseHandler
    implements ApplicationLinkResponseHandler<Either<String, JiraErrors>> {
        private final ApplicationLink jiraLink;
        private final String property;
        private final ApplicationLinkRequestFactory requestFactory;

        private JiraResponseHandler(ApplicationLinkRequestFactory requestFactory, ApplicationLink jiraLink) {
            this(requestFactory, jiraLink, (String)null);
        }

        private JiraResponseHandler(ApplicationLinkRequestFactory requestFactory, ApplicationLink jiraLink, String property) {
            this.jiraLink = jiraLink;
            this.property = property;
            this.requestFactory = requestFactory;
        }

        public Either<String, JiraErrors> credentialsRequired(Response response) {
            throw DefaultJiraService.this.newAuthenticationRequiredException(this.jiraLink, this.requestFactory);
        }

        public Either<String, JiraErrors> handle(Response response) {
            try {
                InputStream responseStream = response.getResponseBodyAsStream();
                if (response.getStatusCode() == 204) {
                    log.debug("Jira says: 204");
                    return Either.left((Object)"");
                }
                String responseString = IOUtils.toString((InputStream)responseStream, (String)"UTF-8");
                log.debug("Jira says: " + response.getStatusCode() + ": " + responseString);
                return this.filter(response, responseString);
            }
            catch (IOException e) {
                DefaultJiraService.logCommunicationIssue(this.jiraLink, e.getMessage());
                throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
            }
            catch (ResponseException e) {
                DefaultJiraService.logCommunicationIssue(this.jiraLink, e.getMessage());
                throw DefaultJiraService.this.newCommunicationException(this.jiraLink);
            }
        }

        Either<String, JiraErrors> filter(Response response, String responseString) throws IOException {
            Map o = (Map)OBJECT_MAPPER.reader(Map.class).readValue(responseString);
            JiraErrors errors = new JiraErrors();
            errors.setResponseCode(response.getStatusCode());
            if (o.containsKey("errorMessages")) {
                errors.addAllErrors((List)o.get("errorMessages"));
            }
            if (o.containsKey("errors")) {
                errors.addAllErrors((Map)o.get("errors"));
            }
            if (errors.hasErrors()) {
                return Either.right((Object)errors);
            }
            StringWriter writer = new StringWriter();
            OBJECT_MAPPER.writeValue((Writer)writer, (Object)(this.property == null ? o : o.get(this.property)));
            return Either.left((Object)writer.toString());
        }
    }

    private static interface JiraRequestFunction<T> {
        public T apply(ApplicationLink var1, ApplicationLinkRequestFactory var2) throws CredentialsRequiredException, ResponseException;
    }
}

