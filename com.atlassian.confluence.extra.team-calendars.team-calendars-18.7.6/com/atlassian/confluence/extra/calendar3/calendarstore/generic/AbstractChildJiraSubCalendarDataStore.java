/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.xml.bind.annotation.XmlElement
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractJiraSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.JiraAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarHelper;
import com.atlassian.confluence.extra.calendar3.model.AbstractJiraSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ChildSubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.util.CalendarHelper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.util.profiling.UtilTimerStack;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChildJiraSubCalendarDataStore<T extends ChildJiraSubCalendar>
extends AbstractJiraSubCalendarDataStore<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractChildJiraSubCalendarDataStore.class);
    private final ParentSubCalendarHelper parentSubCalendarHelper;

    public AbstractChildJiraSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, CacheManager cacheManager, JiraAccessor jiraAccessor, ParentSubCalendarHelper parentSubCalendarHelper, CalendarHelper calendarHelper) {
        super(dataStoreCommonPropertyAccessor, cacheManager, jiraAccessor, calendarHelper);
        this.parentSubCalendarHelper = parentSubCalendarHelper;
    }

    public T createSubCalendarFrom(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map rawData = (Map)mapper.readValue(json, Map.class);
            String parentCalendarId = (String)rawData.get("parentId");
            T jiraSubCalendar = this.createSubCalendarFromInternal(mapper, json);
            Assertions.isTrue((String)"Parent Id from JSON should not be null or empty", (boolean)StringUtils.isNotEmpty(parentCalendarId));
            ((SubCalendar)jiraSubCalendar).setParent(this.parentSubCalendarHelper.getParentSubCalendar(parentCalendarId));
            return jiraSubCalendar;
        }
        catch (IOException e) {
            LOGGER.error("Exception during parse JSON string", (Throwable)e);
            return null;
        }
    }

    protected abstract T createSubCalendarFromInternal(ObjectMapper var1, String var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected T fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"AbstractChildJiraSubCalendarDataStore.getSubCalendar()");
        try {
            ChildJiraSubCalendar childJiraSubCalendar = (ChildJiraSubCalendar)super.fromStorageFormat(subCalendarEntity);
            childJiraSubCalendar.setParent(this.parentSubCalendarHelper.getParentSubCalendar(subCalendarEntity.getParent().getID()));
            ChildJiraSubCalendar childJiraSubCalendar2 = childJiraSubCalendar;
            return (T)childJiraSubCalendar2;
        }
        finally {
            UtilTimerStack.pop((String)"AbstractChildJiraSubCalendarDataStore.getSubCalendar()");
        }
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        PersistedSubCalendar parent = subCalendar.getParent();
        if (parent == null) {
            this.addFieldError(fieldErrors, "parentId", this.getText("calendar3.error.generic.parentnotspecified", new Object[0]));
        } else if (!this.parentSubCalendarHelper.canEditParentSubCalendarEvents(parent, AuthenticatedUserThreadLocal.get())) {
            this.addFieldError(fieldErrors, "parentId", this.getText("calendar3.error.generic.parentnotpermitted", new Object[0]));
        }
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new ChildSubCalendarSummary(subCalendarEntity.getParent().getID(), subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    public int getSubCalendarsCount() {
        return 0;
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals(this.getType(), subCalendar.getType());
    }

    @Override
    public boolean hasEditEventPrivilege(T subCalendar, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasEditEventPrivilege(((SubCalendar)subCalendar).getParent(), user);
    }

    @Override
    public boolean hasViewEventPrivilege(String subCalendarId, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasViewEventPrivilege(this.getSubCalendarEntity(subCalendarId).getParent().getID(), user);
    }

    @Override
    public boolean hasDeletePrivilege(T subCalendar, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasDeletePrivilege(((SubCalendar)subCalendar).getParent(), user);
    }

    @Override
    public boolean hasAdminPrivilege(T subCalendar, ConfluenceUser user) {
        return this.parentSubCalendarHelper.hasAdminPrivilege(((SubCalendar)subCalendar).getParent(), user);
    }

    protected abstract String getDefaultSubCalendarColour();

    public static abstract class ChildJiraSubCalendar
    extends AbstractJiraSubCalendar
    implements Cloneable {
        @Override
        @XmlElement
        public String getSpaceName() {
            PersistedSubCalendar parent = this.getParent();
            return parent != null ? parent.getSpaceName() : null;
        }

        @Override
        @XmlElement
        public String getSpaceKey() {
            PersistedSubCalendar parent = this.getParent();
            return parent != null ? parent.getSpaceKey() : null;
        }
    }
}

