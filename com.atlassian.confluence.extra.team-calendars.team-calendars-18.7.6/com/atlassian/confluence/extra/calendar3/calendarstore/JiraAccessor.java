/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.core.util.PairType
 *  com.atlassian.sal.api.net.ResponseException
 *  org.json.JSONException
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.confluence.extra.calendar3.model.JiraDateField;
import com.atlassian.confluence.extra.calendar3.model.JqlAutoCompleteResult;
import com.atlassian.confluence.extra.calendar3.model.JqlValidationResult;
import com.atlassian.confluence.extra.calendar3.model.Project;
import com.atlassian.confluence.extra.calendar3.model.QueryOptions;
import com.atlassian.core.util.PairType;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import org.json.JSONException;

public interface JiraAccessor {
    public static final String X_EVENT_ID = "X-JIRA-UID-STATIC";
    public static final String X_SPRINT_VIEWBOARDS_URL = "X-GREENHOPPER-SPRINT-VIEWBOARDS-URL";
    public static final String X_SPRINT_HOME_URL = "X-GREENHOPPER-SPRINT-HOMEPAGE-URL";
    public static final String X_PROJECT = "X-JIRA-PROJECT";
    public static final String X_PROJECT_NAME = "X-JIRA-PROJECT-NAME";
    public static final String X_PROJECT_KEY = "X-JIRA-PROJECT-KEY";
    public static final String X_PROJECT_ID = "X-JIRA-PROJECT-ID";
    public static final String X_VERSION_ID = "X-JIRA-VERSION-ID";
    public static final String X_VERSION_RELEASED = "X-JIRA-VERSION-RELEASED";
    public static final String X_VERSION_SUMMARY_URL = "X-JIRA-VERSION-SUMMARY-URL";
    public static final String X_VERSION_ISSUES_URL = "X-JIRA-ISSUES-URL";
    public static final String X_VERSION_NOTES_URL = "X-JIRA-VERSION-RELEASE-NOTES-URL";
    public static final String X_ISSUE_KEY = "X-JIRA-ISSUE-KEY";
    public static final String X_ISSUE_STATUS = "X-JIRA-STATUS";
    public static final String X_ISSUE_RESOLUTION = "X-JIRA-RESOLUTION";
    public static final String X_DATE_FIELD = "X-JIRA-ISSUE-DATE-FIELD";
    public static final String X_DATE_FIELD_ID_PARAM = "X-JIRA-ISSUE-DATE-FIELD-KEY";
    public static final String X_END_DATE_FIELD = "X-JIRA-ISSUE-END-DATE-FIELD";
    public static final String X_END_DATE_FIELD_ID_PARAM = "X-JIRA-ISSUE-END-DATE-FIELD-KEY";
    public static final String X_PROP_ISSUE_DATE_FIELD_HAS_TIME = "X_JIRA_ISSUE_DATE_FIELD_HAS_TIME";
    public static final String X_PROP_ISSUE_DATE_FIELD_EDITABLE = "X_JIRA_ISSUE_DATE_FIELD_EDITABLE";
    public static final String X_PROP_ISSUE_END_DATE_FIELD_EDITABLE = "X_JIRA_ISSUE_END_DATE_FIELD_EDITABLE";
    public static final String X_PROP_ISSUE_END_DATE_FIELD_HAS_TIME = "X_JIRA_ISSUE_END_DATE_FIELD_HAS_TIME";
    public static final String X_ASSIGNEE = "X-JIRA-ASSIGNEE";
    public static final String X_ASSIGNEE_ID = "X-JIRA-ASSIGNEE-ID";
    public static final String X_CONFLUENCE_SUBCALENDAR_TYPE = "X-CONFLUENCE-SUBCALENDAR-TYPE";
    public static final String DATE_FIELD_VERSION_DUE = "versiondue";
    public static final String DATE_FIELD_ISSUE_DUE = "duedate";
    public static final String DATE_FIELD_RESOLUTION = "resolution";
    public static final String DATE_FIELD_SPRINT = "sprint";

    public Collection<ApplicationLink> getLinkedJiraApplications();

    public ApplicationLink getLinkedJiraInstance(String var1);

    public QueryOptions getQueryOptions(ApplicationLink var1) throws CredentialsRequiredException, ResponseException;

    public Collection<Project> getProjects(ApplicationLink var1) throws CredentialsRequiredException, ResponseException;

    public Calendar getCalendarByJql(ApplicationLink var1, String var2, CalendarOptions var3, long var4, long var6) throws CredentialsRequiredException, ResponseException, IOException, ParserException;

    public Calendar getCalendarBySearchFilter(ApplicationLink var1, long var2, CalendarOptions var4, long var5, long var7) throws CredentialsRequiredException, ResponseException, IOException, ParserException;

    public Collection<JiraDateField> getDateFields(ApplicationLink var1, String var2) throws CredentialsRequiredException, ResponseException, IOException;

    public Collection<JiraDateField> getDateFields(ApplicationLink var1, long var2) throws CredentialsRequiredException, ResponseException, IOException;

    public JqlValidationResult validateJql(ApplicationLink var1, String var2) throws CredentialsRequiredException, ResponseException, IOException;

    public JqlAutoCompleteResult getAutoComplete(ApplicationLink var1, String var2, String var3) throws CredentialsRequiredException, ResponseException, IOException;

    public boolean isGreenHopperSprintDatesSupported(ApplicationLink var1);

    public void updateProjectVersionReleaseDate(ApplicationLink var1, String var2, Date var3) throws CredentialsRequiredException, ResponseException, JSONException;

    @Deprecated
    public void updateEventFields(ApplicationLink var1, Map<String, List<PairType>> var2) throws ResponseException, CredentialsRequiredException, JSONException;

    public void updateEventFields(ApplicationLink var1, Map<String, List<PairType>> var2, String var3) throws ResponseException, CredentialsRequiredException, JSONException;

    public boolean canEditCalendar(ApplicationLink var1) throws ResponseException, CredentialsRequiredException;

    public String getSearchFilterJql(ApplicationLink var1, long var2) throws ResponseException, CredentialsRequiredException;

    public int getIssuesReturnedByJql(ApplicationLink var1, String var2, Collection<String> var3, Collection<String> var4) throws ResponseException, CredentialsRequiredException;

    public static class JiraJQLWrongResponseException
    extends ResponseException {
        private final String message;

        public JiraJQLWrongResponseException(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static class JiraResponseException
    extends ResponseException {
        private final String message;

        public JiraResponseException(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static class JiraPreConditionUnmetException
    extends ResponseException {
        private final ApplicationLink applicationLink;
        private final PreCondition preCondition;
        private final String endpointString;

        public JiraPreConditionUnmetException(ApplicationLink applicationLink, PreCondition preCondition) {
            this(applicationLink, preCondition, "");
        }

        public JiraPreConditionUnmetException(ApplicationLink applicationLink, PreCondition preCondition, String endpointString) {
            this.applicationLink = Objects.requireNonNull(applicationLink, "The applicationLink param cannot be null");
            this.preCondition = Objects.requireNonNull(preCondition, "The precondition param cannot be null");
            this.endpointString = Optional.ofNullable(endpointString).orElse("");
        }

        public ApplicationLink getJiraLink() {
            return this.applicationLink;
        }

        public PreCondition getPreCondition() {
            return this.preCondition;
        }

        public String getMessage() {
            return String.format("%s: Unable to connect to Jira endpoint at %s%s", this.preCondition.name(), this.applicationLink.getDisplayUrl(), this.endpointString);
        }

        public static enum PreCondition {
            EndpointNotFound,
            InvalidSearchFilter;

        }
    }

    public static class CalendarOptions {
        private final Set<PairType> durations;
        private final Set<String> dateFieldNames;

        public CalendarOptions(Set<PairType> durations, Set<String> dateFieldNames) {
            this.durations = durations;
            this.dateFieldNames = dateFieldNames;
        }

        public Set<PairType> getDurations() {
            return this.durations;
        }

        public Set<String> getDateFieldNames() {
            return this.dateFieldNames;
        }

        public boolean isIncludeFixVersions() {
            Set<String> dateFieldNames = this.getDateFieldNames();
            return null != dateFieldNames && dateFieldNames.contains(JiraAccessor.DATE_FIELD_VERSION_DUE);
        }

        public boolean isIncludeGreenHopperSprints() {
            return null != this.getDateFieldNames() && this.getDateFieldNames().contains(JiraAccessor.DATE_FIELD_SPRINT);
        }
    }
}

