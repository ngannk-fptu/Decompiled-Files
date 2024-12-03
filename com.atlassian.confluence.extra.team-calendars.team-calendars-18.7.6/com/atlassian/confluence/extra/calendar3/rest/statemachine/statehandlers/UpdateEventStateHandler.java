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
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.UpdateEventContext;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers.AbstractStateHandler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateEventStateHandler
extends AbstractStateHandler {
    @Autowired
    public UpdateEventStateHandler(CalendarManager calendarManager) {
        super(calendarManager);
    }

    @Override
    public void register(EventUpdateStateMachine stateMachine) {
        stateMachine.onState(EventUpdateStateMachine.States.UPDATE_EVENT, this);
    }

    @Override
    public void onState(UpdateEventContext context) throws Exception {
        UpdateEventParam param = context.getUpdateEventParam();
        String originalSubCalendarId = param.getOriginalSubCalendarId();
        String subCalendarId = param.getSubCalendarId();
        String _subCalendarId = StringUtils.defaultIfEmpty(originalSubCalendarId, subCalendarId);
        SubCalendarEvent subCalendarEvent = context.getSubCalendarEvent();
        subCalendarEvent.setSubCalendar(this.calendarManager.getSubCalendar(_subCalendarId));
        context.setUpdatedEvent(this.calendarManager.updateEvent(subCalendarEvent));
    }
}

