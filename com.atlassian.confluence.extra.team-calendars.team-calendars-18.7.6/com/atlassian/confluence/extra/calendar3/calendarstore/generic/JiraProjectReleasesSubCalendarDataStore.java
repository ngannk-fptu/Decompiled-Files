/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.xml.bind.annotation.XmlElement
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.AbstractChildJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarHelper;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import javax.xml.bind.annotation.XmlElement;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="jiraProjectReleasesSubCalendarDataStore")
public class JiraProjectReleasesSubCalendarDataStore
extends AbstractChildJiraSubCalendarDataStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraProjectReleasesSubCalendarDataStore.class);
    public static final String SUB_CALENDAR_TYPE = "jira-project-releases";
    public static final String STORE_KEY = "JIRA_PROJECT_RELEASES_SUB_CALENDAR_STORE";

    @Autowired
    public JiraProjectReleasesSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @ComponentImport CacheManager cacheManager, JiraAccessor jiraAccessor, ParentSubCalendarHelper parentSubCalendarHelper, CalendarHelper calendarHelper) {
        super(dataStoreCommonPropertyAccessor, cacheManager, jiraAccessor, parentSubCalendarHelper, calendarHelper);
    }

    public AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar createSubCalendarFromInternal(ObjectMapper mapper, String json) {
        return JiraProjectReleasesSubCalendar.fromJSON(mapper, json);
    }

    @Override
    protected String getDefaultSubCalendarColour() {
        return "subcalendar-red";
    }

    @Override
    protected String getStoreKey() {
        return STORE_KEY;
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    @Override
    protected JiraProjectReleasesSubCalendar createNewJiraSubCalendar() {
        return new JiraProjectReleasesSubCalendar();
    }

    public static class JiraProjectReleasesSubCalendar
    extends AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar
    implements Cloneable {
        @Override
        @XmlElement
        public String getType() {
            return JiraProjectReleasesSubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        public String getStoreKey() {
            return JiraProjectReleasesSubCalendarDataStore.STORE_KEY;
        }

        public static JiraProjectReleasesSubCalendar fromJSON(ObjectMapper mapper, String json) {
            try {
                return (JiraProjectReleasesSubCalendar)mapper.readValue(json, JiraProjectReleasesSubCalendar.class);
            }
            catch (IOException e) {
                LOGGER.error("Could not json string to JiraProjectReleasesSubCalendar object", (Throwable)e);
                return null;
            }
        }

        @Override
        public Object clone() {
            JiraProjectReleasesSubCalendar JiraProjectReleasesSubCalendar2 = new JiraProjectReleasesSubCalendar();
            JiraProjectReleasesSubCalendar2.setParent(this.getParent());
            JiraProjectReleasesSubCalendar2.setId(this.getId());
            JiraProjectReleasesSubCalendar2.setName(this.getName());
            JiraProjectReleasesSubCalendar2.setDescription(this.getDescription());
            JiraProjectReleasesSubCalendar2.setColor(this.getColor());
            JiraProjectReleasesSubCalendar2.setCreator(this.getCreator());
            JiraProjectReleasesSubCalendar2.setTimeZoneId(this.getTimeZoneId());
            JiraProjectReleasesSubCalendar2.setSourceLocation(this.getSourceLocation());
            JiraProjectReleasesSubCalendar2.setApplicationName(this.getApplicationName());
            JiraProjectReleasesSubCalendar2.setApplicationId(this.getApplicationId());
            JiraProjectReleasesSubCalendar2.setProjectKey(this.getProjectKey());
            JiraProjectReleasesSubCalendar2.setProjectName(this.getProjectName());
            JiraProjectReleasesSubCalendar2.setSearchFilterId(this.getSearchFilterId());
            JiraProjectReleasesSubCalendar2.setSearchFilterName(this.getSearchFilterName());
            JiraProjectReleasesSubCalendar2.setJql(this.getJql());
            JiraProjectReleasesSubCalendar2.setDateFieldNames(this.getDateFieldNames());
            JiraProjectReleasesSubCalendar2.setDurations(this.getDurations());
            return JiraProjectReleasesSubCalendar2;
        }
    }
}

