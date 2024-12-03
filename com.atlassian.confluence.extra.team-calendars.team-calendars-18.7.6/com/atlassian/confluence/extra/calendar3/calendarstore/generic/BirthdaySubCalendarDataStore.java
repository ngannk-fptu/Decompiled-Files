/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.AbstractLocallyManagedChildSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarHelper;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.RequiresInvitees;
import com.atlassian.confluence.extra.calendar3.model.AbstractChildSubCalendar;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="birthdaySubCalendarDataStore")
public class BirthdaySubCalendarDataStore
extends AbstractLocallyManagedChildSubCalendarDataStore<BirthdaySubCalendar> {
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.BirthdaySubCalendarDataStore";
    public static final String SUB_CALENDAR_TYPE = "birthdays";

    @Autowired
    public BirthdaySubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, ParentSubCalendarHelper parentSubCalendarHelper) {
        super(dataStoreCommonPropertyAccessor, parentSubCalendarHelper);
    }

    @Override
    protected String getDefaultSubCalendarColour() {
        return "subcalendar-pink";
    }

    @Override
    protected BirthdaySubCalendar createNewSubCalendarInstance() {
        return new BirthdaySubCalendar();
    }

    @Override
    protected String getStoreKey() {
        return STORE_KEY;
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    public static class BirthdaySubCalendar
    extends AbstractChildSubCalendar
    implements Cloneable,
    RequiresInvitees {
        @Override
        @XmlElement
        public String getType() {
            return BirthdaySubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        public Object clone() {
            BirthdaySubCalendar birthdaySubCalendar = new BirthdaySubCalendar();
            birthdaySubCalendar.setParent(this.getParent());
            birthdaySubCalendar.setId(this.getId());
            birthdaySubCalendar.setName(this.getName());
            birthdaySubCalendar.setDescription(this.getDescription());
            birthdaySubCalendar.setCreator(this.getCreator());
            birthdaySubCalendar.setColor(this.getColor());
            birthdaySubCalendar.setTimeZoneId(this.getTimeZoneId());
            return birthdaySubCalendar;
        }
    }
}

