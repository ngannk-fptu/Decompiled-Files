/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.calendarstore.BaseCacheableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.DataStoreCommonPropertyAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.DelegatableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.XConfluencePersonPropertyToAttendeePropertyTransformer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.transform.Transformer;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractPeopleHandlingSubCalendarDataStore<T extends PersistedSubCalendar>
extends BaseCacheableCalendarDataStore<T>
implements RefreshableCalendarDataStore<T>,
DelegatableCalendarDataStore<T> {
    public AbstractPeopleHandlingSubCalendarDataStore(DataStoreCommonPropertyAccessor dataStoreCommonPropertyAccessor) {
        super(dataStoreCommonPropertyAccessor);
    }

    @Override
    public boolean handles(SubCalendar subCalendar) {
        return StringUtils.equals(this.getType(), subCalendar.getType());
    }

    @Override
    public void validate(SubCalendar subCalendar, Map<String, List<String>> fieldErrors) {
        super.validate(subCalendar, fieldErrors);
        if (StringUtils.isBlank(subCalendar.getTimeZoneId()) || !this.getJodaIcal4jTimeZoneMapper().getSupportedTimeZoneIds().contains(subCalendar.getTimeZoneId())) {
            this.addFieldError(fieldErrors, "timeZoneId", this.getText("calendar3.error.invalidfield"));
        }
    }

    protected String getText(String i18nKey) {
        return this.getI18NBean().getText(i18nKey);
    }

    @Override
    public SubCalendarEvent transform(final SubCalendarEvent toBeTransformed, final VEvent raw) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return this.getSubCalendarEventTransformerFactory().getInviteesTransformer().transform(super.transform(toBeTransformed, raw), currentUser, new SubCalendarEventTransformerFactory.TransformParameters(){

            @Override
            public VEvent getRawEvent() {
                return raw;
            }

            @Override
            public boolean isReadOnly() {
                return !toBeTransformed.isEditable();
            }
        });
    }

    @Override
    public Calendar getSubCalendarContent(T subCalendar) throws Exception {
        return new XConfluencePersonPropertyToAttendeePropertyTransformer(this.getSettingsManager().getGlobalSettings().getBaseUrl(), this.getUserAccessor()).transform(super.getSubCalendarContent(subCalendar));
    }

    @Override
    public void setSubCalendarContent(T subCalendar, Calendar subCalendarData) throws Exception {
        super.setSubCalendarContent(subCalendar, new Transformer<Calendar>(){

            @Override
            public Calendar transform(Calendar subCalendarData) {
                ComponentList<VEvent> eventComponents = subCalendarData.getComponents("VEVENT");
                if (null != eventComponents) {
                    for (VEvent eventComponent : eventComponents) {
                        Predicate confluenceAttendeePredicate;
                        PropertyList<Property> eventComponentProps;
                        PropertyList attendeeProperties;
                        ArrayList confluenceAttendeeProperties;
                        VEvent baseEvent;
                        if (!this.isRescheduledRecurrence(eventComponent) || null == (baseEvent = this.getBaseEvent(eventComponent, eventComponents)) || !(confluenceAttendeeProperties = new ArrayList(Collections2.filter(null == (attendeeProperties = (eventComponentProps = eventComponent.getProperties()).getProperties("ATTENDEE")) ? Collections.emptyList() : attendeeProperties, (Predicate)(confluenceAttendeePredicate = attendee -> null != attendee.getParameter("X-CONFLUENCE-USER-KEY"))))).isEmpty()) continue;
                        PropertyList baseEventAttendeeProperties = baseEvent.getProperties("ATTENDEE");
                        ArrayList baseEventConfluenceAttendeeProperties = new ArrayList(Collections2.filter(null == baseEventAttendeeProperties ? Collections.emptyList() : baseEventAttendeeProperties, (Predicate)confluenceAttendeePredicate));
                        for (Attendee baseEventConfluenceAttendee : baseEventConfluenceAttendeeProperties) {
                            eventComponentProps.add(new Attendee(baseEventConfluenceAttendee.getParameters(), URI.create(baseEventConfluenceAttendee.getValue())));
                        }
                    }
                }
                return subCalendarData;
            }

            private VEvent getBaseEvent(VEvent rescheduledRecurrence, List<VEvent> eventComponents) {
                Object rescheduledRecurrenceUid = rescheduledRecurrence.getProperty("UID");
                if (null != rescheduledRecurrenceUid && StringUtils.isNotBlank(((Content)rescheduledRecurrenceUid).getValue())) {
                    for (VEvent event : eventComponents) {
                        Object uidProperty = event.getProperty("UID");
                        if (null == uidProperty || !StringUtils.equals(((Content)rescheduledRecurrenceUid).getValue(), ((Content)uidProperty).getValue()) || this.isRescheduledRecurrence(event)) continue;
                        return event;
                    }
                }
                return null;
            }

            private boolean isRescheduledRecurrence(VEvent event) {
                Object recurrenceId = event.getProperty("RECURRENCE-ID");
                return null != recurrenceId && StringUtils.isNotBlank(((Content)recurrenceId).getValue());
            }
        }.transform(subCalendarData));
    }

    @Override
    public boolean hasReloadEventsPrivilege(T subCalendar, ConfluenceUser user) {
        return this.hasViewEventPrivilege(subCalendar, user);
    }

    @Override
    public void refresh(T subCalendar) {
    }
}

