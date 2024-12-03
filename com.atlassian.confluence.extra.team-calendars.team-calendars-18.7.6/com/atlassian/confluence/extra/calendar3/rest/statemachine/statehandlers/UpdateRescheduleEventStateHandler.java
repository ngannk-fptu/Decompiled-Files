/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.SubCalendarEventConverter;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.UpdateEventContext;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers.AbstractStateHandler;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateRescheduleEventStateHandler
extends AbstractStateHandler {
    @Autowired
    public UpdateRescheduleEventStateHandler(CalendarManager calendarManager) {
        super(calendarManager);
    }

    @Override
    public void register(EventUpdateStateMachine stateMachine) {
        stateMachine.onState(EventUpdateStateMachine.States.UPDATED_RESCHEDULED_EVENT, this);
    }

    @Override
    public void onState(UpdateEventContext context) throws Exception {
        VEvent originalEvent;
        RRule rRule;
        UpdateEventParam param = context.getUpdateEventParam();
        String originalSubCalendarId = param.getOriginalSubCalendarId();
        String subCalendarId = param.getSubCalendarId();
        String _subCalendarId = StringUtils.defaultIfEmpty(originalSubCalendarId, subCalendarId);
        SubCalendarEvent subCalendarEvent = context.getSubCalendarEvent();
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(_subCalendarId);
        subCalendarEvent.setSubCalendar(persistedSubCalendar);
        PersistedSubCalendar innerPersistedSubCalendar = persistedSubCalendar;
        if ("com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore".equals(persistedSubCalendar.getStoreKey())) {
            InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar internalSubscriptionSubCalendar = (InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)persistedSubCalendar;
            innerPersistedSubCalendar = internalSubscriptionSubCalendar.getSourceSubCalendar();
        }
        if ((rRule = (RRule)(originalEvent = ((SubCalendarEventConverter)((Object)this.calendarManager)).getEvent(innerPersistedSubCalendar, subCalendarEvent.getUid(), null)).getProperty("RRULE")) != null) {
            subCalendarEvent.setRruleStr(rRule.getValue());
        }
        if (StringUtils.isBlank(subCalendarEvent.getRecurrenceId())) {
            context.setUpdatedEvent(this.calendarManager.addEvent(subCalendarEvent));
        } else {
            context.setUpdatedEvent(this.calendarManager.updateEvent(subCalendarEvent));
        }
    }
}

