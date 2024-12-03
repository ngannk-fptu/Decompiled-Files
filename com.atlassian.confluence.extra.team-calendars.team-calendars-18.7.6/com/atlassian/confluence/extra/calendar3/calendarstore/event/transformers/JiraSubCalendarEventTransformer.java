/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.collect.Collections2
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.WebResourceDependentSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.collect.Collections2;
import java.util.HashMap;
import java.util.regex.Pattern;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class JiraSubCalendarEventTransformer
extends WebResourceDependentSubCalendarEventTransformer<SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters> {
    private static final Pattern BASE_URL_REPLACE_FOR_SECURE = Pattern.compile("^http.+/secure/");
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final SubCalendarColorRegistry subCalendarColorRegistry;

    public JiraSubCalendarEventTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, WebResourceUrlProvider webResourceUrlProvider, SubCalendarColorRegistry subCalendarColorRegistry) {
        super(localeManager, i18NBeanFactory, buildInformationManager);
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.subCalendarColorRegistry = subCalendarColorRegistry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, ConfluenceUser forUser, SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters transformParameters) {
        String methodSignature = "JiraSubCalendarEventTransformer.transform(SubCalendarEvent, SubCalendarEventTransformerFactory.TransformParameters)";
        UtilTimerStack.push((String)methodSignature);
        try {
            if (CalendarUtil.isGreenHopperSprint(transformParameters.getRawEvent())) {
                this.transformGreenhopperEvent(toBeTransformed, transformParameters);
            } else {
                this.transformJiraEvent(toBeTransformed, forUser, transformParameters);
            }
            toBeTransformed.getExtraProperties().put("jiraVersionDisplayDayDuration", this.calculateDisplayDayDuration(forUser, toBeTransformed, "version"));
            toBeTransformed.getExtraProperties().put("jiraIssueDisplayDayDuration", this.calculateDisplayDayDuration(forUser, toBeTransformed, "issue"));
            SubCalendarEvent subCalendarEvent = toBeTransformed;
            return subCalendarEvent;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    private void transformGreenhopperEvent(SubCalendarEvent toBeTransformed, SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters transformParameters) {
        VEvent raw = transformParameters.getRawEvent();
        String applinksDisplayUrlForBrowse = this.getJiraDisplayUrl(transformParameters) + "/browse/";
        Object viewBoardsUrlProperty = raw.getProperty("X-GREENHOPPER-SPRINT-VIEWBOARDS-URL");
        if (null != viewBoardsUrlProperty) {
            Object sprintClosedProperty;
            String sprintHomePageUrl;
            PropertyList projectProperties;
            toBeTransformed.setClassName("greenhopper-sprint");
            toBeTransformed.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/greenhopper_sprint_48.png", UrlMode.ABSOLUTE));
            toBeTransformed.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/greenhopper_sprint_24.png", UrlMode.ABSOLUTE));
            HashMap<String, String> extraProperties = new HashMap<String, String>();
            if (null != toBeTransformed.getExtraProperties()) {
                extraProperties.putAll(toBeTransformed.getExtraProperties());
            }
            toBeTransformed.setDescription((projectProperties = raw.getProperties("X-JIRA-PROJECT")).isEmpty() ? null : StringUtils.join(Collections2.transform(projectProperties, Content::getValue), ", "));
            extraProperties.put("viewBoardsUrl", CalendarUtil.rebaseUrl(CalendarUtil.BASE_URL_REPLACE_FOR_BROWSE, ((Content)viewBoardsUrlProperty).getValue(), applinksDisplayUrlForBrowse));
            Object sprintHomePageUrlProperty = raw.getProperty("X-GREENHOPPER-SPRINT-HOMEPAGE-URL");
            if (null != sprintHomePageUrlProperty && StringUtils.isNotBlank(sprintHomePageUrl = ((Content)sprintHomePageUrlProperty).getValue())) {
                extraProperties.put("sprintHomePageUrl", sprintHomePageUrl);
            }
            if ((sprintClosedProperty = raw.getProperty("X-GREENHOPPER-SPRINT-CLOSED")) != null) {
                extraProperties.put("resolved", ((Content)sprintClosedProperty).getValue());
            }
            toBeTransformed.setDisableResizing(true);
            toBeTransformed.setExtraProperties(extraProperties);
            toBeTransformed.setExtraPropertiesTemplate("event-details-sprint");
            toBeTransformed.setEditable(false);
        }
        this.lightenEventColorIfResolved(toBeTransformed, raw, new StringBuilder());
    }

    private String getJiraDisplayUrl(SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters transformParameters) {
        return transformParameters.getJiraLink().getDisplayUrl().toString();
    }

    private void transformJiraEvent(SubCalendarEvent toBeTransformed, ConfluenceUser forUser, SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters transformParameters) {
        VEvent raw = transformParameters.getRawEvent();
        String jiraDisplayUrl = this.getJiraDisplayUrl(transformParameters);
        StringBuilder stringBuilder = new StringBuilder();
        boolean resolved = this.isResolved(raw);
        if (CalendarUtil.isJiraVersion(raw)) {
            toBeTransformed.setClassName("jira-version");
            toBeTransformed.setUrl(CalendarUtil.getVersionUrl(stringBuilder, raw, jiraDisplayUrl));
            if (resolved) {
                toBeTransformed.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/version_closed_48.png", UrlMode.ABSOLUTE));
                toBeTransformed.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/version_closed_24.png", UrlMode.ABSOLUTE));
            } else {
                toBeTransformed.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/version_open_48.png", UrlMode.ABSOLUTE));
                toBeTransformed.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/version_open_24.png", UrlMode.ABSOLUTE));
            }
            toBeTransformed.setName(this.getText(forUser, "calendar3.eventdetails.jira.due", CalendarUtil.getProject(raw).getName(), toBeTransformed.getName()));
            HashMap<String, String> extraProperties = new HashMap<String, String>();
            if (toBeTransformed.getExtraProperties() != null) {
                extraProperties.putAll(toBeTransformed.getExtraProperties());
            }
            extraProperties.put("summaryLink", this.getVersionSummaryUrl(stringBuilder, raw, jiraDisplayUrl));
            extraProperties.put("viewIssuesLink", this.getVersionIssuesUrl(stringBuilder, raw, jiraDisplayUrl));
            extraProperties.put("releaseNotesLink", this.getVersionReleaseNotesUrl(stringBuilder, raw, jiraDisplayUrl));
            extraProperties.put("resolved", String.valueOf(resolved));
            toBeTransformed.setDisableResizing(true);
            toBeTransformed.setExtraProperties(extraProperties);
            toBeTransformed.setExtraPropertiesTemplate("event-details-version");
            toBeTransformed.setEditable(!transformParameters.isReadOnly());
            toBeTransformed.setCalendarReloadRequiredOnUpdate(toBeTransformed.isEditable() && this.isCalendarReloadRequiredOnUpdate(raw));
        } else {
            Object statusTextProperty;
            JiraUser assignee;
            toBeTransformed.setClassName("jira-issue");
            String url = CalendarUtil.getIssueUrl(stringBuilder, raw, jiraDisplayUrl);
            if (StringUtils.isNotEmpty(url)) {
                toBeTransformed.setUrl(url);
            }
            toBeTransformed.setIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/issue_types_48.png", UrlMode.ABSOLUTE));
            toBeTransformed.setMediumIconUrl(this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), "img/issue_types_24.png", UrlMode.ABSOLUTE));
            Object issueKeyProperty = raw.getProperty("X-JIRA-ISSUE-KEY");
            Object issueDateFieldProperty = raw.getProperty("X-JIRA-ISSUE-DATE-FIELD");
            String issueKey = null == issueKeyProperty ? "" : StringUtils.defaultString(((Content)issueKeyProperty).getValue());
            String fieldName = null == issueDateFieldProperty ? "" : StringUtils.defaultString(((Content)issueDateFieldProperty).getValue());
            String originalName = toBeTransformed.getName();
            Object endDateNameProperty = raw.getProperty("X-JIRA-ISSUE-END-DATE-FIELD");
            if (endDateNameProperty == null) {
                toBeTransformed.setDisableResizing(true);
                toBeTransformed.setName(this.getText(forUser, "calendar3.eventdetails.jira.field", issueKey, originalName, fieldName));
            } else {
                toBeTransformed.setName(this.getText(forUser, "calendar3.eventdetails.jira.field.namesupressed", issueKey, originalName));
            }
            HashMap<String, String> extraProperties = new HashMap<String, String>();
            if (toBeTransformed.getExtraProperties() != null) {
                extraProperties.putAll(toBeTransformed.getExtraProperties());
            }
            extraProperties.put("assigneeName", (assignee = this.getAssignee(stringBuilder, raw, jiraDisplayUrl)) == null ? this.getText(forUser, "calendar3.eventdetails.unassigned") : assignee.getDisplayName());
            if (assignee != null) {
                extraProperties.put("assigneeProfileUrl", assignee.getProfileUrl());
            }
            extraProperties.put("statusText", null == (statusTextProperty = raw.getProperty("X-JIRA-STATUS")) ? "" : StringUtils.defaultString(((Content)statusTextProperty).getValue()));
            extraProperties.put("issueLink", toBeTransformed.getUrl());
            extraProperties.put("resolved", String.valueOf(resolved));
            if (null != issueDateFieldProperty) {
                String dateFieldKey = ((Content)((Property)issueDateFieldProperty).getParameter("X-JIRA-ISSUE-DATE-FIELD-KEY")).getValue();
                String dateFieldName = StringUtils.equals("duedate", dateFieldKey) ? this.getText(forUser, "calendar3.jira.fields.duedate.name.2") : ((Content)issueDateFieldProperty).getValue();
                extraProperties.put("dateFieldStartName", dateFieldName);
                Object startDateFieldEditable = raw.getProperty("X_JIRA_ISSUE_DATE_FIELD_EDITABLE");
                Object startDateFieldHasTime = raw.getProperty("X_JIRA_ISSUE_DATE_FIELD_HAS_TIME");
                if (startDateFieldEditable != null) {
                    extraProperties.put("startDateFieldEditable", ((Content)startDateFieldEditable).getValue());
                }
                if (startDateFieldHasTime != null) {
                    extraProperties.put("startDateFieldHasTime", ((Content)startDateFieldHasTime).getValue());
                }
                if (null != endDateNameProperty) {
                    extraProperties.put("dateField", String.format("%s - %s", dateFieldName, ((Content)endDateNameProperty).getValue()));
                    extraProperties.put("dateFieldEndName", ((Content)endDateNameProperty).getValue());
                    Object endDateFieldEditable = raw.getProperty("X_JIRA_ISSUE_END_DATE_FIELD_EDITABLE");
                    Object endDateFieldHasTime = raw.getProperty("X_JIRA_ISSUE_END_DATE_FIELD_HAS_TIME");
                    if (endDateFieldEditable != null) {
                        extraProperties.put("endDateFieldEditable", ((Content)endDateFieldEditable).getValue());
                    }
                    if (endDateFieldHasTime != null) {
                        extraProperties.put("endDateFieldHasTime", ((Content)endDateFieldHasTime).getValue());
                    }
                } else {
                    extraProperties.put("dateField", dateFieldName);
                }
            } else {
                extraProperties.put("dateField", this.getText(forUser, "calendar3.jira.fields.duedate.name.2"));
            }
            toBeTransformed.setExtraProperties(extraProperties);
            toBeTransformed.setExtraPropertiesTemplate("event-details-issue");
            toBeTransformed.setEditable(!transformParameters.isReadOnly());
            toBeTransformed.setCalendarReloadRequiredOnUpdate(toBeTransformed.isEditable() && this.isCalendarReloadRequiredOnUpdate(raw));
        }
        this.lightenEventColorIfResolved(toBeTransformed, raw, stringBuilder);
    }

    private String calculateDisplayDayDuration(ConfluenceUser forUser, SubCalendarEvent toBeTransformed, String popupType) {
        int dayDuration = 0;
        DateTime startTime = toBeTransformed.getStartTime();
        DateTime startTimeZone = new DateTime(toBeTransformed.getStartTime().getZone());
        dayDuration = startTime.getYear() != startTimeZone.getYear() ? (int)((startTime.getMillis() - startTimeZone.getMillis()) / 86400000L) : startTime.getDayOfYear() - startTimeZone.getDayOfYear();
        int displayDayDuration = Math.abs(dayDuration);
        if (dayDuration == 0) {
            return this.getText(forUser, String.format("calendar3.%s.today", popupType));
        }
        if (dayDuration < 0) {
            return this.getText(forUser, String.format("calendar3.%s.dueDays", popupType), displayDayDuration);
        }
        if (dayDuration > 0) {
            return this.getText(forUser, String.format("calendar3.%s.remainingDays", popupType), displayDayDuration);
        }
        return "invalidValue";
    }

    private void lightenEventColorIfResolved(SubCalendarEvent transformed, VEvent raw, StringBuilder stringBuilder) {
        if (this.isResolved(raw)) {
            String subCalendarColour = transformed.getSubCalendar().getColor();
            transformed.setColorScheme(this.subCalendarColorRegistry.getEventMoreLightenedColourScheme(subCalendarColour));
            transformed.setBorderColor(CalendarUtil.buildString(stringBuilder, "#", this.subCalendarColorRegistry.getEvenMoreLightenedColorHex(subCalendarColour)));
            transformed.setBackgroundColor(transformed.getBorderColor());
        }
    }

    private boolean isResolved(VEvent raw) {
        Object statusProperty = raw.getProperty("X-JIRA-RESOLUTION");
        if (statusProperty != null && !((Content)statusProperty).getValue().isEmpty()) {
            return !((Content)statusProperty).getValue().equals("Unresolved");
        }
        Object isReleasedProperty = raw.getProperty("X-JIRA-VERSION-RELEASED");
        if (isReleasedProperty != null) {
            return Boolean.valueOf(((Content)isReleasedProperty).getValue());
        }
        Object sprintClosedProperty = raw.getProperty("X-GREENHOPPER-SPRINT-CLOSED");
        if (sprintClosedProperty != null) {
            return Boolean.valueOf(((Content)sprintClosedProperty).getValue());
        }
        return false;
    }

    private boolean isCalendarReloadRequiredOnUpdate(VEvent vEvent) {
        Object staticUidParameter = vEvent.getUid().getParameter("X-JIRA-UID-STATIC");
        return null == staticUidParameter || !BooleanUtils.toBoolean(((Content)staticUidParameter).getValue());
    }

    private JiraUser getAssignee(StringBuilder stringBuilder, VEvent raw, String jiraDisplayUrl) {
        Object assignee = raw.getProperty("X-JIRA-ASSIGNEE");
        if (null == assignee) {
            return null;
        }
        ParameterList assigneeParams = ((Property)assignee).getParameters();
        String assigneeId = null != assigneeParams && !assigneeParams.isEmpty() ? ((Content)assigneeParams.getParameter("X-JIRA-ASSIGNEE-ID")).getValue() : ((Content)raw.getProperty("X-JIRA-ASSIGNEE-ID")).getValue();
        return new JiraUser(assigneeId, ((Content)assignee).getValue(), CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/secure/ViewProfile.jspa?name=", GeneralUtil.urlEncode((String)StringUtils.defaultString(assigneeId))));
    }

    private String getVersionReleaseNotesUrl(StringBuilder stringBuilder, VEvent raw, String jiraDisplayUrl) {
        Property versionId = this.getVersionIdProperty(raw);
        if (versionId == null) {
            Object releaseNotesUrl = raw.getProperty("X-JIRA-VERSION-RELEASE-NOTES-URL");
            if (releaseNotesUrl == null) {
                return "#";
            }
            return CalendarUtil.rebaseUrl(BASE_URL_REPLACE_FOR_SECURE, StringUtils.defaultString(((Content)releaseNotesUrl).getValue(), "#"), CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/secure/"));
        }
        return CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/secure/ReleaseNote.jspa?projectId=", CalendarUtil.getProject(raw).getProjectId(), "&version=", versionId.getValue());
    }

    private String getVersionIssuesUrl(StringBuilder stringBuilder, VEvent raw, String jiraDisplayUrl) {
        Property versionId = this.getVersionIdProperty(raw);
        if (versionId == null) {
            Object versionIssuesUrl = raw.getProperty("X-JIRA-ISSUES-URL");
            if (versionIssuesUrl == null) {
                return "#";
            }
            return CalendarUtil.rebaseUrl(CalendarUtil.BASE_URL_REPLACE_FOR_BROWSE, StringUtils.defaultString(((Content)versionIssuesUrl).getValue(), "#"), CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/"));
        }
        return CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/", CalendarUtil.getProject(raw).getKey(), "/fixforversion/", versionId.getValue(), "#selectedTab=com.atlassian.jira.plugin.system.project%3Aversion-issues-panel");
    }

    private String getVersionSummaryUrl(StringBuilder stringBuilder, VEvent raw, String jiraDisplayUrl) {
        Property versionId = this.getVersionIdProperty(raw);
        if (versionId == null) {
            Object versionSummaryUrl = raw.getProperty("X-JIRA-VERSION-SUMMARY-URL");
            if (versionSummaryUrl == null) {
                return "#";
            }
            return CalendarUtil.rebaseUrl(CalendarUtil.BASE_URL_REPLACE_FOR_BROWSE, StringUtils.defaultString(((Content)versionSummaryUrl).getValue(), "#"), CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/"));
        }
        return CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/", CalendarUtil.getProject(raw).getKey(), "/fixforversion/", versionId.getValue(), "#selectedTab=com.atlassian.jira.plugin.system.project%3Aversion-summary-panel");
    }

    private Property getVersionIdProperty(VEvent raw) {
        return raw.getProperty("X-JIRA-VERSION-ID");
    }

    private static class JiraUser {
        private final String name;
        private final String displayName;
        private final String profileUrl;

        private JiraUser(String name, String displayName, String profileUrl) {
            this.name = name;
            this.displayName = displayName;
            this.profileUrl = profileUrl;
        }

        private String getName() {
            return this.name;
        }

        private String getDisplayName() {
            return this.displayName;
        }

        private String getProfileUrl() {
            return this.profileUrl;
        }
    }
}

