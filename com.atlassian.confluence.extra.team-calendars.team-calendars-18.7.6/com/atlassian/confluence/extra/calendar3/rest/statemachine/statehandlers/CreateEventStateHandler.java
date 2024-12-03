/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.UpdateEventContext;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers.AbstractStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateEventStateHandler
extends AbstractStateHandler {
    @Autowired
    public CreateEventStateHandler(CalendarManager calendarManager) {
        super(calendarManager);
    }

    @Override
    public void register(EventUpdateStateMachine stateMachine) {
        stateMachine.onState(EventUpdateStateMachine.States.CREATING_EVENT, this);
    }

    @Override
    public void onState(UpdateEventContext context) throws Exception {
        String subCalendarId = context.getSubCalendarId();
        SubCalendarEvent subCalendarEvent = context.getSubCalendarEvent();
        subCalendarEvent.setSubCalendar(this.calendarManager.getSubCalendar(subCalendarId));
        SubCalendarEvent updatedEvent = this.calendarManager.addEvent(subCalendarEvent);
        context.setUpdatedEvent(updatedEvent);
    }
}

