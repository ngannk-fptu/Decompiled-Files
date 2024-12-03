/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.xml.bind.annotation.XmlElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.ParentSubCalendarRefreshed;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.ReminderPeriods;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.DisableEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="parentSubCalendarDataStore")
public class ParentSubCalendarDataStore
extends BaseCacheableCalendarDataStore<ParentSubCalendar>
implements RefreshableCalendarDataStore<ParentSubCalendar>,
DelegatableCalendarDataStore<ParentSubCalendar> {
    private static final Logger LOG = LoggerFactory.getLogger(ParentSubCalendarDataStore.class);
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericSubCalendarDataStore";
    public static final String SUB_CALENDAR_TYPE = "parent";
    private final EventPublisher eventPublisher;

    @Autowired
    public ParentSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, @ComponentImport EventPublisher eventPublisher) {
        super(dataStoreCommonPropertyAccessor);
        this.eventPublisher = eventPublisher;
    }

    private String getText(String i18nKey) {
        return this.getI18NBean().getText(i18nKey);
    }

    @Override
    protected String getStoreKey() {
        return STORE_KEY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ParentSubCalendar fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"ParentSubCalendarDataStore.fromStorageFormat()");
        try {
            ConfluenceUser creatorUser;
            String creator;
            JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper = this.getJodaIcal4jTimeZoneMapper();
            ParentSubCalendar parentSubCalendar = new ParentSubCalendar();
            parentSubCalendar.setId(subCalendarEntity.getID());
            parentSubCalendar.setName(subCalendarEntity.getName());
            parentSubCalendar.setDescription(subCalendarEntity.getDescription());
            parentSubCalendar.setColor(subCalendarEntity.getColour());
            parentSubCalendar.setCreator(subCalendarEntity.getCreator());
            parentSubCalendar.setSpaceKey(subCalendarEntity.getSpaceKey());
            parentSubCalendar.setSpaceName(this.getSpaceName(parentSubCalendar.getSpaceKey()));
            parentSubCalendar.setStoreKey(this.getStoreKey());
            parentSubCalendar.setCreatedDate(subCalendarEntity.getCreated());
            parentSubCalendar.setLastUpdateDate(subCalendarEntity.getLastModified());
            HashSet<String> disableEventTypes = new HashSet<String>();
            if (subCalendarEntity.getDisableEventTypes() != null) {
                for (DisableEventTypeEntity disableEventTypeEntity : subCalendarEntity.getDisableEventTypes()) {
                    disableEventTypes.add(disableEventTypeEntity.getEventKey());
                }
            }
            parentSubCalendar.setDisableEventTypes(disableEventTypes);
            HashSet<CustomEventType> customEventTypes = new HashSet<CustomEventType>();
            if (subCalendarEntity.getAvailableCustomEventTypes() != null) {
                Map<String, ReminderSettingEntity> reminderSettingEntityMap = this.getReminderSettingForCustomEvent(subCalendarEntity);
                for (CustomEventTypeEntity customEventTypeEntity : subCalendarEntity.getAvailableCustomEventTypes()) {
                    long periodInMilis = reminderSettingEntityMap.containsKey(customEventTypeEntity.getID()) ? reminderSettingEntityMap.get(customEventTypeEntity.getID()).getPeriod() : 0L;
                    customEventTypes.add(new CustomEventType(String.valueOf(customEventTypeEntity.getID()), customEventTypeEntity.getTitle(), customEventTypeEntity.getIcon(), customEventTypeEntity.getBelongSubCalendar() != null ? customEventTypeEntity.getBelongSubCalendar().getID() : null, customEventTypeEntity.getCreated(), ReminderPeriods.toReminderPeriod(periodInMilis).equals((Object)Option.none()) ? 0 : ((ReminderPeriods)((Object)ReminderPeriods.toReminderPeriod(periodInMilis).get())).getMins()));
                }
            }
            parentSubCalendar.setCustomEventTypes(customEventTypes);
            parentSubCalendar.setEventTypeReminders(this.getReminderSettingForSanboxEventType(subCalendarEntity));
            parentSubCalendar.setTimeZoneId(subCalendarEntity.getTimeZoneId());
            if (StringUtils.isBlank(parentSubCalendar.getTimeZoneId()) && StringUtils.isNotBlank(creator = parentSubCalendar.getCreator()) && null != (creatorUser = this.getUserAccessor().getUserByKey(new UserKey(creator)))) {
                parentSubCalendar.setTimeZoneId(jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(creatorUser));
            }
            if (jodaIcal4jTimeZoneMapper.isTimeZoneIdAnAlias(parentSubCalendar.getTimeZoneId())) {
                parentSubCalendar.setTimeZoneId(jodaIcal4jTimeZoneMapper.getTimeZoneIdForAlias(parentSubCalendar.getTimeZoneId()));
            }
            parentSubCalendar.setChildSubCalendarIds(this.getFilterChildSubCalendarIds(subCalendarEntity, disableEventTypes));
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Loaded ParentSubCalendar %s with ID %s", parentSubCalendar.getName(), parentSubCalendar.getId()));
            }
            ParentSubCalendar parentSubCalendar2 = parentSubCalendar;
            return parentSubCalendar2;
        }
        finally {
            UtilTimerStack.pop((String)"ParentSubCalendarDataStore.fromStorageFormat()");
        }
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new SubCalendarSummary(subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals(this.getType(), subCalendar.getType());
    }

    @Override
    public boolean hasReloadEventsPrivilege(ParentSubCalendar subCalendar, ConfluenceUser user) {
        return this.hasViewEventPrivilege(subCalendar, user);
    }

    @Override
    public void refresh(ParentSubCalendar subCalendar) {
        this.eventPublisher.publish((Object)new ParentSubCalendarRefreshed((Object)this, AuthenticatedUserThreadLocal.get(), subCalendar));
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        if (StringUtils.isBlank(subCalendar.getTimeZoneId()) || null == this.getJodaIcal4jTimeZoneMapper().getTimeZoneIdForAlias(subCalendar.getTimeZoneId())) {
            this.addFieldError(fieldErrors, "timeZoneId", this.getText("calendar3.error.invalidfield"));
        }
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    public static class ParentSubCalendar
    extends LocallyManagedSubCalendar
    implements Cloneable {
        private String id;
        private String creator;
        private String spaceName;
        private Set<String> childSubCalendarIds;
        private Set<String> disableEventTypes;
        private Set<CustomEventType> customEventTypes;
        private Set<EventTypeReminder> eventTypeReminders;

        @Override
        @XmlElement
        public String getType() {
            return ParentSubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        @XmlElement
        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        @XmlElement
        public String getCreator() {
            return this.creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        @Override
        @XmlElement
        public String getSpaceName() {
            return this.spaceName;
        }

        public void setSpaceName(String spaceName) {
            this.spaceName = spaceName;
        }

        @Override
        @XmlElement
        public boolean isWatchable() {
            return true;
        }

        @Override
        @XmlElement
        public boolean isRestrictable() {
            return true;
        }

        @Override
        @XmlElement
        public boolean isEventInviteesSupported() {
            return true;
        }

        @Override
        @XmlElement
        public Set<String> getChildSubCalendarIds() {
            return this.childSubCalendarIds;
        }

        protected void setChildSubCalendarIds(Set<String> childSubCalendarIds) {
            this.childSubCalendarIds = childSubCalendarIds;
        }

        @Override
        @XmlElement
        public Set<String> getDisableEventTypes() {
            return this.disableEventTypes;
        }

        @Override
        public void setDisableEventTypes(Set<String> disableEventTypes) {
            this.disableEventTypes = disableEventTypes;
        }

        @Override
        public Set<CustomEventType> getCustomEventTypes() {
            return this.customEventTypes;
        }

        @Override
        @XmlElement
        public void setCustomEventTypes(Set<CustomEventType> customEventTypes) {
            this.customEventTypes = customEventTypes;
        }

        @Override
        public Set<EventTypeReminder> getEventTypeReminders() {
            return this.eventTypeReminders;
        }

        @Override
        public void setEventTypeReminders(Set<EventTypeReminder> eventTypeReminders) {
            this.eventTypeReminders = eventTypeReminders;
        }

        @Override
        public Object clone() {
            ParentSubCalendar copy = new ParentSubCalendar();
            copy.setId(this.getId());
            copy.setType(this.getType());
            copy.setName(this.getName());
            copy.setDescription(this.getDescription());
            copy.setColor(this.getColor());
            copy.setCreator(this.getCreator());
            copy.setSpaceKey(this.getSpaceKey());
            copy.setSpaceName(this.getSpaceName());
            copy.setTimeZoneId(this.getTimeZoneId());
            copy.setChildSubCalendarIds(this.getChildSubCalendarIds());
            copy.setDisableEventTypes(this.getDisableEventTypes());
            copy.setCustomEventTypes(this.getCustomEventTypes());
            copy.setEventTypeReminders(this.getEventTypeReminders());
            return copy;
        }
    }
}

