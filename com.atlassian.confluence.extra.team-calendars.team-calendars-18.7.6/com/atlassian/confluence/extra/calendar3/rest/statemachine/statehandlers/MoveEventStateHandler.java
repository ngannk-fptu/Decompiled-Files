/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.UpdateEventContext;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers.AbstractStateHandler;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoveEventStateHandler
extends AbstractStateHandler {
    @Autowired
    public MoveEventStateHandler(CalendarManager calendarManager) {
        super(calendarManager);
    }

    @Override
    public void register(EventUpdateStateMachine stateMachine) {
        stateMachine.onState(EventUpdateStateMachine.States.MOVE_EVENT, this);
    }

    @Override
    public void onState(UpdateEventContext context) throws Exception {
        UpdateEventParam param = context.getUpdateEventParam();
        String originalSubCalendarId = param.getOriginalSubCalendarId();
        String subCalendarId = param.getSubCalendarId();
        String eventType = param.getEventType();
        SubCalendarEvent subCalendarEvent = context.getSubCalendarEvent();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        subCalendarEvent.setSubCalendar(this.calendarManager.getSubCalendar(originalSubCalendarId));
        this.calendarManager.deleteDisableEventType(subCalendarId, eventType);
        context.setUpdatedEvent(this.calendarManager.moveEvent(subCalendarEvent, this.calendarManager.getSubCalendar(subCalendarId)));
        this.calendarManager.unhideEventsOfSubCalendar(subCalendarEvent.getSubCalendar(), currentUser);
    }
}

