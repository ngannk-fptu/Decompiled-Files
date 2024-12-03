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

import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
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

@Component(value="customSubCalendarDataStore")
public class CustomSubCalendarDataStore
extends AbstractLocallyManagedChildSubCalendarDataStore<CustomSubCalendar> {
    public static final String STORE_KEY = "com.atlassian.confluence.extra.calendar3.calendarstore.generic.CustomSubCalendarDataStore";
    public static final String SUB_CALENDAR_TYPE = "custom";
    private final SubCalendarColorRegistry subCalendarColorRegistry;

    @Autowired
    public CustomSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor, ParentSubCalendarHelper parentSubCalendarHelper, SubCalendarColorRegistry subCalendarColorRegistry) {
        super(dataStoreCommonPropertyAccessor, parentSubCalendarHelper);
        this.subCalendarColorRegistry = subCalendarColorRegistry;
    }

    @Override
    protected String getDefaultSubCalendarColour() {
        return this.subCalendarColorRegistry.getRandomColourClass(new String[0]);
    }

    @Override
    protected CustomSubCalendar createNewSubCalendarInstance() {
        return new CustomSubCalendar();
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

    public static class CustomSubCalendar
    extends AbstractChildSubCalendar {
        @Override
        public String getType() {
            return CustomSubCalendarDataStore.SUB_CALENDAR_TYPE;
        }

        @Override
        public Object clone() {
            CustomSubCalendar customSubCalendar = new CustomSubCalendar();
            customSubCalendar.setParent(this.getParent());
            customSubCalendar.setId(this.getId());
            customSubCalendar.setName(this.getName());
            customSubCalendar.setDescription(this.getDescription());
            customSubCalendar.setCreator(this.getCreator());
            customSubCalendar.setColor(this.getColor());
            customSubCalendar.setTimeZoneId(this.getTimeZoneId());
            customSubCalendar.setCustomEventTypes(this.getCustomEventTypes());
            customSubCalendar.setCustomEventTypeId(this.getCustomEventTypeId());
            return customSubCalendar;
        }
    }
}

