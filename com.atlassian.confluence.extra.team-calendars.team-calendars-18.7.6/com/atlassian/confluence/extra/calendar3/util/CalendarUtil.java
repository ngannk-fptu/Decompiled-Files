/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.util.GeneralUtil;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Url;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;

public class CalendarUtil {
    private static final String JIRA_ISSUE_EVENT_TYPE = "jira-calendar";
    private static final String JIRA_PROJECT_EVENT_TYPE = "jira-project-releases-calendar";
    private static final String JIRA_AGILE_SPRINT_EVENT_TYPE = "jira-agile-sprint-calendar";
    private static final Pattern VERSION_EVENT_PATTERN = Pattern.compile("^.*fixforversion/\\d+$");
    public static final Pattern BASE_URL_REPLACE_FOR_BROWSE = Pattern.compile("^http.+/browse/");
    public static final int MAX_JIRA_ISSUES_TO_DISPLAY = Integer.getInteger("com.atlassian.confluence.extra.calendar3.jira.issues.max", 1000);

    public static String getImageExtensionFromMineType(String mineType) {
        HashMap<String, String> mineTypeToExtensionMap = new HashMap<String, String>();
        mineTypeToExtensionMap.put("image/jpeg", "jpg");
        mineTypeToExtensionMap.put("image/png", "png");
        mineTypeToExtensionMap.put("image/gif", "gif");
        return mineTypeToExtensionMap.getOrDefault(mineType, "png");
    }

    public static String limitByWord(String inputText, int numberOfWord) {
        if (StringUtils.isEmpty(inputText)) {
            return inputText;
        }
        List<String> words = Arrays.asList(inputText.split(" "));
        return words.stream().limit(numberOfWord).collect(Collectors.joining(" "));
    }

    public static boolean isJiraSubCalendarType(String subCalendarType) {
        String[] jiraTypes = new String[]{"jira", "jira-project-releases", "jira-agile-sprint"};
        return Arrays.asList(jiraTypes).contains(subCalendarType);
    }

    public static String convertSubCalendarTypeToJiraEventType(String subCalendarType) {
        if (subCalendarType.equals("jira")) {
            return JIRA_ISSUE_EVENT_TYPE;
        }
        if (subCalendarType.equals("jira-project-releases")) {
            return JIRA_PROJECT_EVENT_TYPE;
        }
        if (subCalendarType.equals("jira-agile-sprint")) {
            return JIRA_AGILE_SPRINT_EVENT_TYPE;
        }
        return subCalendarType;
    }

    public static String getEventTypeFromStoreKey(String storeKey) {
        String eventType = "";
        if (StringUtils.isEmpty(storeKey)) {
            return eventType;
        }
        if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore")) {
            eventType = "other";
        } else if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore")) {
            eventType = "birthdays";
        } else if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.LeaveSubCalendarDataStore")) {
            eventType = "leaves";
        } else if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.TravelSubCalendarDataStore")) {
            eventType = "travel";
        } else if (storeKey.equals("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE")) {
            eventType = JIRA_ISSUE_EVENT_TYPE;
        } else if (storeKey.equals("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE")) {
            eventType = JIRA_PROJECT_EVENT_TYPE;
        } else if (storeKey.equals("AGILE_SPRINTS_SUB_CALENDAR_STORE")) {
            eventType = JIRA_AGILE_SPRINT_EVENT_TYPE;
        }
        return eventType;
    }

    public static boolean isJiraEventType(String eventType) {
        return eventType.equals(JIRA_ISSUE_EVENT_TYPE) || eventType.equals(JIRA_AGILE_SPRINT_EVENT_TYPE) || eventType.equals(JIRA_PROJECT_EVENT_TYPE);
    }

    public static boolean isJiraStoreKey(String storeKey) {
        return storeKey.equals("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE") || storeKey.equals("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE") || storeKey.equals("AGILE_SPRINTS_SUB_CALENDAR_STORE");
    }

    public static String getI18nKeyForViewJiraIssueTextFromEventStoreKey(String storeKey) {
        if ("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE".equals(storeKey)) {
            return "calendar3.reminder.view.jira.issue.link.text";
        }
        if ("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE".equals(storeKey)) {
            return "calendar3.reminder.view.jira.project.link.text";
        }
        if ("AGILE_SPRINTS_SUB_CALENDAR_STORE".equals(storeKey)) {
            return "calendar3.reminder.view.jira.sprint.link.text";
        }
        return "";
    }

    public static String getStoreKeyFromEventType(String eventType) {
        String storeKey = "";
        if (eventType.equals("other")) {
            storeKey = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore";
        } else if (eventType.equals("birthdays")) {
            storeKey = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore";
        } else if (eventType.equals("leaves")) {
            storeKey = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.LeaveSubCalendarDataStore";
        } else if (eventType.equals("travel")) {
            storeKey = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.TravelSubCalendarDataStore";
        } else if (eventType.equals(JIRA_ISSUE_EVENT_TYPE)) {
            storeKey = "JIRA_ISSUE_DATES_SUB_CALENDAR_STORE";
        } else if (eventType.equals(JIRA_PROJECT_EVENT_TYPE)) {
            storeKey = "JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE";
        } else if (eventType.equals(JIRA_AGILE_SPRINT_EVENT_TYPE)) {
            storeKey = "AGILE_SPRINTS_SUB_CALENDAR_STORE";
        }
        return storeKey;
    }

    public static String getEventTypePropertyFromStoreKey(String storeKey) {
        String eventTypeProperty = "";
        if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore")) {
            eventTypeProperty = "calendar3.event.type.other";
        } else if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore")) {
            eventTypeProperty = "calendar3.event.type.birthdays";
        } else if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.LeaveSubCalendarDataStore")) {
            eventTypeProperty = "calendar3.event.type.leaves";
        } else if (storeKey.equals("com.atlassian.confluence.extra.calendar3.calendarstore.generic.TravelSubCalendarDataStore")) {
            eventTypeProperty = "calendar3.event.type.travel";
        } else if (storeKey.equals("JIRA_ISSUE_DATES_SUB_CALENDAR_STORE")) {
            eventTypeProperty = "calendar3.subcalendar.type.jira-issue-dates";
        } else if (storeKey.equals("JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE")) {
            eventTypeProperty = "calendar3.subcalendar.type.jira-project-releases";
        } else if (storeKey.equals("AGILE_SPRINTS_SUB_CALENDAR_STORE")) {
            eventTypeProperty = "calendar3.subcalendar.type.jira-agile-sprint";
        }
        return eventTypeProperty;
    }

    public static Date getUtcTime(DateProperty dateProperty) {
        Date returnVal = null;
        TimeZone originalTimezone = dateProperty.getTimeZone();
        try {
            dateProperty.setUtc(true);
            returnVal = dateProperty.getDate();
        }
        finally {
            dateProperty.setTimeZone(originalTimezone);
        }
        return returnVal;
    }

    public static Date convertToUtcTime(DateProperty dateProperty) {
        dateProperty.setUtc(true);
        return dateProperty.getDate();
    }

    public static DateTime getUtcDateTimeWithAllDay(Date dateTime) {
        DateTime startDateTime = new DateTime((Object)dateTime);
        return startDateTime.withTime(0, 0, 0, 0).withZone(DateTimeZone.UTC);
    }

    public static String getIcalFormatDateTime(String dt) {
        if (dt == null) {
            return null;
        }
        if (dt.charAt(4) != '-') {
            return dt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(dt.substring(0, 4));
        sb.append(dt.substring(5, 7));
        sb.append(dt.substring(8, 10));
        if (dt.length() > 10) {
            sb.append("T");
            sb.append(dt.substring(11, 13));
            sb.append(dt.substring(14, 16));
            sb.append(dt.substring(17, 19));
            if (dt.endsWith("Z")) {
                sb.append("Z");
            }
        }
        return sb.toString();
    }

    public static net.fortuna.ical4j.model.DateTime toIcal4jDateTime(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, DateTime jodaDate) {
        net.fortuna.ical4j.model.DateTime dateTime = new net.fortuna.ical4j.model.DateTime(jodaDate.getMillis());
        dateTime.setTimeZone(jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(jodaDate.getZone().getID()));
        return dateTime;
    }

    public static Date toIcal4jDate(DateTime jodaDate) {
        try {
            return new Date(DateTimeFormat.forPattern((String)"yyyyMMdd").withZone(jodaDate.getZone()).print((ReadableInstant)jodaDate));
        }
        catch (ParseException pe) {
            return null;
        }
    }

    public static String buildString(StringBuilder buffer, Object ... args) {
        buffer.setLength(0);
        for (Object str : args) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    public static String getVersionUrl(StringBuilder stringBuilder, VEvent raw, String jiraDisplayUrl) {
        Object versionId = raw.getProperty("X-JIRA-VERSION-ID");
        if (versionId == null) {
            Url urlProperty = raw.getUrl();
            return urlProperty == null ? null : ((Content)urlProperty).getValue();
        }
        return CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/", CalendarUtil.getProject(raw).getKey(), "/fixforversion/", ((Content)versionId).getValue());
    }

    public static Project getProject(VEvent raw) {
        Object projectProperty = raw.getProperty("X-JIRA-PROJECT");
        if (projectProperty == null) {
            return new Project(((Content)raw.getProperty("X-JIRA-PROJECT-NAME")).getValue(), null, null);
        }
        return new Project(((Content)projectProperty).getValue(), ((Content)((Property)projectProperty).getParameter("X-JIRA-PROJECT-KEY")).getValue(), ((Content)((Property)projectProperty).getParameter("X-JIRA-PROJECT-ID")).getValue());
    }

    public static boolean isJiraVersion(VEvent raw) {
        Url url = raw.getUrl();
        return url == null ? raw.getProperty("X-JIRA-VERSION-ID") != null : VERSION_EVENT_PATTERN.matcher(((Content)url).getValue()).matches();
    }

    public static boolean isGreenHopperSprint(VEvent raw) {
        return raw.getProperty("X-GREENHOPPER-SPRINT-CLOSED") != null;
    }

    public static String getIssueUrl(StringBuilder stringBuilder, VEvent raw, String jiraDisplayUrl) {
        Url url = raw.getUrl();
        if (url != null) {
            return CalendarUtil.rebaseUrl(BASE_URL_REPLACE_FOR_BROWSE, ((Content)url).getValue(), CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/"));
        }
        if (raw.getProperty("X-JIRA-ISSUE-KEY") != null) {
            return CalendarUtil.buildString(stringBuilder, jiraDisplayUrl, "/browse/", ((Content)raw.getProperty("X-JIRA-ISSUE-KEY")).getValue());
        }
        return null;
    }

    public static String rebaseUrl(Pattern aPattern, String url, String newBaseUrl) {
        Matcher urlMatcher = aPattern.matcher(url);
        return urlMatcher.lookingAt() ? urlMatcher.replaceFirst(newBaseUrl) : url;
    }

    public static String getProjectNameFromJiraRawEvent(VEvent rawEvent) {
        Object projectProperty = rawEvent.getProperty("X-JIRA-PROJECT");
        if (projectProperty == null) {
            Object property = rawEvent.getProperty("X-JIRA-PROJECT-NAME");
            return property != null ? ((Content)property).getValue() : "";
        }
        return ((Content)projectProperty).getValue();
    }

    public static boolean isNewDashBoard() {
        block9: {
            String version = StringUtils.split(GeneralUtil.getVersionNumber(), "-")[0];
            String[] splitedVersionString = StringUtils.split(version, ".");
            ArrayList<Integer> splitedVersionInt = new ArrayList<Integer>(splitedVersionString.length);
            for (String aSplitedVersionString : splitedVersionString) {
                splitedVersionInt.add(Integer.parseInt(aSplitedVersionString));
            }
            if ((Integer)splitedVersionInt.get(0) == 5) {
                if ((Integer)splitedVersionInt.get(1) == 9) {
                    try {
                        if ((Integer)splitedVersionInt.get(2) >= 1) {
                            return true;
                        }
                        break block9;
                    }
                    catch (IndexOutOfBoundsException e) {
                        return false;
                    }
                }
                if ((Integer)splitedVersionInt.get(1) > 9) {
                    return true;
                }
            } else if ((Integer)splitedVersionInt.get(0) > 5) {
                return true;
            }
        }
        return false;
    }

    public static class Project
    extends com.atlassian.confluence.extra.calendar3.model.Project {
        private final String projectId;

        private Project(String name, String key, String projectId) {
            super(name, key);
            this.projectId = projectId;
        }

        public String getProjectId() {
            return this.projectId;
        }
    }
}

