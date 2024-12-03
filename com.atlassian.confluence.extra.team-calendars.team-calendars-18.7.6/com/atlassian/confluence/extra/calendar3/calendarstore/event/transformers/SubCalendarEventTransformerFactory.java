/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import net.fortuna.ical4j.model.component.VEvent;

public interface SubCalendarEventTransformerFactory {
    public SubCalendarEventTransformer<TransformParameters> getDefaultTransformer();

    public SubCalendarEventTransformer<TransformParameters> getInviteesTransformer();

    public SubCalendarEventTransformer<TransformParameters> getNoInviteesTransformer();

    public SubCalendarEventTransformer<TransformParameters> getDescriptionHtmlCleaningTransformer();

    public SubCalendarEventTransformer<JiraSubCalendarEventTransformParameters> getJiraTransformer();

    public SubCalendarEventTransformer<TransformParameters> getReminderTransformer();

    public static interface JiraSubCalendarEventTransformParameters
    extends TransformParameters {
        public ApplicationLink getJiraLink();
    }

    public static interface ReminderTransformParameters
    extends TransformParameters {
        public SubCalendarEventTransformerFactory getSubCalendarEventTransformerFactory();

        public Collection<CustomEventType> getAvailableCustomEventTypes();
    }

    public static interface TransformParameters {
        public VEvent getRawEvent();

        public boolean isReadOnly();
    }

    public static interface SubCalendarEventTransformer<T extends TransformParameters> {
        public SubCalendarEvent transform(SubCalendarEvent var1, ConfluenceUser var2, T var3);
    }
}

