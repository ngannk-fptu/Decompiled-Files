/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.application.jira.JiraApplicationType
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.http.ConfluenceHttpParameters
 *  com.atlassian.core.util.PairType
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.apache.commons.io.IOUtils
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraDateStamp;
import com.atlassian.confluence.extra.calendar3.events.JiraApplinkRequest;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.ical4j.property.GreenHopperSprintEditability;
import com.atlassian.confluence.extra.calendar3.ical4j.property.GreenHopperSprintStatus;
import com.atlassian.confluence.extra.calendar3.model.JiraDateField;
import com.atlassian.confluence.extra.calendar3.model.JqlAutoCompleteResult;
import com.atlassian.confluence.extra.calendar3.model.JqlValidationResult;
import com.atlassian.confluence.extra.calendar3.model.Project;
import com.atlassian.confluence.extra.calendar3.model.QueryOptions;
import com.atlassian.confluence.extra.calendar3.model.SearchFilter;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.Ical4jIoUtil;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.core.util.PairType;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.XProperty;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultJiraAccessor
implements JiraAccessor,
InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJiraAccessor.class);
    private static final String REST_PREFIX = "/rest";
    private static final String API_VERSION_LEGACY = "2.0.alpha1";
    private static final String API_VERSION_FINAL = "2";
    private static final String ICAL_VERSION = "1.0";
    private static final String ICAL_ENDPOINT_PREFIX = "/rest/ical/1.0/ical";
    private static final String DISABLE_EXPANDED_FIELD_RETRIEVAL = "teamcal.calendar3.disable.expanded.jira.field.retrieval";
    private static final String GREENHOPPER_SPRINT_DATES_BY_JQL_URI_FORMAT = "/rest/greenhopper/1.0/integration/teamcalendars/sprint/list.json?jql=%s";
    private static final String GREENHOPPPER_SPRINT_DATES_BY_SEARCH_FILTER_URL_FORMAT = "/rest/greenhopper/1.0/integration/teamcalendars/sprint/list.json?searchFilterId=%s";
    private static final boolean GREENHOPPER_SPRINT_DATES_ENABLED = BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.greenhopper.sprint.enabled", Boolean.TRUE.toString()));
    private static final boolean CACHE_JIRA_ACCESSOR_ENABLE = BooleanUtils.toBoolean(System.getProperty("com.atlassian.confluence.extra.calendar3.cache.jira.accessor", Boolean.TRUE.toString()));
    private final ApplicationLinkService applicationLinkService;
    private final JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter;
    private final CacheManager cacheManager;
    private final DarkFeatureManager darkFeatureManager;
    private final String CACHE_JIRA_QUERY_OPTION = this.getClass().getName() + ":cache-jira-query-option";
    private final String CACHE_GREENHOPPER_SPRINT_DATE = this.getClass().getName() + ":cache-greenhopper-spint-date";
    private final SettingsManager settingsManager;
    private static int ICALENDAR_STREAM_SOCKET_TIMEOUT = Integer.getInteger("com.atlassian.confluence.extra.calendar3.jira.timeout.socket", 20000);
    private int confluenceSocketTimeoutSetting = -1;
    private EventPublisher eventPublisher;

    public void afterPropertiesSet() throws Exception {
        if (this.getCacheQueryOptionJIRA() != null) {
            this.getCacheQueryOptionJIRA().removeAll();
        }
        if (this.getCacheGreenHopperSprintDate() != null) {
            this.getCacheGreenHopperSprintDate().removeAll();
        }
    }

    @Autowired
    public DefaultJiraAccessor(@ComponentImport ApplicationLinkService applicationLinkService, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, @ComponentImport CacheManager cacheManager, @ComponentImport SettingsManager settingsManager, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport EventPublisher eventPublisher) {
        this.applicationLinkService = applicationLinkService;
        this.jodaIcal4jDateTimeConverter = jodaIcal4jDateTimeConverter;
        this.cacheManager = cacheManager;
        this.settingsManager = settingsManager;
        this.darkFeatureManager = darkFeatureManager;
        this.eventPublisher = eventPublisher;
    }

    private int getSocketTimeout() {
        ConfluenceHttpParameters httpParameters;
        if (this.confluenceSocketTimeoutSetting != -1) {
            return this.confluenceSocketTimeoutSetting > ICALENDAR_STREAM_SOCKET_TIMEOUT ? this.confluenceSocketTimeoutSetting : ICALENDAR_STREAM_SOCKET_TIMEOUT;
        }
        Settings settings = this.settingsManager.getGlobalSettings();
        if (settings != null && (httpParameters = settings.getConfluenceHttpParameters()) != null) {
            this.confluenceSocketTimeoutSetting = httpParameters.getSocketTimeout();
        }
        return this.confluenceSocketTimeoutSetting > ICALENDAR_STREAM_SOCKET_TIMEOUT ? this.confluenceSocketTimeoutSetting : ICALENDAR_STREAM_SOCKET_TIMEOUT;
    }

    @Override
    public Collection<ApplicationLink> getLinkedJiraApplications() {
        return Lists.newArrayList((Iterable)this.applicationLinkService.getApplicationLinks(JiraApplicationType.class));
    }

    @Override
    public ApplicationLink getLinkedJiraInstance(String applicationId) {
        for (ApplicationLink jiraLink : this.getLinkedJiraApplications()) {
            if (!StringUtils.equals(jiraLink.getId().get(), applicationId)) continue;
            return jiraLink;
        }
        return null;
    }

    private void publishJiraApplinkRequestEvent(ApplicationLink jiraLink, String operation) {
        this.eventPublisher.publish((Object)new JiraApplinkRequest(this, AuthenticatedUserThreadLocal.get(), jiraLink, operation));
    }

    @Override
    public QueryOptions getQueryOptions(final ApplicationLink jiraLink) throws CredentialsRequiredException, ResponseException {
        QueryOptions queryOptions = null;
        String keyJiraOptionCache = this.getKeyUserCache(jiraLink);
        Cache jiraOptionCache = this.getCacheQueryOptionJIRA();
        if (jiraOptionCache != null) {
            queryOptions = (QueryOptions)jiraOptionCache.get((Object)keyJiraOptionCache);
        }
        if (queryOptions == null) {
            final String endPoint = this.getQueryOptionsEndPoint() + "?_=" + System.currentTimeMillis();
            final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
            try {
                this.publishJiraApplinkRequestEvent(jiraLink, "getQueryOptions");
                queryOptions = (QueryOptions)((ApplicationLinkRequest)requestFactory.createRequest(Request.MethodType.GET, endPoint).setSoTimeout(this.getSocketTimeout())).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<QueryOptions>(){

                    public QueryOptions credentialsRequired(Response response) throws ResponseException {
                        throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                    }

                    public QueryOptions handle(Response response) throws ResponseException {
                        if (404 == response.getStatusCode()) {
                            throw new JiraAccessor.JiraPreConditionUnmetException(jiraLink, JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound, endPoint);
                        }
                        try {
                            return DefaultJiraAccessor.this.getQueryOptions(DefaultJiraAccessor.this.readResponseToString(response));
                        }
                        catch (IOException io) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)io);
                        }
                        catch (JSONException json) {
                            throw new ResponseException(String.format("Unable to interpret response from %s%s in the expected JSON format", jiraLink.getDisplayUrl().toString(), endPoint), (Throwable)json);
                        }
                    }
                });
                if (jiraOptionCache != null) {
                    jiraOptionCache.put((Object)keyJiraOptionCache, (Object)queryOptions);
                }
            }
            catch (JiraAccessor.JiraPreConditionUnmetException projectsRestEndPointNotFound) {
                LOG.debug(String.format("API end point to get query options from %s.", projectsRestEndPointNotFound.getJiraLink().getDisplayUrl().toString()), (Throwable)((Object)projectsRestEndPointNotFound));
                return new QueryOptions(new ArrayList<Project>(this.getProjects(jiraLink)), null, true, null, null, null, false);
            }
            catch (ResponseException applinkResponseError) {
                Throwable cause = applinkResponseError.getCause();
                if (cause instanceof CredentialsRequiredException) {
                    throw (CredentialsRequiredException)cause;
                }
                throw applinkResponseError;
            }
        }
        return queryOptions;
    }

    private QueryOptions getQueryOptions(String json) throws JSONException {
        JSONObject queryOptionsObj = new JSONObject(json);
        JSONArray projectsArray = (JSONArray)queryOptionsObj.get("projects");
        ArrayList<Project> projects = null == projectsArray || 0 == projectsArray.length() ? null : new ArrayList<Project>(this.getProjects(projectsArray));
        JSONArray searchFilterArray = (JSONArray)queryOptionsObj.get("searchFilters");
        ArrayList<SearchFilter> searchFilters = null;
        if (null != searchFilterArray && 0 != searchFilterArray.length()) {
            searchFilters = new ArrayList<SearchFilter>(searchFilterArray.length());
            int j = searchFilterArray.length();
            for (int i = 0; i < j; ++i) {
                JSONObject searchRequestObj = searchFilterArray.getJSONObject(i);
                searchFilters.add(new SearchFilter(searchRequestObj.getLong("id"), searchRequestObj.getString("name"), searchRequestObj.has("description") ? searchRequestObj.getString("description") : null));
            }
        }
        String visibleFieldNames = queryOptionsObj.has("visibleFieldNames") ? queryOptionsObj.getString("visibleFieldNames") : null;
        String visibleFunctionNamesJson = queryOptionsObj.has("visibleFunctionNamesJson") ? queryOptionsObj.getString("visibleFunctionNamesJson") : null;
        String jqlReservedWordsJson = queryOptionsObj.has("jqlReservedWordsJson") ? queryOptionsObj.getString("jqlReservedWordsJson") : null;
        return new QueryOptions(projects, searchFilters, false, visibleFieldNames, visibleFunctionNamesJson, jqlReservedWordsJson, queryOptionsObj.has("dateRangeSupported"));
    }

    private String getQueryOptionsEndPoint() {
        return "/rest/ical/1.0/ical/config/query/options";
    }

    @Override
    public Collection<Project> getProjects(final ApplicationLink jiraLink) throws CredentialsRequiredException, ResponseException {
        final String endPoint = this.getProjectListEndPoint(this.getApiVersion(jiraLink)) + "?_=" + System.currentTimeMillis();
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        try {
            this.publishJiraApplinkRequestEvent(jiraLink, "getProjects");
            return (Collection)requestFactory.createRequest(Request.MethodType.GET, endPoint).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Collection<Project>>(){

                public Collection<Project> credentialsRequired(Response response) throws ResponseException {
                    throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                }

                public Collection<Project> handle(Response response) throws ResponseException {
                    if (404 == response.getStatusCode()) {
                        throw new JiraAccessor.JiraPreConditionUnmetException(jiraLink, JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound, endPoint);
                    }
                    try {
                        return DefaultJiraAccessor.this.getProjects(DefaultJiraAccessor.this.readResponseToString(response));
                    }
                    catch (IOException io) {
                        throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)io);
                    }
                    catch (JSONException json) {
                        throw new ResponseException(String.format("Unable to interpret response from %s%s in the expected JSON format", jiraLink.getDisplayUrl().toString(), endPoint), (Throwable)json);
                    }
                }
            });
        }
        catch (ResponseException applinkResponseError) {
            Throwable cause = applinkResponseError.getCause();
            if (cause instanceof CredentialsRequiredException) {
                throw (CredentialsRequiredException)cause;
            }
            throw applinkResponseError;
        }
    }

    private Collection<Project> getProjects(String projectsJsonString) throws JSONException {
        return this.getProjects(new JSONArray(projectsJsonString));
    }

    private Collection<Project> getProjects(JSONArray projectsJson) throws JSONException {
        LinkedHashSet<Project> projects = new LinkedHashSet<Project>();
        int j = projectsJson.length();
        for (int i = 0; i < j; ++i) {
            JSONObject projectJson = projectsJson.getJSONObject(i);
            String projectKey = projectJson.getString("key");
            projects.add(new Project(projectJson.has("name") ? projectJson.getString("name") : projectKey, projectKey));
        }
        return projects;
    }

    @Override
    public void updateProjectVersionReleaseDate(final ApplicationLink jiraLink, final String versionId, Date date) throws CredentialsRequiredException, ResponseException, JSONException {
        String dateStamp = JiraDateStamp.fromDate(date);
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        ApplicationLinkRequest putRequest = requestFactory.createRequest(Request.MethodType.PUT, REST_PREFIX + "/api/" + this.getApiVersion(jiraLink) + "/version/" + versionId);
        this.setApplicationLinkRequestBody(putRequest, new JSONObject().put("releaseDate", (Object)dateStamp).toString(), "application/json");
        this.publishJiraApplinkRequestEvent(jiraLink, "updateProjectVersionReleaseDate");
        putRequest.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Void>(){

            public Void credentialsRequired(Response response) throws ResponseException {
                throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
            }

            public Void handle(Response response) throws ResponseException {
                if (response.getStatusCode() == 403) {
                    throw new CalendarException("calendar3.error.edit.jira.version.permission", versionId, jiraLink.getName());
                }
                if (!response.isSuccessful()) {
                    throw new CalendarException("calendar3.error.edit.jira.version", versionId, jiraLink.getName(), response.getStatusCode());
                }
                return null;
            }
        });
    }

    @Override
    @Deprecated
    public void updateEventFields(ApplicationLink jiraLink, Map<String, List<PairType>> eventFields) throws CredentialsRequiredException, ResponseException, JSONException {
        this.updateEventFields(jiraLink, eventFields, null);
    }

    @Override
    public void updateEventFields(final ApplicationLink jiraLink, Map<String, List<PairType>> eventFields, String timeZoneId) throws CredentialsRequiredException, ResponseException, JSONException {
        String apiVersion = this.getApiVersion(jiraLink);
        if (apiVersion.equals(API_VERSION_LEGACY)) {
            throw new CalendarException("calendar3.error.edit.jira.api.version");
        }
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        for (final Map.Entry<String, List<PairType>> eventField : eventFields.entrySet()) {
            JSONObject updateObject = new JSONObject();
            for (PairType field : eventField.getValue()) {
                updateObject.put(field.getKey().toString(), (Object)new JSONArray().put((Object)new JSONObject().put("set", (Object)(timeZoneId == null ? JiraDateStamp.fromDate((Date)field.getValue()) : JiraDateStamp.fromDateAndTimeZoneId((Date)field.getValue(), timeZoneId)))));
            }
            ApplicationLinkRequest putRequest = requestFactory.createRequest(Request.MethodType.PUT, REST_PREFIX + "/api/" + apiVersion + "/issue/" + GeneralUtil.urlEncode((String)eventField.getKey()));
            this.setApplicationLinkRequestBody(putRequest, new JSONObject().put("update", (Object)updateObject).toString(), "application/json");
            try {
                this.publishJiraApplinkRequestEvent(jiraLink, "updateEventFields");
                putRequest.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Void>(){

                    public Void credentialsRequired(Response response) throws ResponseException {
                        throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                    }

                    public Void handle(Response response) throws ResponseException {
                        if (response.getStatusCode() == 400) {
                            throw new CalendarException("calendar3.error.edit.jira.field.permission", DefaultJiraAccessor.this.getFieldNames((List)eventField.getValue()), jiraLink.getName());
                        }
                        if (!response.isSuccessful()) {
                            throw new CalendarException("calendar3.error.edit.jira.field", DefaultJiraAccessor.this.getFieldNames((List)eventField.getValue()), jiraLink.getName(), response.getStatusCode());
                        }
                        return null;
                    }
                });
            }
            catch (ResponseException re) {
                throw new CalendarException((Exception)((Object)re), "calendar3.error.edit.jira.field", this.getFieldNames(eventField.getValue()), jiraLink.getName());
            }
        }
    }

    private String getFieldNames(List<PairType> fields) {
        return StringUtils.join(Collections2.transform(fields, pairType -> pairType.getKey().toString()), ", ");
    }

    @Override
    public boolean canEditCalendar(ApplicationLink jiraLink) throws ResponseException, CredentialsRequiredException {
        return null != jiraLink && this.getApiVersion(jiraLink).equals(API_VERSION_FINAL);
    }

    private String readResponseToString(Response response) throws ResponseException, IOException {
        if (response.isSuccessful()) {
            try (BufferedInputStream inputStream = new BufferedInputStream(response.getResponseBodyAsStream());){
                String string = IOUtils.toString((InputStream)inputStream, (String)Ical4jIoUtil.getContentTypeCharset(response.getHeader("Content-Type")));
                return string;
            }
        }
        throw new ResponseException(String.format("%d - %s", response.getStatusCode(), StringUtils.defaultString(response.getStatusText())));
    }

    private String getProjectListEndPoint(String apiVersion) {
        return "/rest/api/" + apiVersion + "/project";
    }

    @Override
    public Calendar getCalendarByJql(ApplicationLink jiraLink, String jql, JiraAccessor.CalendarOptions calendarOptions, long start, long end) throws CredentialsRequiredException, ResponseException, IOException, ParserException {
        String endPoint = this.getIcalendarEndPointByJql(false, jql, calendarOptions, start, end);
        try {
            Calendar calendar = this.getCalendarFromEndPoint(jiraLink, endPoint, jql);
            if (calendarOptions.isIncludeGreenHopperSprints()) {
                calendar.getComponents().addAll(this.getGreenHopperSprintDates(jiraLink, jql));
            }
            return calendar;
        }
        catch (JiraAccessor.JiraPreConditionUnmetException iCalendarByJqlEndPointNotFound) {
            LOG.debug(String.format("API end point to get query options from %s.", iCalendarByJqlEndPointNotFound.getJiraLink().getDisplayUrl().toString()), (Throwable)((Object)iCalendarByJqlEndPointNotFound));
            return this.getCalendarFromEndPoint(jiraLink, this.getIcalendarEndPointByJql(true, jql, calendarOptions, start, end), jql);
        }
    }

    private Calendar getCalendarFromEndPoint(final ApplicationLink jiraLink, final String endPoint, final String jql) throws CredentialsRequiredException, IOException, ParserException, ResponseException {
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        UtilTimerStack.push((String)"DefaultJiraAccessor.getCalendarFromEndPoint()");
        try {
            this.publishJiraApplinkRequestEvent(jiraLink, "getCalendarFromEndPoint");
            String icalendarText = (String)((ApplicationLinkRequest)requestFactory.createRequest(Request.MethodType.GET, endPoint).setSoTimeout(this.getSocketTimeout())).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<String>(){

                public String credentialsRequired(Response response) throws ResponseException {
                    throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                }

                public String handle(Response response) throws ResponseException {
                    if (response.isSuccessful()) {
                        try {
                            return DefaultJiraAccessor.this.readResponseToString(response);
                        }
                        catch (IOException io) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)io);
                        }
                    }
                    if (404 == response.getStatusCode()) {
                        throw new JiraAccessor.JiraPreConditionUnmetException(jiraLink, JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound, endPoint);
                    }
                    if (400 == response.getStatusCode()) {
                        throw new JiraAccessor.JiraJQLWrongResponseException(String.format("The JQL query for %s didn't return any results. Try changing the query.", jql));
                    }
                    throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", jql));
                }
            });
            if (StringUtils.isEmpty(icalendarText)) {
                throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", jql));
            }
            Calendar calendar = Ical4jIoUtil.newCalendarBuilder().build(new StringReader(icalendarText));
            return calendar;
        }
        catch (ResponseException applinkResponseError) {
            Throwable cause = applinkResponseError.getCause();
            if (cause instanceof CredentialsRequiredException) {
                throw (CredentialsRequiredException)cause;
            }
            throw applinkResponseError;
        }
        finally {
            UtilTimerStack.pop((String)"DefaultJiraAccessor.getCalendarFromEndPoint()");
        }
    }

    private String getIcalendarEndPointByJql(boolean oldEndpoint, String jql, JiraAccessor.CalendarOptions calendarOptions, long start, long end) {
        StringBuilder endPointBuilder = new StringBuilder(ICAL_ENDPOINT_PREFIX).append(oldEndpoint ? "/search/events.ics" : "/search/jql/events.ics").append("?_=").append(System.currentTimeMillis());
        endPointBuilder.append("&jql=").append(GeneralUtil.urlEncode((String)jql));
        this.appendEndPointDateFieldNameParam(endPointBuilder, calendarOptions);
        this.appendEndPointFixVersionParam(endPointBuilder, calendarOptions);
        this.appendEndPointRangeDateLimit(endPointBuilder, start, end);
        return endPointBuilder.toString();
    }

    private StringBuilder appendEndPointFixVersionParam(StringBuilder endPointBuilder, JiraAccessor.CalendarOptions calendarOptions) {
        return endPointBuilder.append("&includeFixVersions=").append(calendarOptions.isIncludeFixVersions());
    }

    private StringBuilder appendEndPointRangeDateLimit(StringBuilder endPointBuilder, long start, long end) {
        return endPointBuilder.append("&start=").append(start).append("&end=").append(end).append("&maxIssue=").append(CalendarUtil.MAX_JIRA_ISSUES_TO_DISPLAY);
    }

    private void appendEndPointDateFieldNameParam(StringBuilder endPointBuilder, JiraAccessor.CalendarOptions calendarOptions) {
        Set<PairType> durations;
        Set<String> dateFieldNames = calendarOptions.getDateFieldNames();
        if (null != dateFieldNames) {
            for (String dateFieldName : dateFieldNames) {
                endPointBuilder.append("&dateFieldName=").append(GeneralUtil.urlEncode((String)this.rekeyDateField(dateFieldName)));
            }
        }
        if (null != (durations = calendarOptions.getDurations()) && !durations.isEmpty()) {
            StringBuilder durationsBuilder = new StringBuilder();
            for (PairType duration : durations) {
                durationsBuilder.setLength(0);
                durationsBuilder.append(this.rekeyDateField(duration.getKey().toString())).append('/').append(this.rekeyDateField(duration.getValue().toString()));
                endPointBuilder.append("&duration=").append(GeneralUtil.urlEncode((String)durationsBuilder.toString()));
            }
        }
    }

    private String rekeyDateField(String dateFieldKey) {
        return StringUtils.equals("resolution", dateFieldKey) ? "resolutiondate" : dateFieldKey;
    }

    @Override
    public Calendar getCalendarBySearchFilter(ApplicationLink jiraLink, long searchFilter, JiraAccessor.CalendarOptions calendarOptions, long start, long end) throws CredentialsRequiredException, ResponseException, IOException, ParserException {
        this.checkSearchFilterExists(jiraLink, searchFilter);
        String endPoint = this.getIcalendarEndPointBySearchFilter(searchFilter, calendarOptions, start, end);
        this.getSearchFilterJql(jiraLink, searchFilter);
        String jql = "filter=" + searchFilter;
        Calendar calendar = this.getCalendarFromEndPoint(jiraLink, endPoint, jql);
        if (calendarOptions.isIncludeGreenHopperSprints()) {
            calendar.getComponents().addAll(this.getGreenHopperSprintDates(jiraLink, searchFilter));
        }
        return calendar;
    }

    private String getIcalendarEndPointBySearchFilter(long searchFilterId, JiraAccessor.CalendarOptions calendarOptions, long start, long end) {
        StringBuilder endPointBuilder = new StringBuilder(ICAL_ENDPOINT_PREFIX).append("/search/filter/events.ics").append("?_=").append(System.currentTimeMillis());
        endPointBuilder.append("&searchFilterId=").append(searchFilterId);
        this.appendEndPointDateFieldNameParam(endPointBuilder, calendarOptions);
        this.appendEndPointFixVersionParam(endPointBuilder, calendarOptions);
        this.appendEndPointRangeDateLimit(endPointBuilder, start, end);
        return endPointBuilder.toString();
    }

    private void checkSearchFilterExists(ApplicationLink jiraLink, long searchFilterId) throws CredentialsRequiredException, ResponseException {
        try {
            this.getSearchFilterJql(jiraLink, searchFilterId);
        }
        catch (JiraAccessor.JiraPreConditionUnmetException jiraPreConditionUnmet) {
            LOG.debug(String.format("Unable to check if search filter %d exists. The JIRA REST end point for that does not exist.", searchFilterId), (Throwable)((Object)jiraPreConditionUnmet));
        }
    }

    @Override
    public Collection<JiraDateField> getDateFields(ApplicationLink jiraLink, String jql) throws CredentialsRequiredException, ResponseException {
        return this.getDateFieldsInternal(jiraLink, this.getDateFieldsListEndPoint(jql), jql);
    }

    private Collection<JiraDateField> getDateFieldsInternal(ApplicationLink jiraLink, String endPoint, final String jql) throws CredentialsRequiredException, ResponseException {
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        LOG.debug("Retrieving date fields from Jira endpoint {}", (Object)endPoint);
        try {
            this.publishJiraApplinkRequestEvent(jiraLink, "getDateFieldsInternal");
            return (Collection)requestFactory.createRequest(Request.MethodType.GET, endPoint).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Collection<JiraDateField>>(){

                public Collection<JiraDateField> credentialsRequired(Response response) throws ResponseException {
                    throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                }

                public Collection<JiraDateField> handle(Response response) throws ResponseException {
                    if (response.isSuccessful()) {
                        try {
                            String dateFieldsJson = DefaultJiraAccessor.this.readResponseToString(response);
                            JSONArray dateFieldsCollection = new JSONArray(dateFieldsJson);
                            ArrayList<JiraDateField> jiraDateFields = new ArrayList<JiraDateField>();
                            int j = dateFieldsCollection.length();
                            for (int i = 0; i < j; ++i) {
                                JSONObject dateFieldJson = (JSONObject)dateFieldsCollection.get(i);
                                jiraDateFields.add(new JiraDateField(dateFieldJson.getString("key"), dateFieldJson.getString("name"), dateFieldJson.optBoolean("hasSearcher", true)));
                            }
                            if (LOG.isDebugEnabled()) {
                                StringJoiner joiner = new StringJoiner(", ", "{", "}");
                                for (JiraDateField jiraDateField : jiraDateFields) {
                                    JSONObject toJson = jiraDateField.toJson();
                                    joiner.add(toJson.toString());
                                }
                                LOG.debug("Date fields retrieved from Jira: {}", (Object)joiner.toString());
                            }
                            return jiraDateFields;
                        }
                        catch (JSONException invalidJsonFromJira) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)invalidJsonFromJira);
                        }
                        catch (IOException io) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)io);
                        }
                    }
                    if (response.getStatusCode() == 404) {
                        return Collections.emptySet();
                    }
                    throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", jql));
                }
            });
        }
        catch (ResponseException applinkResponseError) {
            Throwable cause = applinkResponseError.getCause();
            if (cause instanceof CredentialsRequiredException) {
                throw (CredentialsRequiredException)cause;
            }
            throw applinkResponseError;
        }
    }

    private boolean isUsingExpandedFieldRetrieval() {
        boolean expandedFieldRetrievalIsDisabled = this.darkFeatureManager.isEnabledForAllUsers(DISABLE_EXPANDED_FIELD_RETRIEVAL).orElse(false);
        return !expandedFieldRetrievalIsDisabled;
    }

    private String getDateFieldsListEndPoint(String jql) {
        return ICAL_ENDPOINT_PREFIX + "/config/fields?jql=" + GeneralUtil.urlEncode((String)jql) + String.format("&useExpandedFieldRetrieval=%b", this.isUsingExpandedFieldRetrieval());
    }

    @Override
    public Collection<JiraDateField> getDateFields(ApplicationLink jiraLink, long searchFilterId) throws CredentialsRequiredException, ResponseException, IOException {
        String jql = "filter=" + searchFilterId;
        return this.getDateFieldsInternal(jiraLink, this.getDateFieldsListEndPoint(searchFilterId), jql);
    }

    private String getDateFieldsListEndPoint(long searchFilterId) {
        return ICAL_ENDPOINT_PREFIX + "/config/fields?searchRequestId=" + searchFilterId + String.format("&useExpandedFieldRetrieval=%b", this.isUsingExpandedFieldRetrieval());
    }

    @Override
    public JqlValidationResult validateJql(ApplicationLink jiraLink, final String jql) throws CredentialsRequiredException, ResponseException, IOException {
        String endPoint = this.getJqlValidationEndPoint();
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        try {
            this.publishJiraApplinkRequestEvent(jiraLink, "validateJql");
            return (JqlValidationResult)((ApplicationLinkRequest)requestFactory.createRequest(Request.MethodType.POST, endPoint).addRequestParameters(new String[]{"jql", jql})).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<JqlValidationResult>(){

                public JqlValidationResult credentialsRequired(Response response) throws ResponseException {
                    throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                }

                public JqlValidationResult handle(Response response) throws ResponseException {
                    if (response.isSuccessful()) {
                        try {
                            int i;
                            int j;
                            String validationResultsJson = DefaultJiraAccessor.this.readResponseToString(response);
                            JSONObject validationResultsObj = new JSONObject(validationResultsJson);
                            JqlValidationResult jqlValidationResult = new JqlValidationResult();
                            if (validationResultsObj.has("warningMessages")) {
                                LinkedHashSet<String> warningMessages = new LinkedHashSet<String>();
                                JSONArray warningMessagesArray = validationResultsObj.getJSONArray("warningMessages");
                                j = warningMessagesArray.length();
                                for (i = 0; i < j; ++i) {
                                    warningMessages.add(warningMessagesArray.getString(i));
                                }
                                jqlValidationResult.setWarningMessages(warningMessages);
                            }
                            if (validationResultsObj.has("errorMessages")) {
                                LinkedHashSet<String> errorMessages = new LinkedHashSet<String>();
                                JSONArray errorMessagesArray = validationResultsObj.getJSONArray("errorMessages");
                                j = errorMessagesArray.length();
                                for (i = 0; i < j; ++i) {
                                    errorMessages.add(errorMessagesArray.getString(i));
                                }
                                jqlValidationResult.setErrorMessages(errorMessages);
                            }
                            return jqlValidationResult;
                        }
                        catch (JSONException invalidJsonFromJira) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)invalidJsonFromJira);
                        }
                        catch (IOException io) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)io);
                        }
                    }
                    if (404 == response.getStatusCode()) {
                        return new JqlValidationResult();
                    }
                    throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", jql));
                }
            });
        }
        catch (ResponseException applinkResponseError) {
            Throwable cause = applinkResponseError.getCause();
            if (cause instanceof CredentialsRequiredException) {
                throw (CredentialsRequiredException)cause;
            }
            throw applinkResponseError;
        }
    }

    private String getJqlValidationEndPoint() {
        return "/rest/ical/1.0/ical/util/jql/validate";
    }

    @Override
    public JqlAutoCompleteResult getAutoComplete(ApplicationLink jiraLink, String fieldName, String fieldValue) throws CredentialsRequiredException, ResponseException, IOException {
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        final String endPoint = this.getJqlAutocompleteEndPoint(fieldName, fieldValue);
        try {
            this.publishJiraApplinkRequestEvent(jiraLink, "getAutoComplete");
            return (JqlAutoCompleteResult)requestFactory.createRequest(Request.MethodType.GET, endPoint).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<JqlAutoCompleteResult>(){

                public JqlAutoCompleteResult credentialsRequired(Response response) throws ResponseException {
                    throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                }

                public JqlAutoCompleteResult handle(Response response) throws ResponseException {
                    if (!response.isSuccessful()) {
                        throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", endPoint));
                    }
                    try {
                        JSONArray resultsArray;
                        String resultsJson = DefaultJiraAccessor.this.readResponseToString(response);
                        JSONObject resultsObject = new JSONObject(resultsJson);
                        ArrayList<JqlAutoCompleteResult.Result> results = new ArrayList<JqlAutoCompleteResult.Result>();
                        if (resultsObject.has("results") && null != (resultsArray = resultsObject.getJSONArray("results"))) {
                            int j = resultsArray.length();
                            for (int i = 0; i < j; ++i) {
                                JSONObject aResult = resultsArray.getJSONObject(i);
                                results.add(new JqlAutoCompleteResult.Result(aResult.getString("displayName"), aResult.getString("value")));
                            }
                        }
                        return new JqlAutoCompleteResult(results);
                    }
                    catch (JSONException invalidJsonFromJira) {
                        throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)invalidJsonFromJira);
                    }
                    catch (IOException io) {
                        throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)io);
                    }
                }
            });
        }
        catch (ResponseException applinkResponseError) {
            Throwable cause = applinkResponseError.getCause();
            if (cause instanceof CredentialsRequiredException) {
                throw (CredentialsRequiredException)cause;
            }
            throw applinkResponseError;
        }
    }

    private String getJqlAutocompleteEndPoint(String fieldName, String fieldValue) {
        return String.format("/rest/api/1.0/jql/autocomplete?_=%d&fieldName=%s&fieldValue=%s", System.currentTimeMillis(), GeneralUtil.urlEncode((String)fieldName), GeneralUtil.urlEncode((String)StringUtils.defaultString(fieldValue)));
    }

    @Override
    public boolean isGreenHopperSprintDatesSupported(ApplicationLink jiraLink) {
        if (!GREENHOPPER_SPRINT_DATES_ENABLED) {
            return false;
        }
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        String endPoint = this.getGreenHopperSprintDatesEndPoint(null);
        Boolean isGreenHopperSprint = null;
        String keyGreenHopperSprint = this.getKeyUserCache(jiraLink);
        Cache greenHopperSprintDateCache = this.getCacheGreenHopperSprintDate();
        if (greenHopperSprintDateCache != null) {
            Object result = greenHopperSprintDateCache.get((Object)keyGreenHopperSprint);
            Boolean bl = isGreenHopperSprint = result != null ? (Boolean)result : null;
        }
        if (isGreenHopperSprint == null) {
            try {
                this.publishJiraApplinkRequestEvent(jiraLink, "isGreenHopperSprintDatesSupported");
                isGreenHopperSprint = (Boolean)requestFactory.createRequest(Request.MethodType.GET, endPoint).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Boolean>(){

                    public Boolean credentialsRequired(Response response) throws ResponseException {
                        throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                    }

                    public Boolean handle(Response response) throws ResponseException {
                        return 404 != response.getStatusCode();
                    }
                });
                if (greenHopperSprintDateCache != null) {
                    greenHopperSprintDateCache.put((Object)keyGreenHopperSprint, (Object)isGreenHopperSprint);
                }
            }
            catch (CredentialsRequiredException authorizationRequired) {
                LOG.info("GreenHopper sprint dates support detection requires authentication - assuming it is supported", (Throwable)authorizationRequired);
                return true;
            }
            catch (Exception e) {
                LOG.error("Unable to property determine if GreenHopper sprint dates are supported", (Throwable)e);
                return false;
            }
        }
        return isGreenHopperSprint;
    }

    private String getGreenHopperSprintDatesEndPoint(String jql) {
        return String.format(GREENHOPPER_SPRINT_DATES_BY_JQL_URI_FORMAT, GeneralUtil.urlEncode((String)StringUtils.defaultString(jql)));
    }

    private ComponentList getGreenHopperSprintDates(final ApplicationLink jiraLink, final String jql) throws CredentialsRequiredException, ResponseException, IOException {
        ComponentList sprintVEvents = new ComponentList();
        UtilTimerStack.push((String)"DefaultJiraAccessor.getGreenHopperSprintDates()");
        if (this.isGreenHopperSprintDatesSupported(jiraLink)) {
            final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
            String endPoint = this.getGreenHopperSprintDatesEndPoint(jql);
            try {
                this.publishJiraApplinkRequestEvent(jiraLink, "getGreenHopperSprintDates");
                sprintVEvents.addAll((Collection)requestFactory.createRequest(Request.MethodType.GET, endPoint).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<ComponentList>(){

                    public ComponentList credentialsRequired(Response response) throws ResponseException {
                        throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                    }

                    public ComponentList handle(Response response) throws ResponseException {
                        if (response.isSuccessful()) {
                            try {
                                return DefaultJiraAccessor.this.getSprintDatesAsVEvents(jiraLink.getDisplayUrl(), new JSONObject(DefaultJiraAccessor.this.readResponseToString(response)));
                            }
                            catch (JSONException invalidJsonFromJira) {
                                throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)invalidJsonFromJira);
                            }
                            catch (IOException io) {
                                throw new ResponseException((Throwable)io);
                            }
                        }
                        throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", jql));
                    }
                }));
            }
            catch (ResponseException applinkResponseError) {
                Throwable cause = applinkResponseError.getCause();
                if (cause instanceof CredentialsRequiredException) {
                    throw (CredentialsRequiredException)cause;
                }
                throw applinkResponseError;
            }
            finally {
                UtilTimerStack.pop((String)"DefaultJiraAccessor.getGreenHopperSprintDates()");
            }
        }
        return sprintVEvents;
    }

    private ComponentList getSprintDatesAsVEvents(URI jiraDisplay, JSONObject greenHopperResponseObj) throws JSONException {
        DateTimeZone dateTimeZone = DateTimeZone.forID((String)greenHopperResponseObj.getString("jodaTimeZoneId"));
        JSONArray sprintsArray = greenHopperResponseObj.getJSONArray("sprints");
        ComponentList sprintVEventObjects = new ComponentList();
        int j = sprintsArray.length();
        for (int i = 0; i < j; ++i) {
            JSONObject sprintEventObj = sprintsArray.getJSONObject(i);
            VEvent aSprintEvent = new VEvent((Date)this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(this.getGreenHopperSprintDateAsDateTime(sprintEventObj.getString("start"), dateTimeZone)), this.jodaIcal4jDateTimeConverter.toIcal4jDateTime(this.getGreenHopperSprintDateAsDateTime(sprintEventObj.getString("end"), dateTimeZone)), sprintEventObj.getString("name"));
            PropertyList<Property> vEventProperties = aSprintEvent.getProperties();
            vEventProperties.add(new Uid(String.format("%d@%s", sprintEventObj.getLong("id"), jiraDisplay.getHost())));
            if (sprintEventObj.has("description")) {
                vEventProperties.add(new Description(StringUtils.defaultString(sprintEventObj.getString("description"))));
            }
            vEventProperties.add(sprintEventObj.getBoolean("closed") ? GreenHopperSprintStatus.CLOSED : GreenHopperSprintStatus.OPEN);
            vEventProperties.add(sprintEventObj.getBoolean("editable") ? GreenHopperSprintEditability.EDITABLE : GreenHopperSprintEditability.NOT_EDITABLE);
            if (sprintEventObj.has("projects")) {
                JSONArray projectsArray = sprintEventObj.getJSONArray("projects");
                int l = projectsArray.length();
                for (int k = 0; k < l; ++k) {
                    JSONObject projectObj = projectsArray.getJSONObject(k);
                    ParameterList parameterList = new ParameterList();
                    XParameter jiraProject = new XParameter("X-JIRA-PROJECT-KEY", projectObj.getString("key"));
                    parameterList.add(jiraProject);
                    vEventProperties.add(new XProperty("X-JIRA-PROJECT", parameterList, projectObj.getString("name")));
                }
            }
            if (sprintEventObj.has("viewBoardsUrl")) {
                vEventProperties.add(new XProperty("X-GREENHOPPER-SPRINT-VIEWBOARDS-URL", sprintEventObj.getString("viewBoardsUrl")));
            }
            if (sprintEventObj.has("sprintHomePageUrl")) {
                vEventProperties.add(new XProperty("X-GREENHOPPER-SPRINT-HOMEPAGE-URL", sprintEventObj.getString("sprintHomePageUrl")));
            }
            sprintVEventObjects.add(aSprintEvent);
        }
        return sprintVEventObjects;
    }

    private DateTime getGreenHopperSprintDateAsDateTime(String date, DateTimeZone dateTimeZone) {
        return new DateTime(Integer.parseInt(date.substring(4, 8)), Integer.parseInt(date.substring(2, 4)), Integer.parseInt(date.substring(0, 2)), Integer.parseInt(date.substring(8, 10)), Integer.parseInt(date.substring(10, 12)), 0, 0, dateTimeZone);
    }

    private ComponentList getGreenHopperSprintDates(final ApplicationLink jiraLink, long searchFilterId) throws CredentialsRequiredException, ResponseException, IOException {
        ComponentList sprintVEvents = new ComponentList();
        if (this.isGreenHopperSprintDatesSupported(jiraLink)) {
            final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
            String endPoint = this.getGreenHopperSprintDatesEndPoint(searchFilterId);
            final String jql = "filter=" + searchFilterId;
            try {
                this.publishJiraApplinkRequestEvent(jiraLink, "getGreenHopperSprintDates");
                return (ComponentList)requestFactory.createRequest(Request.MethodType.GET, endPoint).execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<ComponentList>(){

                    public ComponentList credentialsRequired(Response response) throws ResponseException {
                        throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                    }

                    public ComponentList handle(Response response) throws ResponseException {
                        if (response.isSuccessful()) {
                            try {
                                return DefaultJiraAccessor.this.getSprintDatesAsVEvents(jiraLink.getDisplayUrl(), new JSONObject(DefaultJiraAccessor.this.readResponseToString(response)));
                            }
                            catch (JSONException invalidJsonFromJira) {
                                throw new ResponseException(String.format("The response from %s didn't make sense", jql), (Throwable)invalidJsonFromJira);
                            }
                            catch (IOException io) {
                                throw new ResponseException((Throwable)io);
                            }
                        }
                        throw new JiraAccessor.JiraResponseException(String.format("The response from %s didn't make sense", jql));
                    }
                });
            }
            catch (ResponseException applinkResponseError) {
                Throwable cause = applinkResponseError.getCause();
                if (cause instanceof CredentialsRequiredException) {
                    throw (CredentialsRequiredException)cause;
                }
                throw applinkResponseError;
            }
        }
        return sprintVEvents;
    }

    private String getGreenHopperSprintDatesEndPoint(long searchFilterId) {
        return String.format(GREENHOPPPER_SPRINT_DATES_BY_SEARCH_FILTER_URL_FORMAT, searchFilterId);
    }

    private String getApiVersion(ApplicationLink jiraLink) throws ResponseException, CredentialsRequiredException {
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, REST_PREFIX + "/api/" + API_VERSION_FINAL + "/serverInfo");
        this.publishJiraApplinkRequestEvent(jiraLink, "getApiVersion");
        return (String)request.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<String>(){

            public String credentialsRequired(Response response) throws ResponseException {
                throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
            }

            public String handle(Response response) throws ResponseException {
                if (response.isSuccessful()) {
                    return DefaultJiraAccessor.API_VERSION_FINAL;
                }
                if (response.getStatusCode() == 404) {
                    return DefaultJiraAccessor.API_VERSION_LEGACY;
                }
                throw new CalendarException("calendar3.error.find.api.version", response.getStatusCode());
            }
        });
    }

    @Override
    public String getSearchFilterJql(final ApplicationLink jiraLink, long searchFilterId) throws ResponseException, CredentialsRequiredException {
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        final String endPoint = REST_PREFIX + "/api/" + API_VERSION_FINAL + "/filter/" + searchFilterId;
        ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, endPoint);
        this.publishJiraApplinkRequestEvent(jiraLink, "getSearchFilterJql");
        return (String)request.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<String>(){

            public String credentialsRequired(Response response) throws ResponseException {
                throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
            }

            public String handle(Response response) throws ResponseException {
                if (response.isSuccessful()) {
                    try {
                        return new JSONObject(DefaultJiraAccessor.this.readResponseToString(response)).getString("jql");
                    }
                    catch (JSONException invalidJsonFromJira) {
                        throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)invalidJsonFromJira);
                    }
                    catch (IOException io) {
                        throw new ResponseException((Throwable)io);
                    }
                }
                if (404 == response.getStatusCode()) {
                    throw new JiraAccessor.JiraPreConditionUnmetException(jiraLink, JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound, endPoint);
                }
                throw new JiraAccessor.JiraPreConditionUnmetException(jiraLink, JiraAccessor.JiraPreConditionUnmetException.PreCondition.InvalidSearchFilter, endPoint);
            }
        });
    }

    @Override
    public int getIssuesReturnedByJql(final ApplicationLink jiraLink, String jql, Collection<String> singleDateFields, Collection<String> durations) throws ResponseException, CredentialsRequiredException {
        final String endPoint = this.getIssuesCountEndPoint();
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        try {
            ApplicationLinkRequest resultsCountRequest = (ApplicationLinkRequest)((ApplicationLinkRequest)requestFactory.createRequest(Request.MethodType.POST, endPoint).addRequestParameters(new String[]{"jql", jql})).addRequestParameters(new String[]{"includeFixVersions", (null != singleDateFields && singleDateFields.contains("versiondue") ? Boolean.TRUE : Boolean.FALSE).toString()});
            if (null != singleDateFields) {
                for (String singleDateField : Collections2.filter(singleDateFields, (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)"sprint")))) {
                    resultsCountRequest.addRequestParameters(new String[]{"dateFieldName", singleDateField});
                }
            }
            if (null != durations) {
                for (String durationString : durations) {
                    resultsCountRequest.addRequestParameters(new String[]{"duration", durationString});
                }
            }
            this.publishJiraApplinkRequestEvent(jiraLink, "getIssuesReturnedByJql");
            return (Integer)resultsCountRequest.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Integer>(){

                public Integer credentialsRequired(Response response) throws ResponseException {
                    throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
                }

                public Integer handle(Response response) throws ResponseException {
                    if (response.isSuccessful()) {
                        try {
                            return new JSONObject(DefaultJiraAccessor.this.readResponseToString(response)).getInt("total");
                        }
                        catch (JSONException invalidJsonFromJira) {
                            throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)invalidJsonFromJira);
                        }
                        catch (IOException io) {
                            throw new ResponseException((Throwable)io);
                        }
                    }
                    if (404 == response.getStatusCode()) {
                        throw new JiraAccessor.JiraPreConditionUnmetException(jiraLink, JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound, endPoint);
                    }
                    throw new ResponseException(String.format("Unable to validate calendar against JIRA.  JIRA responded with: HTTP %d", response.getStatusCode()));
                }
            });
        }
        catch (ResponseException applinkResponseError) {
            Throwable cause = applinkResponseError.getCause();
            if (cause instanceof CredentialsRequiredException) {
                throw (CredentialsRequiredException)cause;
            }
            if (applinkResponseError instanceof JiraAccessor.JiraPreConditionUnmetException && ((JiraAccessor.JiraPreConditionUnmetException)applinkResponseError).getPreCondition() == JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound) {
                return this.getIssueCountWithJiraApi(jiraLink, jql);
            }
            throw applinkResponseError;
        }
    }

    private ApplicationLinkRequest setApplicationLinkRequestBody(ApplicationLinkRequest applicationLinkRequest, String requestBody, String requestContentType) {
        if (applicationLinkRequest != null) {
            Method setRequestBodyMethod = null;
            try {
                setRequestBodyMethod = applicationLinkRequest.getClass().getMethod("setRequestBody", String.class, String.class);
            }
            catch (NoSuchMethodException e) {
                LOG.debug(e.getMessage());
            }
            if (setRequestBodyMethod != null) {
                try {
                    setRequestBodyMethod.invoke((Object)applicationLinkRequest, requestBody, requestContentType);
                }
                catch (IllegalAccessException e) {
                    LOG.error(e.getMessage());
                }
                catch (InvocationTargetException e) {
                    LOG.error(e.getMessage());
                }
            } else {
                applicationLinkRequest.setRequestBody(requestBody, requestContentType);
            }
        }
        return applicationLinkRequest;
    }

    private int getIssueCountWithJiraApi(ApplicationLink jiraLink, final String jql) throws CredentialsRequiredException, ResponseException {
        final ApplicationLinkRequestFactory requestFactory = jiraLink.createAuthenticatedRequestFactory();
        final String endPoint = REST_PREFIX + "/api/" + API_VERSION_FINAL + "/search";
        ApplicationLinkRequest request = this.setApplicationLinkRequestBody(requestFactory.createRequest(Request.MethodType.POST, endPoint), new JSONObject((Map)new HashMap<String, String>(){
            {
                this.put("jql", jql);
                this.put("startAt", String.valueOf(0));
                this.put("maxResults", String.valueOf(1));
            }
        }).toString(), "application/json");
        this.publishJiraApplinkRequestEvent(jiraLink, "getIssueCountWithJiraApi");
        return (Integer)request.execute((ApplicationLinkResponseHandler)new ApplicationLinkResponseHandler<Integer>(){

            public Integer credentialsRequired(Response response) throws ResponseException {
                throw new ResponseException((Throwable)new CredentialsRequiredException((AuthorisationURIGenerator)requestFactory, ""));
            }

            public Integer handle(Response response) throws ResponseException {
                if (response.isSuccessful()) {
                    try {
                        return new JSONObject(DefaultJiraAccessor.this.readResponseToString(response)).getInt("total");
                    }
                    catch (JSONException invalidJsonFromJira) {
                        throw new ResponseException(String.format("The response from %s didn't make sense", endPoint), (Throwable)invalidJsonFromJira);
                    }
                    catch (IOException io) {
                        throw new ResponseException((Throwable)io);
                    }
                }
                LOG.warn(String.format("Unable to get total results from JQL \"%s\": HTTP %d", jql, response.getStatusCode()));
                return CalendarUtil.MAX_JIRA_ISSUES_TO_DISPLAY;
            }
        });
    }

    private String getIssuesCountEndPoint() {
        return "/rest/ical/1.0/ical/util/jql/count";
    }

    private Cache getCacheQueryOptionJIRA() {
        if (!CACHE_JIRA_ACCESSOR_ENABLE) {
            return null;
        }
        return this.cacheManager.getCache(this.CACHE_JIRA_QUERY_OPTION, null, new CacheSettingsBuilder().local().build());
    }

    private Cache getCacheGreenHopperSprintDate() {
        if (!CACHE_JIRA_ACCESSOR_ENABLE) {
            return null;
        }
        return this.cacheManager.getCache(this.CACHE_GREENHOPPER_SPRINT_DATE, null, new CacheSettingsBuilder().local().build());
    }

    private String getKeyUserCache(ApplicationLink applicationLink) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return currentUser == null || currentUser.getKey() == null || applicationLink == null || applicationLink.getId() == null ? "" : currentUser.getKey().toString() + ":" + applicationLink.getId().toString();
    }
}

