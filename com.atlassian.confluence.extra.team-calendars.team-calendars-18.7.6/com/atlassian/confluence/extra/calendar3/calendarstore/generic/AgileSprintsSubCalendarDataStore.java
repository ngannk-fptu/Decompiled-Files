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

@Component(value="agileSprintsSubCalendarDataStore")
public class AgileSprintsSubCalendarDataStore
extends AbstractChildJiraSubCalendarDataStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgileSprintsSubCalendarDataStore.class);
    public static final String SUB_CALENDAR_TYPE = "jira-agile-sprint";
    public static final String STORE_KEY = "AGILE_SPRINTS_SUB_CALENDAR_STORE";

    @Autowired
    public AgileSprintsSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @ComponentImport CacheManager cacheManager, JiraAccessor jiraAccessor, ParentSubCalendarHelper parentSubCalendarHelper, CalendarHelper calendarHelper) {
        super(dataStoreCommonPropertyAccessor, cacheManager, jiraAccessor, parentSubCalendarHelper, calendarHelper);
    }

    public AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar createSubCalendarFromInternal(ObjectMapper mapper, String json) {
        return AgileSprintsSubCalendar.fromJSON(mapper, json);
    }

    @Override
    protected String getDefaultSubCalendarColour() {
        return "subcalendar-green";
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
    protected AgileSprintsSubCalendar createNewJiraSubCalendar() {
        return new AgileSprintsSubCalendar();
    }

    public static class AgileSprintsSubCalendar
    extends AbstractChildJiraSubCalendarDataStore.ChildJiraSubCalendar
    implements Cloneable {
        @Override
        @XmlElement
        public String getType() {
            return AgileSprintsSubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        public String getStoreKey() {
            return AgileSprintsSubCalendarDataStore.STORE_KEY;
        }

        public static AgileSprintsSubCalendar fromJSON(ObjectMapper mapper, String json) {
            try {
                return (AgileSprintsSubCalendar)mapper.readValue(json, AgileSprintsSubCalendar.class);
            }
            catch (IOException e) {
                LOGGER.error("Could not json string to AgileSprintsSubCalendar object", (Throwable)e);
                return null;
            }
        }

        @Override
        public Object clone() {
            AgileSprintsSubCalendar agileSprintSubCalendar = new AgileSprintsSubCalendar();
            agileSprintSubCalendar.setParent(this.getParent());
            agileSprintSubCalendar.setId(this.getId());
            agileSprintSubCalendar.setName(this.getName());
            agileSprintSubCalendar.setDescription(this.getDescription());
            agileSprintSubCalendar.setColor(this.getColor());
            agileSprintSubCalendar.setCreator(this.getCreator());
            agileSprintSubCalendar.setTimeZoneId(this.getTimeZoneId());
            agileSprintSubCalendar.setSourceLocation(this.getSourceLocation());
            agileSprintSubCalendar.setApplicationName(this.getApplicationName());
            agileSprintSubCalendar.setApplicationId(this.getApplicationId());
            agileSprintSubCalendar.setProjectKey(this.getProjectKey());
            agileSprintSubCalendar.setProjectName(this.getProjectName());
            agileSprintSubCalendar.setSearchFilterId(this.getSearchFilterId());
            agileSprintSubCalendar.setSearchFilterName(this.getSearchFilterName());
            agileSprintSubCalendar.setJql(this.getJql());
            agileSprintSubCalendar.setDateFieldNames(this.getDateFieldNames());
            agileSprintSubCalendar.setDurations(this.getDurations());
            return agileSprintSubCalendar;
        }
    }
}

