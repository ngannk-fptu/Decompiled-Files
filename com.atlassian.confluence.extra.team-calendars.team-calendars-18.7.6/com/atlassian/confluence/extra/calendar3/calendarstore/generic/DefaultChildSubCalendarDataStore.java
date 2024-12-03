/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.generic;

import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.AbstractLocallyManagedChildSubCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.generic.ParentSubCalendarHelper;
import com.atlassian.confluence.extra.calendar3.model.AbstractChildSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.Invitee;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="defaultChildSubCalendarDataStore")
public class DefaultChildSubCalendarDataStore
extends AbstractLocallyManagedChildSubCalendarDataStore<DefaultChildSubCalendar> {
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.GenericLocalSubCalendarDataStore";
    public static final String SUB_CALENDAR_TYPE = "other";

    @Autowired
    public DefaultChildSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, ParentSubCalendarHelper parentSubCalendarHelper) {
        super(dataStoreCommonPropertyAccessor, parentSubCalendarHelper);
    }

    @Override
    protected String getStoreKey() {
        return STORE_KEY;
    }

    @Override
    public SubCalendarEvent transform(final SubCalendarEvent toBeTransformed, final VEvent raw) {
        Set<Invitee> invitees = toBeTransformed.getInvitees();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (invitees == null || invitees.isEmpty()) {
            SubCalendarEventTransformerFactory.TransformParameters transformParameters = new SubCalendarEventTransformerFactory.TransformParameters(){

                @Override
                public VEvent getRawEvent() {
                    return raw;
                }

                @Override
                public boolean isReadOnly() {
                    return !toBeTransformed.isEditable();
                }
            };
            SubCalendarEventTransformerFactory subCalendarEventTransformerFactory = this.getSubCalendarEventTransformerFactory();
            return subCalendarEventTransformerFactory.getDescriptionHtmlCleaningTransformer().transform(subCalendarEventTransformerFactory.getNoInviteesTransformer().transform(subCalendarEventTransformerFactory.getDefaultTransformer().transform(toBeTransformed, currentUser, transformParameters), currentUser, transformParameters), currentUser, transformParameters);
        }
        SubCalendarEvent transformed = super.transform(toBeTransformed, raw);
        transformed.setName(raw.getSummary().getValue());
        return transformed;
    }

    @Override
    protected String getDefaultSubCalendarColour() {
        return "subcalendar-blue";
    }

    @Override
    protected DefaultChildSubCalendar createNewSubCalendarInstance() {
        return new DefaultChildSubCalendar();
    }

    @Override
    public String getType() {
        return SUB_CALENDAR_TYPE;
    }

    public static class DefaultChildSubCalendar
    extends AbstractChildSubCalendar {
        @Override
        public String getType() {
            return DefaultChildSubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        public Object clone() {
            DefaultChildSubCalendar defaultChildSubCalendar = new DefaultChildSubCalendar();
            defaultChildSubCalendar.setParent(this.getParent());
            defaultChildSubCalendar.setId(this.getId());
            defaultChildSubCalendar.setName(this.getName());
            defaultChildSubCalendar.setDescription(this.getDescription());
            defaultChildSubCalendar.setColor(this.getColor());
            defaultChildSubCalendar.setCreator(this.getCreator());
            defaultChildSubCalendar.setTimeZoneId(this.getTimeZoneId());
            return defaultChildSubCalendar;
        }
    }
}

