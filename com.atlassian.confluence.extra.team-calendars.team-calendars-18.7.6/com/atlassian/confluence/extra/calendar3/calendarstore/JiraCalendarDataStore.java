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
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
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

@Component(value="jiraCalendarDataStore")
public class JiraCalendarDataStore
extends AbstractJiraSubCalendarDataStore<JiraSubCalendar> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraCalendarDataStore.class);
    public static final String SUB_CALENDAR_TYPE = "jira";

    @Autowired
    public JiraCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @ComponentImport CacheManager cacheManager, JiraAccessor jiraAccessor, CalendarHelper calendarHelper) {
        super(dataStoreCommonPropertyAccessor, cacheManager, jiraAccessor, calendarHelper);
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals(this.getType(), subCalendar.getType());
    }

    @Override
    protected String getStoreKey() {
        return JiraCalendarDataStore.class.getName();
    }

    @Override
    protected String getType() {
        return SUB_CALENDAR_TYPE;
    }

    @Override
    protected JiraSubCalendar createNewJiraSubCalendar() {
        return new JiraSubCalendar();
    }

    public static class JiraSubCalendar
    extends AbstractJiraSubCalendar
    implements Cloneable {
        @Override
        @XmlElement
        public String getType() {
            return JiraCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        public static AbstractJiraSubCalendar fromJSON(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return (AbstractJiraSubCalendar)mapper.readValue(json, JiraSubCalendar.class);
            }
            catch (IOException e) {
                LOGGER.error("Could not json string to AgileSprintsSubCalendar object", (Throwable)e);
                return null;
            }
        }

        @Override
        public Object clone() {
            JiraSubCalendar jiraSubCalendar = new JiraSubCalendar();
            jiraSubCalendar.setId(this.getId());
            jiraSubCalendar.setName(this.getName());
            jiraSubCalendar.setDescription(this.getDescription());
            jiraSubCalendar.setColor(this.getColor());
            jiraSubCalendar.setCreator(this.getCreator());
            jiraSubCalendar.setSpaceKey(this.getSpaceKey());
            jiraSubCalendar.setSpaceName(this.getSpaceName());
            jiraSubCalendar.setTimeZoneId(this.getTimeZoneId());
            jiraSubCalendar.setDisableEventTypes(this.getDisableEventTypes());
            jiraSubCalendar.setCustomEventTypes(this.getCustomEventTypes());
            jiraSubCalendar.setSourceLocation(this.getSourceLocation());
            jiraSubCalendar.setApplicationName(this.getApplicationName());
            jiraSubCalendar.setApplicationId(this.getApplicationId());
            jiraSubCalendar.setProjectKey(this.getProjectKey());
            jiraSubCalendar.setProjectName(this.getProjectName());
            jiraSubCalendar.setSearchFilterId(this.getSearchFilterId());
            jiraSubCalendar.setSearchFilterName(this.getSearchFilterName());
            jiraSubCalendar.setJql(this.getJql());
            jiraSubCalendar.setDateFieldNames(this.getDateFieldNames());
            jiraSubCalendar.setDurations(this.getDurations());
            return jiraSubCalendar;
        }
    }
}

