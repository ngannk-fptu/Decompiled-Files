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
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="jiraIssueDatesSubCalendarDataStore")
public class JiraIssueDatesSubCalendarDataStore
extends AbstractChildJiraSubCalendarDataStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssueDatesSubCalendarDataStore.class);
    public static final String SUB_CALENDAR_TYPE = "jira";
    public static final String STORE_KEY = "JIRA_ISSUE_DATES_SUB_CALENDAR_STORE";

    @Autowired
    public JiraIssueDatesSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @ComponentImport CacheManager cacheManager, JiraAccessor jiraAccessor, ParentSubCalendarHelper parentSubCalendarHelper, CalendarHelper calendarHelper) {
        super(dataStoreCommonPropertyAccessor, cacheManager, jiraAccessor, parentSubCalendarHelper, calendarHelper);
    }

    public AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar createSubCalendarFromInternal(ObjectMapper mapper, String json) {
        return JiraIssueDatesSubCalendar.fromJSON(mapper, json);
    }

    @Override
    public PersistedSubCalendar save(SubCalendar subCalendar) {
        if (StringUtils.isBlank(subCalendar.getColor())) {
            subCalendar.setColor(this.getDefaultSubCalendarColour());
        }
        return super.save(subCalendar);
    }

    @Override
    protected String getDefaultSubCalendarColour() {
        return "subcalendar-orange";
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
    protected JiraIssueDatesSubCalendar createNewJiraSubCalendar() {
        return new JiraIssueDatesSubCalendar();
    }

    public static class JiraIssueDatesSubCalendar
    extends AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar
    implements Cloneable {
        @Override
        @XmlElement
        public String getType() {
            return JiraIssueDatesSubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        public static JiraIssueDatesSubCalendar fromJSON(ObjectMapper mapper, String json) {
            try {
                return (JiraIssueDatesSubCalendar)mapper.readValue(json, JiraIssueDatesSubCalendar.class);
            }
            catch (IOException e) {
                LOGGER.error("Could not json string to JiraIssueDatesSubCalendar object", (Throwable)e);
                return null;
            }
        }

        @Override
        public String getStoreKey() {
            return JiraIssueDatesSubCalendarDataStore.STORE_KEY;
        }

        @Override
        public Object clone() {
            JiraIssueDatesSubCalendar JiraIssueDatesSubCalendar2 = new JiraIssueDatesSubCalendar();
            JiraIssueDatesSubCalendar2.setParent(this.getParent());
            JiraIssueDatesSubCalendar2.setId(this.getId());
            JiraIssueDatesSubCalendar2.setName(this.getName());
            JiraIssueDatesSubCalendar2.setDescription(this.getDescription());
            JiraIssueDatesSubCalendar2.setColor(this.getColor());
            JiraIssueDatesSubCalendar2.setCreator(this.getCreator());
            JiraIssueDatesSubCalendar2.setTimeZoneId(this.getTimeZoneId());
            JiraIssueDatesSubCalendar2.setSourceLocation(this.getSourceLocation());
            JiraIssueDatesSubCalendar2.setApplicationName(this.getApplicationName());
            JiraIssueDatesSubCalendar2.setApplicationId(this.getApplicationId());
            JiraIssueDatesSubCalendar2.setProjectKey(this.getProjectKey());
            JiraIssueDatesSubCalendar2.setProjectName(this.getProjectName());
            JiraIssueDatesSubCalendar2.setSearchFilterId(this.getSearchFilterId());
            JiraIssueDatesSubCalendar2.setSearchFilterName(this.getSearchFilterName());
            JiraIssueDatesSubCalendar2.setJql(this.getJql());
            JiraIssueDatesSubCalendar2.setDateFieldNames(this.getDateFieldNames());
            JiraIssueDatesSubCalendar2.setDurations(this.getDurations());
            return JiraIssueDatesSubCalendar2;
        }
    }
}

