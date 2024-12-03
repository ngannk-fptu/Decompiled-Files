/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.core.util.PairType
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.json.JSONException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.GenericMessage;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.ExternalCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraCalendarTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.supporters.jirareminder.DefaultJiraReminderSupport;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.Duration;
import com.atlassian.confluence.extra.calendar3.model.JqlValidationResult;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.Project;
import com.atlassian.confluence.extra.calendar3.model.QueryOptions;
import com.atlassian.confluence.extra.calendar3.model.SearchFilter;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.util.PairType;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fortuna.ical4j.extensions.property.WrCalDesc;
import net.fortuna.ical4j.extensions.property.WrCalName;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.transform.Transformer;
import net.java.ao.RawEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractJiraSubCalendarDataStore<T extends AbstractJiraSubCalendar>
extends ExternalCalendarDataStore<T>
implements DelegatableCalendarDataStore<T>,
InitializingBean,
JiraCalendarTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(JiraCalendarDataStore.class);
    private static final String CACHE_DATE_RANGE_FIELD = "rangedate";
    private static final String CACHE_FIELD_VALUE_DELIMITER = ":";
    private static final String CACHE_FIELD_VALUE_DATE_RANGE_DELIMITER = "-";
    public static final String SUB_CALENDAR_TYPE = "jira";
    private static final String APPLICATION_LINK_EDIT_STATUS_CACHE_KEY = AbstractJiraSubCalendarDataStore.class.getName() + ":application-link-edit-status.c711";
    private final CacheManager cacheManager;
    private final JiraAccessor jiraAccessor;
    private final CalendarHelper calendarHelper;

    public AbstractJiraSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, CacheManager cacheManager, JiraAccessor jiraAccessor, CalendarHelper calendarHelper) {
        super(dataStoreCommonPropertyAccessor);
        this.cacheManager = cacheManager;
        this.jiraAccessor = jiraAccessor;
        this.calendarHelper = calendarHelper;
    }

    public void afterPropertiesSet() throws Exception {
        try {
            this.getApplicationLinkEditStatusCache().removeAll();
        }
        catch (RuntimeException re) {
            LOG.warn("Error initializing cache AbstractJiraSubCalendarDataStore. It's probably because of a race condition to get it initialized. If so, nothing to worry about", (Throwable)re);
            LOG.debug("Error detail is:", (Throwable)re);
        }
    }

    @Override
    public String getSubCalendarDataCacheKey(T subCalendar) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        long start = ((AbstractJiraSubCalendar)subCalendar).getStart();
        long end = ((AbstractJiraSubCalendar)subCalendar).getEnd();
        StringBuilder stringBuilder = new StringBuilder();
        if (start > 0L && end > 0L) {
            stringBuilder.append("rangedate:").append(start).append(CACHE_FIELD_VALUE_DATE_RANGE_DELIMITER).append(end);
            stringBuilder.append("::");
        }
        if (null != currentUser) {
            stringBuilder.append(currentUser.getKey().toString());
            stringBuilder.append("::");
        }
        stringBuilder.append(super.getSubCalendarDataCacheKey(subCalendar));
        return stringBuilder.toString();
    }

    public static <T extends AbstractJiraSubCalendar> void setQueryDateRangeFromCacheKey(String cacheKey, T subCalendar) {
        String[] splitResult = cacheKey.split("::");
        String queryDateRangeField = splitResult[0];
        if (StringUtils.isNotEmpty(queryDateRangeField) && queryDateRangeField.contains(CACHE_DATE_RANGE_FIELD)) {
            String[] queryDateRange = queryDateRangeField.split(CACHE_FIELD_VALUE_DELIMITER);
            if (queryDateRange == null || queryDateRange.length == 0) {
                LOG.error("Could not get date range field from cache key");
                return;
            }
            String[] dateRangeStr = queryDateRange[1].split(CACHE_FIELD_VALUE_DATE_RANGE_DELIMITER);
            if (dateRangeStr == null || dateRangeStr.length == 0) {
                LOG.error("Could not get date range value from date range field from cache key");
                return;
            }
            try {
                long startTime = Long.parseLong(dateRangeStr[0]);
                long stopTime = Long.parseLong(dateRangeStr[1]);
                subCalendar.setStart(startTime);
                subCalendar.setEnd(stopTime);
            }
            catch (NumberFormatException ex) {
                LOG.error("Could not get time range value from cache key");
            }
        }
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new SubCalendarSummary(subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    protected T fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        T subCalendar;
        block8: {
            subCalendar = this.createNewJiraSubCalendar();
            ((AbstractJiraSubCalendar)subCalendar).setId(subCalendarEntity.getID());
            ((SubCalendar)subCalendar).setName(subCalendarEntity.getName());
            ((SubCalendar)subCalendar).setDescription(StringUtils.join((Object[])StringUtils.split(subCalendarEntity.getDescription(), "\r\n"), " "));
            ((SubCalendar)subCalendar).setColor(subCalendarEntity.getColour());
            ((SubCalendar)subCalendar).setSpaceKey(subCalendarEntity.getSpaceKey());
            ((AbstractJiraSubCalendar)subCalendar).setSpaceName(this.getSpaceName(((PersistedSubCalendar)subCalendar).getSpaceKey()));
            ((AbstractJiraSubCalendar)subCalendar).setCreator(subCalendarEntity.getCreator());
            ((SubCalendar)subCalendar).setDisableEventTypes(this.getDisableEventType(subCalendarEntity));
            ((SubCalendar)subCalendar).setCustomEventTypes(this.getCustomEventType(subCalendarEntity));
            ((SubCalendar)subCalendar).setCreatedDate(subCalendarEntity.getCreated());
            ((SubCalendar)subCalendar).setLastUpdateDate(subCalendarEntity.getLastModified());
            ((SubCalendar)subCalendar).setTimeZoneId(subCalendarEntity.getTimeZoneId());
            ((AbstractJiraSubCalendar)subCalendar).setApplicationId(this.getSubCalendarEntityPropertyValue(subCalendarEntity, "applicationId"));
            ((AbstractJiraSubCalendar)subCalendar).setApplicationName(((AbstractJiraSubCalendar)subCalendar).getApplicationId());
            ((AbstractJiraSubCalendar)subCalendar).setDateFieldNames(Sets.newHashSet(this.getSubCalendarEntityPropertyValues(subCalendarEntity, "dateFieldName")));
            ((SubCalendar)subCalendar).setStoreKey(this.getStoreKey());
            ((AbstractJiraSubCalendar)subCalendar).setDurations(Sets.newHashSet((Iterable)Collections2.transform(this.getSubCalendarEntityPropertyValues(subCalendarEntity, "duration"), durationString -> {
                String[] durationTokens = StringUtils.split(durationString, "/", 2);
                return new Duration(durationTokens[0], durationTokens[1]);
            })));
            String projectKey = this.getSubCalendarEntityPropertyValue(subCalendarEntity, "projectKey");
            String searchFilterIdStr = this.getSubCalendarEntityPropertyValue(subCalendarEntity, "searchFilterId");
            long searchFilterId = StringUtils.isNotBlank(searchFilterIdStr) ? Long.parseLong(searchFilterIdStr) : 0L;
            String jql = this.getSubCalendarEntityPropertyValue(subCalendarEntity, "jql");
            ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(((AbstractJiraSubCalendar)subCalendar).getApplicationId());
            if (jiraLink != null) {
                ((AbstractJiraSubCalendar)subCalendar).setApplicationName(jiraLink.getName());
            }
            try {
                if (StringUtils.isNotBlank(projectKey)) {
                    ((AbstractJiraSubCalendar)subCalendar).setProjectKey(projectKey);
                    ((AbstractJiraSubCalendar)subCalendar).setProjectName(projectKey);
                    ((SubCalendar)subCalendar).setSourceLocation(String.format("jira://%s?projectKey=%s&%s", ((AbstractJiraSubCalendar)subCalendar).getApplicationId(), projectKey, this.getDateFieldNamesAsQueryParam(subCalendar)));
                } else if (0L < searchFilterId) {
                    ((AbstractJiraSubCalendar)subCalendar).setSearchFilterId(searchFilterId);
                    ((AbstractJiraSubCalendar)subCalendar).setSearchFilterName(String.valueOf(searchFilterId));
                    ((SubCalendar)subCalendar).setSourceLocation(String.format("jira://%s?searchFilterId=%d&%s", ((AbstractJiraSubCalendar)subCalendar).getApplicationId(), searchFilterId, this.getDateFieldNamesAsQueryParam(subCalendar)));
                } else if (StringUtils.isNotBlank(jql)) {
                    ((AbstractJiraSubCalendar)subCalendar).setJql(jql);
                    ((SubCalendar)subCalendar).setSourceLocation(String.format("jira://%s?jql=%s&%s", ((AbstractJiraSubCalendar)subCalendar).getApplicationId(), GeneralUtil.urlEncode((String)jql), this.getDateFieldNamesAsQueryParam(subCalendar)));
                }
            }
            catch (Exception e) {
                LOG.info(String.format("Unable to retrieve the name of project %s", projectKey));
                if (!LOG.isDebugEnabled()) break block8;
                LOG.debug(String.format("Unable to retrieve the name of project %s", projectKey), (Throwable)e);
            }
        }
        return subCalendar;
    }

    private String getDateFieldNamesAsQueryParam(T abstractJiraSubCalendar) {
        Set<Duration> durations;
        StringBuilder dateFieldNamesQueryParamBuilder = new StringBuilder();
        Set<String> dateFieldNames = ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDateFieldNames();
        if (null != dateFieldNames && !dateFieldNames.isEmpty()) {
            for (String dateFieldName : dateFieldNames) {
                this.appendQueryParam(dateFieldNamesQueryParamBuilder, "dateFieldName", dateFieldName);
            }
        }
        if (null != (durations = ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDurations()) && !durations.isEmpty()) {
            StringBuilder durationFieldBuilder = new StringBuilder();
            for (Duration duration : durations) {
                durationFieldBuilder.setLength(0);
                this.appendQueryParam(dateFieldNamesQueryParamBuilder, "duration", durationFieldBuilder.append(duration.getStartDateFieldName()).append('/').append(duration.getEndDateFieldName()).toString());
            }
        }
        return dateFieldNamesQueryParamBuilder.toString();
    }

    private void appendQueryParam(StringBuilder urlBuilder, String paramName, String paramValue) {
        if (urlBuilder.length() > 0) {
            urlBuilder.append('&');
        }
        urlBuilder.append(paramName).append('=').append(GeneralUtil.urlEncode((String)paramValue));
    }

    protected abstract T createNewJiraSubCalendar();

    private String getText(String key) {
        return this.getI18NBean().getText(key);
    }

    @Override
    protected SubCalendarEntity toStorageFormat(SubCalendar subCalendar) {
        Set<Duration> durations;
        SubCalendarEntity subCalendarEntity = super.toStorageFormat(subCalendar);
        JiraSubCalendarSource jiraSubCalendarSource = JiraSubCalendarSource.parse(subCalendar.getSourceLocation(), this.calendarHelper);
        this.getActiveObjects().delete((RawEntity[])subCalendarEntity.getExtraProperties());
        this.createSubCalendarEntityProperty(subCalendarEntity, "applicationId", jiraSubCalendarSource.getApplicationId());
        String projectKey = jiraSubCalendarSource.getProjectKey();
        long searchFilterId = jiraSubCalendarSource.getSearchFilterId();
        String jql = jiraSubCalendarSource.getJql();
        if (StringUtils.isNotBlank(projectKey)) {
            this.createSubCalendarEntityProperty(subCalendarEntity, "projectKey", projectKey);
        } else if (0L < searchFilterId) {
            this.createSubCalendarEntityProperty(subCalendarEntity, "searchFilterId", Long.toString(searchFilterId));
        } else if (StringUtils.isNotBlank(jql)) {
            this.createSubCalendarEntityProperty(subCalendarEntity, "jql", jql);
        }
        Set<String> dateFieldNames = jiraSubCalendarSource.getDateFieldNames();
        if (dateFieldNames != null) {
            for (String dateFieldName : dateFieldNames) {
                this.createSubCalendarEntityProperty(subCalendarEntity, "dateFieldName", dateFieldName);
            }
        }
        if ((durations = jiraSubCalendarSource.getDurations()) != null) {
            for (Duration duration : durations) {
                this.createSubCalendarEntityProperty(subCalendarEntity, "duration", duration.getStartDateFieldName() + "/" + duration.getEndDateFieldName());
            }
        }
        return this.getSubCalendarEntity(subCalendarEntity.getID());
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        String location = StringUtils.defaultString(subCalendar.getSourceLocation());
        String locationWithoutProtocol = location.substring("jira://".length());
        if (locationWithoutProtocol.indexOf(63) == 0) {
            this.addFieldError(fieldErrors, "server", this.getText("calendar3.error.blank"));
        } else {
            int idxOfQueryString = locationWithoutProtocol.indexOf(63);
            if (-1 == idxOfQueryString || locationWithoutProtocol.length() - 1 == idxOfQueryString) {
                this.addFieldError(fieldErrors, "jiraQueryOptions", this.getText("calendar3.jira.error.server.idnotspecified"));
            } else {
                String applicationId = StringUtils.defaultString(1 < idxOfQueryString ? locationWithoutProtocol.substring(0, idxOfQueryString) : null);
                ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(applicationId);
                if (null == jiraLink) {
                    this.addFieldError(fieldErrors, "server", this.getText("calendar3.jira.error.server.invalid", Arrays.asList(applicationId)));
                } else {
                    String locationQueryString = locationWithoutProtocol.substring(idxOfQueryString);
                    String[] queryParams = StringUtils.split(locationQueryString, "?&");
                    HashMap<String, List<String>> queryParamsMap = new HashMap<String, List<String>>();
                    for (String queryParam : queryParams) {
                        ArrayList<String> paramValues;
                        String paramValue;
                        String[] paramPair = StringUtils.split(queryParam, "=");
                        if (paramPair.length <= 0) continue;
                        String paramName = paramPair[0];
                        String string = paramValue = paramPair.length > 1 ? paramPair[1] : null;
                        if (queryParamsMap.containsKey(paramName)) {
                            paramValues = (ArrayList<String>)queryParamsMap.get(paramName);
                        } else {
                            paramValues = new ArrayList<String>();
                            queryParamsMap.put(paramName, paramValues);
                        }
                        if (!StringUtils.isNotBlank(paramValue)) continue;
                        paramValues.add(paramValue);
                    }
                    List dateFieldNames = (List)queryParamsMap.get("dateFieldName");
                    ArrayList<String> durations = (ArrayList<String>)queryParamsMap.get("duration");
                    if (null != durations) {
                        durations = new ArrayList<String>(Collections2.transform((Collection)durations, GeneralUtil::urlDecode));
                    }
                    if (queryParamsMap.containsKey("projectKey")) {
                        this.validateProjectKey(fieldErrors, jiraLink, queryParamsMap, dateFieldNames, durations);
                    } else if (queryParamsMap.containsKey("jql")) {
                        this.validateJql(fieldErrors, jiraLink, queryParamsMap, dateFieldNames, durations);
                    } else if (queryParamsMap.containsKey("searchFilterId")) {
                        this.validateSearchFilter(fieldErrors, jiraLink, queryParamsMap, dateFieldNames, durations);
                    }
                    if (!(queryParamsMap.containsKey("projectKey") || queryParamsMap.containsKey("searchFilterId") || queryParamsMap.containsKey("jql"))) {
                        this.addFieldError(fieldErrors, "jiraQueryOptions", this.getText("calendar3.error.blank"));
                    }
                    this.validateDateFields(fieldErrors, queryParamsMap, dateFieldNames);
                }
            }
        }
    }

    private void validateDateFields(Map<String, List<String>> fieldErrors, Map<String, List<String>> queryParamsMap, List<String> dateFieldNames) {
        List<String> durationParams = queryParamsMap.get("duration");
        if ((null == dateFieldNames || dateFieldNames.isEmpty()) && (null == durationParams || durationParams.isEmpty())) {
            this.addFieldError(fieldErrors, "dateFieldName", this.getText("calendar3.error.jiradatefieldnotset"));
        } else if (null != durationParams && !durationParams.isEmpty()) {
            for (String durationParam : durationParams) {
                String[] durationTokens = StringUtils.split(GeneralUtil.urlDecode((String)durationParam), "/", 2);
                if (durationTokens.length == 2) {
                    if (!StringUtils.equals(durationTokens[0], durationTokens[1])) continue;
                    this.addFieldError(fieldErrors, "dateFieldName", this.getText("calendar3.error.jiradaterange.duplicatefields"));
                    continue;
                }
                this.addFieldError(fieldErrors, "dateFieldName", this.getText("calendar3.error.jiradaterange.incomplete"));
            }
        }
    }

    private void validateSearchFilter(Map<String, List<String>> fieldErrors, ApplicationLink jiraLink, Map<String, List<String>> queryParamsMap, List<String> dateFieldNames, List<String> durations) {
        String searchFilterIdString = StringUtils.join((Collection)queryParamsMap.get("searchFilterId"), "");
        if (StringUtils.isBlank(searchFilterIdString) || !StringUtils.isNumeric(searchFilterIdString)) {
            this.addFieldError(fieldErrors, "searchFilterId", this.getText("calendar3.jira.error.searchfilter.invalid", Arrays.asList(jiraLink.getName())));
        } else {
            long searchFilterId = Long.parseLong(searchFilterIdString);
            try {
                QueryOptions queryOptions = this.jiraAccessor.getQueryOptions(jiraLink);
                List<SearchFilter> searchFilters = queryOptions.getSearchFilters();
                if (null == searchFilters || Collections2.filter(searchFilters, searchFilter -> searchFilter.getId() == searchFilterId).size() != 1) {
                    this.addFieldError(fieldErrors, "searchFilterId", this.getText("calendar3.jira.error.searchfilter.invalid", Arrays.asList(jiraLink.getName())));
                }
            }
            catch (Exception unableToValidateSearchFilter) {
                LOG.error(String.format("Error occurred while validating search filter %d against %s (%s)", searchFilterId, jiraLink.getName(), jiraLink.getId()), (Throwable)unableToValidateSearchFilter);
                this.addFieldError(fieldErrors, "searchFilterId", this.getText("calendar3.jira.error.searchfilter.cannotvalidate", Arrays.asList(jiraLink.getName())));
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void validateJql(Map<String, List<String>> fieldErrors, ApplicationLink jiraLink, Map<String, List<String>> queryParamsMap, List<String> dateFieldNames, List<String> durations) {
        String jql = GeneralUtil.urlDecode((String)StringUtils.join((Collection)queryParamsMap.get("jql"), ""));
        if (StringUtils.isNotBlank(jql)) {
            try {
                JqlValidationResult jqlValidationResult = this.jiraAccessor.validateJql(jiraLink, jql);
                if (jqlValidationResult.isValid()) return;
                Set<String> validationMessages = jqlValidationResult.getWarningMessages();
                if (null != validationMessages) {
                    for (String message : validationMessages) {
                        this.addFieldError(fieldErrors, "jql", message);
                    }
                }
                if (null == (validationMessages = jqlValidationResult.getErrorMessages())) return;
                for (String message : validationMessages) {
                    this.addFieldError(fieldErrors, "jql", message);
                }
                return;
            }
            catch (Exception unableToValidateJql) {
                LOG.error(String.format("Error occurred while validating JQL %s against %s (%s)", jql, jiraLink.getName(), jiraLink.getId()), (Throwable)unableToValidateJql);
                this.addFieldError(fieldErrors, "jql", this.getText("calendar3.jira.error.jql.cannotvalidate", Arrays.asList(jiraLink.getName())));
                return;
            }
        } else {
            this.addFieldError(fieldErrors, "jql", this.getText("calendar3.jira.error.jql.blank"));
        }
    }

    private void validateProjectKey(Map<String, List<String>> fieldErrors, ApplicationLink jiraLink, Map<String, List<String>> queryParamsMap, List<String> dateFieldNames, List<String> durations) {
        String projectKey = StringUtils.join((Collection)queryParamsMap.get("projectKey"), "");
        try {
            QueryOptions queryOptions = this.jiraAccessor.getQueryOptions(jiraLink);
            List<Project> projects = queryOptions.getProjects();
            if (projects == null) {
                this.addFieldError(fieldErrors, "project", "");
            } else if (Collections2.filter(projects, project -> StringUtils.equals(projectKey, project.getKey())).size() != 1) {
                this.addFieldError(fieldErrors, "project", this.getText("calendar3.jira.error.project.doesnotexist", Arrays.asList(projectKey)));
            }
        }
        catch (Exception unableToValidateProject) {
            LOG.error(String.format("Error occurred while validating project key %s against %s (%s)", projectKey, jiraLink.getName(), jiraLink.getId()), (Throwable)unableToValidateProject);
            this.addFieldError(fieldErrors, "project", this.getText("calendar3.jira.error.project.cannotvalidate", Arrays.asList(jiraLink.getName())));
        }
    }

    @Override
    protected Calendar getSubCalendarContentInternal(T subCalendar) throws Exception {
        ApplicationLink jiraLink = this.getJiraLink(((AbstractJiraSubCalendar)subCalendar).getApplicationId());
        JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper = this.getJodaIcal4jTimeZoneMapper();
        if (null == jiraLink) {
            throw new CalendarException("calendar3.error.unabletofindjiraappid", ((AbstractJiraSubCalendar)subCalendar).getApplicationId());
        }
        try {
            String projectKey = ((AbstractJiraSubCalendar)subCalendar).getProjectKey();
            long searchFilterId = ((AbstractJiraSubCalendar)subCalendar).getSearchFilterId();
            String jql = ((AbstractJiraSubCalendar)subCalendar).getJql();
            long start = ((AbstractJiraSubCalendar)subCalendar).getStart();
            long end = ((AbstractJiraSubCalendar)subCalendar).getEnd();
            Calendar calendar = StringUtils.isNotBlank(projectKey) ? this.jiraAccessor.getCalendarByJql(jiraLink, String.format("project = \"%s\"", projectKey), this.getCalendarOptions(subCalendar), start, end) : (0L < searchFilterId ? this.jiraAccessor.getCalendarBySearchFilter(jiraLink, searchFilterId, this.getCalendarOptions(subCalendar), start, end) : this.jiraAccessor.getCalendarByJql(jiraLink, StringUtils.defaultString(jql), this.getCalendarOptions(subCalendar), start, end));
            PropertyList<Property> calendarProperties = calendar.getProperties();
            calendarProperties.add(new WrCalName(new ParameterList(), ((PersistedSubCalendar)subCalendar).getName()));
            calendarProperties.add(new WrCalDesc(new ParameterList(), StringUtils.defaultString(((PersistedSubCalendar)subCalendar).getDescription()).replaceAll("((\\r\\n)|\\r|\\n)", "\\n")));
            VTimeZone vTimeZone = (VTimeZone)calendar.getComponent("VTIMEZONE");
            if (null == vTimeZone) {
                calendar.getComponents().add(jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(jodaIcal4jTimeZoneMapper.getSystemTimeZoneIdJoda()).getVTimeZone());
            }
            return this.getCalendarWithGloballyUniqueVEventUid(calendar, (AbstractJiraSubCalendar)subCalendar);
        }
        catch (ResponseException applinksError) {
            JiraAccessor.JiraPreConditionUnmetException jiraPreConditionUnmetException;
            if (applinksError instanceof JiraAccessor.JiraPreConditionUnmetException && (jiraPreConditionUnmetException = (JiraAccessor.JiraPreConditionUnmetException)applinksError).getPreCondition() == JiraAccessor.JiraPreConditionUnmetException.PreCondition.InvalidSearchFilter) {
                throw new CalendarException((Exception)((Object)applinksError), "calendar3.error.invalidsearchfilter", ((PersistedSubCalendar)subCalendar).getName(), ((AbstractJiraSubCalendar)subCalendar).getId());
            }
            if (applinksError instanceof JiraAccessor.JiraResponseException) {
                JiraAccessor.JiraResponseException jiraResponseException = (JiraAccessor.JiraResponseException)applinksError;
                throw new CalendarException((Exception)((Object)applinksError), "calendar3.error.jiraresponse", ((PersistedSubCalendar)subCalendar).getName(), StringUtils.defaultString(jiraResponseException.getMessage()));
            }
            if (applinksError instanceof JiraAccessor.JiraJQLWrongResponseException) {
                LOG.error(applinksError.getMessage());
                throw new CalendarException((Exception)((Object)applinksError), true, CalendarException.StatusError.JQL_WRONG.getStatusNum(), "calendar3.jira.error.jqlwrong.one", ((PersistedSubCalendar)subCalendar).getName());
            }
            throw new CalendarException((Exception)((Object)applinksError), "calendar3.error.jiralink", ((PersistedSubCalendar)subCalendar).getName(), StringUtils.defaultString(applinksError.getMessage()), jiraLink.getName());
        }
    }

    private Calendar getCalendarWithGloballyUniqueVEventUid(Calendar calendar, final AbstractJiraSubCalendar abstractJiraSubCalendar) {
        return new Transformer<Calendar>(){

            @Override
            public Calendar transform(Calendar calendar) {
                StringBuilder uidBuilder = new StringBuilder();
                ComponentList vEventComponents = calendar.getComponents("VEVENT");
                if (!vEventComponents.isEmpty()) {
                    for (VEvent vEvent : vEventComponents) {
                        Uid uid = vEvent.getUid();
                        uidBuilder.setLength(0);
                        uid.setValue(uidBuilder.append(abstractJiraSubCalendar.getId()).append('-').append(uid.getValue()).toString());
                    }
                }
                return calendar;
            }
        }.transform(calendar);
    }

    private JiraAccessor.CalendarOptions getCalendarOptions(T abstractJiraSubCalendar) {
        return new JiraAccessor.CalendarOptions(new HashSet<PairType>(Collections2.transform(null == ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDurations() ? Collections.emptySet() : ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDurations(), duration -> new PairType((Serializable)((Object)duration.getStartDateFieldName()), (Serializable)((Object)duration.getEndDateFieldName())))), ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDateFieldNames());
    }

    protected ApplicationLink getJiraLink(String jiraApplicationId) {
        for (ApplicationLink jiraLink : this.jiraAccessor.getLinkedJiraApplications()) {
            if (!StringUtils.equals(jiraLink.getId().get(), jiraApplicationId)) continue;
            return jiraLink;
        }
        return null;
    }

    @Override
    public List<Message> getSubCalendarWarnings(T abstractJiraSubCalendar) {
        ApplicationLink applicationLink = this.getJiraLink(((AbstractJiraSubCalendar)abstractJiraSubCalendar).getApplicationId());
        ArrayList<Message> warnings = new ArrayList<Message>();
        try {
            if (null != applicationLink) {
                Set<Duration> durations = ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDurations();
                if (this.isJqlReturningTooManyIssues(applicationLink, this.getSubCalendarJql(applicationLink, abstractJiraSubCalendar), ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getDateFieldNames(), Collections2.transform(null == durations ? Collections.emptySet() : durations, (Function)new Function<Duration, String>(){
                    private final StringBuilder durationStringBuilder = new StringBuilder();

                    public String apply(Duration duration) {
                        this.durationStringBuilder.setLength(0);
                        return this.durationStringBuilder.append(duration.getStartDateFieldName()).append('/').append(duration.getEndDateFieldName()).toString();
                    }
                }))) {
                    warnings.add(new GenericMessage("calendar3.jira.error.calendartruncated", new Serializable[]{((PersistedSubCalendar)abstractJiraSubCalendar).getName(), Integer.valueOf(AbstractJiraSubCalendarDataStore.getMaxJiraIssuesToDisplay())}));
                }
            }
        }
        catch (JiraAccessor.JiraPreConditionUnmetException jiraPreConditionUnmetException) {
            LOG.error(String.format("Unable to get matching issues count from %s", applicationLink.getName()), (Throwable)((Object)jiraPreConditionUnmetException));
            JiraAccessor.JiraPreConditionUnmetException.PreCondition preCondition = jiraPreConditionUnmetException.getPreCondition();
            if (JiraAccessor.JiraPreConditionUnmetException.PreCondition.EndpointNotFound.equals((Object)preCondition)) {
                warnings.add(new GenericMessage("calendar3.jira.error.searchfilter.nonefound", new Serializable[]{applicationLink.getName()}));
            } else if (JiraAccessor.JiraPreConditionUnmetException.PreCondition.InvalidSearchFilter.equals((Object)preCondition)) {
                warnings.add(new GenericMessage("calendar3.jira.error.searchfilter.invalid", new Serializable[]{applicationLink.getName()}));
            }
        }
        catch (ResponseException searchError) {
            LOG.error(String.format("Unable to get matching issues count from %s", applicationLink.getName()), (Throwable)searchError);
            warnings.add(new GenericMessage("calendar3.error.jira.error", new Serializable[]{searchError}));
        }
        catch (CredentialsRequiredException needAuth) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Not authorised to get issues count from %s", applicationLink.getName()), (Throwable)needAuth);
            } else {
                LOG.warn(String.format("Not authorised to get issues count from %s. Log at DEBUG level for more info.", applicationLink.getName()));
            }
            warnings.add(new GenericMessage("calendar3.error.jira.error", new Serializable[]{needAuth}));
        }
        catch (Exception ex) {
            LOG.warn(String.format("Exception when try to contact with Jira server on Applink {}", applicationLink.getName()));
            warnings.add(new GenericMessage("calendar3.error.jira.error", ex));
        }
        return warnings;
    }

    private boolean isJqlReturningTooManyIssues(ApplicationLink jiraLink, String jql, Collection<String> dateFields, Collection<String> durations) throws ResponseException, CredentialsRequiredException {
        if (null != dateFields && dateFields.size() == 1 && (dateFields.contains("versiondue") || dateFields.contains("sprint"))) {
            return false;
        }
        return AbstractJiraSubCalendarDataStore.getMaxJiraIssuesToDisplay() < this.jiraAccessor.getIssuesReturnedByJql(jiraLink, jql, dateFields, durations);
    }

    public static int getMaxJiraIssuesToDisplay() {
        return CalendarUtil.MAX_JIRA_ISSUES_TO_DISPLAY;
    }

    private String getSubCalendarJql(ApplicationLink applicationLink, T abstractJiraSubCalendar) throws ResponseException, CredentialsRequiredException {
        try {
            if (StringUtils.isNotBlank(((AbstractJiraSubCalendar)abstractJiraSubCalendar).getProjectKey())) {
                return String.format("project = \"%s\"", ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getProjectKey());
            }
            if (0L < ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getSearchFilterId()) {
                return this.jiraAccessor.getSearchFilterJql(applicationLink, ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getSearchFilterId());
            }
            return ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getJql();
        }
        catch (ResponseException searchFilterDetailsQueryError) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Unable to get details of search filter %d", ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getSearchFilterId()), (Throwable)searchFilterDetailsQueryError);
            } else {
                LOG.error(String.format("Unable to get details of search filter %d", ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getSearchFilterId()));
            }
            throw searchFilterDetailsQueryError;
        }
        catch (CredentialsRequiredException authRequired) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Unable to get details of search filter %d", ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getSearchFilterId()), (Throwable)authRequired);
            } else {
                LOG.error(String.format("Unable to get details of search filter %d", ((AbstractJiraSubCalendar)abstractJiraSubCalendar).getSearchFilterId()));
            }
            throw authRequired;
        }
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        return this.hasEditEventPrivilegeInternal(subCalendar);
    }

    private boolean hasEditEventPrivilegeInternal(T subCalendar) {
        Cache applicationLinkEditStatusCache = this.getApplicationLinkEditStatusCache();
        String applicationId = ((AbstractJiraSubCalendar)subCalendar).getApplicationId();
        Optional canEdit = (Optional)applicationLinkEditStatusCache.get((Object)applicationId);
        return canEdit.orElseGet(() -> {
            applicationLinkEditStatusCache.remove((Object)applicationId);
            return false;
        });
    }

    private Cache getApplicationLinkEditStatusCache() {
        CacheSettings cacheSettings = new CacheSettingsBuilder().replicateViaInvalidation().replicateAsynchronously().flushable().build();
        ApplicationLinkEditStatusCacheLoader cacheLoader = new ApplicationLinkEditStatusCacheLoader();
        return this.cacheManager.getCache(APPLICATION_LINK_EDIT_STATUS_CACHE_KEY, (CacheLoader)cacheLoader, cacheSettings);
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        return super.hasViewEventPrivilege(subCalendarId, user) && this.getJiraLink(this.getSubCalendarEntityPropertyValue(this.getSubCalendarEntity(subCalendarId), "applicationId")) != null;
    }

    @Override
    public boolean hasDeletePrivilege(T subCalendar, ConfluenceUser user) {
        return this.hasViewEventPrivilege(((AbstractJiraSubCalendar)subCalendar).getId(), user);
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        return false;
    }

    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, VEvent raw) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return this.getSubCalendarEventTransformerFactory().getJiraTransformer().transform(super.transform(toBeTransformed, raw), currentUser, this.createJiraSubcalendarEventTransformParameters(toBeTransformed, raw));
    }

    protected String getText(String key, Object ... substitutions) {
        return this.getI18NBean().getText(key, Arrays.asList(substitutions));
    }

    @Override
    public Message getTypeSpecificText(T subCalendar, Message originalMessage) {
        if (StringUtils.equals("calendar3.error.loadevents.notpermitted", originalMessage.getKey())) {
            return new GenericMessage("calendar3.jira.error.loadevents.notpermitted", new Serializable[]{((PersistedSubCalendar)subCalendar).getName(), ((AbstractJiraSubCalendar)subCalendar).getApplicationName()});
        }
        return originalMessage;
    }

    @Override
    public VEvent updateEvent(T subCalendar, VEvent newEventDetails) {
        ApplicationLink jiraLink = this.jiraAccessor.getLinkedJiraInstance(((AbstractJiraSubCalendar)subCalendar).getApplicationId());
        Object issueKey = newEventDetails.getProperty("X-JIRA-ISSUE-KEY");
        Object fieldName = newEventDetails.getProperty("X-JIRA-ISSUE-DATE-FIELD");
        Object startIsEditable = newEventDetails.getProperty("X_JIRA_ISSUE_DATE_FIELD_EDITABLE");
        Object endIsEditable = newEventDetails.getProperty("X_JIRA_ISSUE_END_DATE_FIELD_EDITABLE");
        Date startDate = newEventDetails.getStartDate().getDate();
        try {
            if (issueKey != null && fieldName != null) {
                Object endDateFieldName;
                ArrayList<PairType> fields = new ArrayList<PairType>(2);
                if (startIsEditable == null || ((Content)startIsEditable).getValue().equals("true")) {
                    fields.add(new PairType((Serializable)((Object)((Content)((Property)fieldName).getParameter("X-JIRA-ISSUE-DATE-FIELD-KEY")).getValue()), (Serializable)startDate));
                }
                if ((endDateFieldName = newEventDetails.getProperty("X-JIRA-ISSUE-END-DATE-FIELD")) != null && (endIsEditable == null || ((Content)endIsEditable).getValue().equals("true"))) {
                    Date endDate = newEventDetails.getEndDate().getDate();
                    if (!(startDate instanceof DateTime) && !(endDate instanceof DateTime)) {
                        endDate = new Date(endDate.getTime() - 86400000L);
                    }
                    fields.add(new PairType((Serializable)((Object)((Content)((Property)endDateFieldName).getParameter("X-JIRA-ISSUE-END-DATE-FIELD-KEY")).getValue()), (Serializable)endDate));
                }
                HashMap<String, List<PairType>> eventFields = new HashMap<String, List<PairType>>(1);
                eventFields.put(((Content)issueKey).getValue(), fields);
                this.jiraAccessor.updateEventFields(jiraLink, eventFields, ((PersistedSubCalendar)subCalendar).getTimeZoneId());
            }
            if (this.isVEventVersion(newEventDetails)) {
                this.jiraAccessor.updateProjectVersionReleaseDate(jiraLink, this.getVersionId(newEventDetails), startDate);
            }
            return newEventDetails;
        }
        catch (ResponseException responseException) {
            throw new CalendarException((Exception)((Object)responseException), "calendar3.jira.error.event.issue.update.remoteerror", jiraLink.getName());
        }
        catch (CredentialsRequiredException notAuthenticatedError) {
            throw new CalendarException((Exception)((Object)notAuthenticatedError), "calendar3.jira.error.event.issue.update.needauth", jiraLink.getName());
        }
        catch (JSONException invalidResponseError) {
            throw new CalendarException((Exception)((Object)invalidResponseError), "calendar3.jira.error.event.issue.update.invalidresponse", jiraLink.getName());
        }
    }

    @Override
    public void updateJiraReminderEvents(T subCalendar, Calendar subCalendarContent) {
        DefaultJiraReminderSupport<T> jiraReminderSupport = new DefaultJiraReminderSupport<T>(this, this.getActiveObjects(), this.dataStoreCommonPropertyAccessor.getTransactionalExecutorFactory(), () -> AuthenticatedUserThreadLocal.get());
        jiraReminderSupport.updateJiraReminderNewEvents(subCalendar, subCalendarContent);
    }

    @Override
    public SubCalendarEvent transformJiraEvent(SubCalendarEvent toBeTransformed, VEvent raw) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return this.getSubCalendarEventTransformerFactory().getJiraTransformer().transform(toBeTransformed, currentUser, this.createJiraSubcalendarEventTransformParameters(toBeTransformed, raw));
    }

    private SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters createJiraSubcalendarEventTransformParameters(final SubCalendarEvent toBeTransformed, final VEvent raw) {
        return new SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters(){

            @Override
            public ApplicationLink getJiraLink() {
                AbstractJiraSubCalendar persistedSubCalendar = (AbstractJiraSubCalendar)toBeTransformed.getSubCalendar();
                ApplicationLink applicationLink = AbstractJiraSubCalendarDataStore.this.getJiraLink(persistedSubCalendar.getApplicationId());
                if (applicationLink != null) {
                    return applicationLink;
                }
                SettingsManager settingsManager = AbstractJiraSubCalendarDataStore.this.getSettingsManager();
                String contactAdminUrl = settingsManager.getGlobalSettings().getBaseUrl() + "/contactadministrators.action";
                String confgureAppLinks = settingsManager.getGlobalSettings().getBaseUrl() + "/admin/listapplicationlinks.action";
                String error = AbstractJiraSubCalendarDataStore.this.getText("calendar3.error.jira.removelink", StringEscapeUtils.escapeHtml4((String)persistedSubCalendar.getName()), confgureAppLinks, contactAdminUrl);
                throw new CalendarException(error, true);
            }

            @Override
            public VEvent getRawEvent() {
                return raw;
            }

            @Override
            public boolean isReadOnly() {
                return !toBeTransformed.isEditable();
            }
        };
    }

    private String getVersionId(VEvent vEvent) {
        Url url = vEvent.getUrl();
        if (null != url) {
            String[] urlBits = ((Content)url).getValue().split("/");
            return urlBits[urlBits.length - 1];
        }
        return ((Content)vEvent.getProperty("X-JIRA-VERSION-ID")).getValue();
    }

    private boolean isVEventVersion(VEvent vEvent) {
        return vEvent.getProperty("X-JIRA-PROJECT-NAME") != null || vEvent.getProperty("X-JIRA-VERSION-ID") != null;
    }

    public class ApplicationLinkEditStatusCacheLoader
    implements CacheLoader<String, Optional<Boolean>> {
        public Optional<Boolean> load(String applicationId) {
            Boolean canEdit;
            try {
                canEdit = AbstractJiraSubCalendarDataStore.this.jiraAccessor.canEditCalendar(AbstractJiraSubCalendarDataStore.this.getJiraLink(applicationId));
            }
            catch (CredentialsRequiredException cre) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error retrieving API version from JIRA", (Throwable)cre);
                }
                return Optional.empty();
            }
            catch (ResponseException re) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error retrieving API version from JIRA", (Throwable)re);
                }
                if (re.getCause() instanceof CredentialsRequiredException) {
                    return Optional.empty();
                }
                canEdit = false;
            }
            return Optional.of(canEdit);
        }
    }

    private static class JiraSubCalendarSource {
        private static final String SOURCE_LOCATION_PROTOCOL = "jira://";
        private final String applicationId;
        private final String projectKey;
        private final long searchFilterId;
        private final String jql;
        private Set<String> dateFieldNames;
        private Set<Duration> durations;

        private JiraSubCalendarSource(String applicationId, String projectKey, long searchFilterId, String jql, Set<String> dateFieldNames, Set<Duration> durations) {
            this.applicationId = applicationId;
            this.projectKey = projectKey;
            this.searchFilterId = searchFilterId;
            this.jql = jql;
            this.dateFieldNames = dateFieldNames;
            this.durations = durations;
        }

        public String getApplicationId() {
            return this.applicationId;
        }

        public String getProjectKey() {
            return this.projectKey;
        }

        public long getSearchFilterId() {
            return this.searchFilterId;
        }

        public String getJql() {
            return this.jql;
        }

        public Set<String> getDateFieldNames() {
            return this.dateFieldNames;
        }

        public Set<Duration> getDurations() {
            return this.durations;
        }

        private static JiraSubCalendarSource parse(String sourceLocation, CalendarHelper calendarHelper) {
            String trimmedSourceLocation = StringUtils.trim(sourceLocation);
            if (StringUtils.isBlank(trimmedSourceLocation) || !StringUtils.startsWith(trimmedSourceLocation, SOURCE_LOCATION_PROTOCOL) || trimmedSourceLocation.length() == SOURCE_LOCATION_PROTOCOL.length()) {
                return null;
            }
            int indexOfQueryString = trimmedSourceLocation.indexOf(63);
            if (indexOfQueryString < 0) {
                return new JiraSubCalendarSource(trimmedSourceLocation, null, 0L, null, null, null);
            }
            Map<String, Set<String>> queryParams = calendarHelper.parseURLParamJira(trimmedSourceLocation.substring(indexOfQueryString));
            Set<String> durationPairs = queryParams.get("duration");
            HashSet<Duration> durations = null;
            if (null != durationPairs && !durationPairs.isEmpty()) {
                durations = new HashSet<Duration>(Collections2.filter((Collection)Collections2.transform(durationPairs, durationPair -> {
                    String[] durationTokens = StringUtils.split(durationPair, "/", 2);
                    if (durationTokens.length == 2) {
                        return new Duration(durationTokens[0], durationTokens[1]);
                    }
                    return null;
                }), (Predicate)Predicates.notNull()));
            }
            return new JiraSubCalendarSource(GeneralUtil.urlDecode((String)trimmedSourceLocation.substring(SOURCE_LOCATION_PROTOCOL.length(), indexOfQueryString)), GeneralUtil.urlDecode((String)StringUtils.join((Collection)queryParams.get("projectKey"), "")), JiraSubCalendarSource.getStringAsLong(StringUtils.join((Collection)queryParams.get("searchFilterId"), "")), GeneralUtil.urlDecode((String)StringUtils.join((Collection)queryParams.get("jql"), "")), queryParams.get("dateFieldName"), durations);
        }

        private static long getStringAsLong(String longStr) {
            return StringUtils.isNotBlank(longStr) && StringUtils.isNumeric(longStr) ? Long.parseLong(longStr) : 0L;
        }
    }
}

