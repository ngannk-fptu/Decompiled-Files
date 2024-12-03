/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.xml.bind.annotation.XmlElement
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.AbstractPeopleHandlingSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.model.LocallyManagedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.util.profiling.UtilTimerStack;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="peopleCalendarDataStore")
public class PeopleCalendarDataStore
extends AbstractPeopleHandlingSubCalendarDataStore<PeopleSubCalendar> {
    public static final String SUB_CALENDAR_TYPE = "people";
    private static final String SUB_CALENDAR_LOCATION_PREFIX = "people://";

    @Autowired
    public PeopleCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor) {
        super(dataStoreCommonPropertyAccessor);
    }

    @Override
    protected SubCalendarSummary toSummary(SubCalendarEntity subCalendarEntity) {
        return new SubCalendarSummary(subCalendarEntity.getID(), this.getType(), subCalendarEntity.getName(), subCalendarEntity.getDescription(), subCalendarEntity.getColour(), subCalendarEntity.getCreator());
    }

    @Override
    protected String getStoreKey() {
        return "com.atlassian.confluence.extra.calendar3.calendarstore.PeopleCalendarDataStore";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected PeopleSubCalendar fromStorageFormat(SubCalendarEntity subCalendarEntity) {
        UtilTimerStack.push((String)"PeopleCalendarDataStore.fromStorageFormat()");
        try {
            PeopleSubCalendar peopleSubCalendar = new PeopleSubCalendar();
            peopleSubCalendar.setId(subCalendarEntity.getID());
            peopleSubCalendar.setName(subCalendarEntity.getName());
            peopleSubCalendar.setDescription(subCalendarEntity.getDescription());
            peopleSubCalendar.setColor(subCalendarEntity.getColour());
            peopleSubCalendar.setCreator(subCalendarEntity.getCreator());
            peopleSubCalendar.setSpaceKey(subCalendarEntity.getSpaceKey());
            peopleSubCalendar.setSpaceName(this.getSpaceName(peopleSubCalendar.getSpaceKey()));
            peopleSubCalendar.setTimeZoneId(subCalendarEntity.getTimeZoneId());
            peopleSubCalendar.setStoreKey(this.getStoreKey());
            peopleSubCalendar.setCreatedDate(subCalendarEntity.getCreated());
            peopleSubCalendar.setLastUpdateDate(subCalendarEntity.getLastModified());
            PeopleSubCalendar peopleSubCalendar2 = peopleSubCalendar;
            return peopleSubCalendar2;
        }
        finally {
            UtilTimerStack.pop((String)"PeopleCalendarDataStore.fromStorageFormat()");
        }
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    public static class PeopleSubCalendar
    extends LocallyManagedSubCalendar
    implements Cloneable {
        private String id;
        private String creator;
        private String spaceName;

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
        public String getType() {
            return PeopleCalendarDataStore.SUB_CALENDAR_TYPE;
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
        public String getSourceLocation() {
            return PeopleCalendarDataStore.SUB_CALENDAR_LOCATION_PREFIX;
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
        public Object clone() {
            PeopleSubCalendar copy = new PeopleSubCalendar();
            copy.setId(this.getId());
            copy.setName(this.getName());
            copy.setDescription(this.getDescription());
            copy.setColor(this.getColor());
            copy.setCreator(this.getCreator());
            copy.setSpaceKey(this.getSpaceKey());
            copy.setSpaceName(this.getSpaceName());
            copy.setTimeZoneId(this.getTimeZoneId());
            return copy;
        }
    }
}

