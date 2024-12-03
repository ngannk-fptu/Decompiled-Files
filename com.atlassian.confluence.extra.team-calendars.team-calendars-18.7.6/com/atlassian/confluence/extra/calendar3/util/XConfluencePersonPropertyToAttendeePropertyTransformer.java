/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.util.UserKeyMigratorTransformer;
import com.atlassian.confluence.user.UserAccessor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;

public class XConfluencePersonPropertyToAttendeePropertyTransformer
extends UserKeyMigratorTransformer {
    public XConfluencePersonPropertyToAttendeePropertyTransformer(String baseUrl, UserAccessor userAccessor) {
        super(baseUrl, userAccessor);
    }

    @Override
    public Calendar transform(Calendar calendar) {
        ComponentList calendarVEventComponents = calendar.getComponents("VEVENT");
        if (null != calendarVEventComponents && !calendarVEventComponents.isEmpty()) {
            for (VEvent vEvent : calendarVEventComponents) {
                PropertyList<Property> vEventProperties = vEvent.getProperties();
                PropertyList confluencePersonProperties = vEventProperties.getProperties("X-CONFLUENCE-PERSON");
                if (null == confluencePersonProperties || confluencePersonProperties.isEmpty()) continue;
                for (Property confluencePersonProperty : confluencePersonProperties) {
                    vEventProperties.remove(confluencePersonProperty);
                    this.addProperty(vEvent, "ATTENDEE", confluencePersonProperty.getValue());
                }
            }
        }
        return calendar;
    }
}

