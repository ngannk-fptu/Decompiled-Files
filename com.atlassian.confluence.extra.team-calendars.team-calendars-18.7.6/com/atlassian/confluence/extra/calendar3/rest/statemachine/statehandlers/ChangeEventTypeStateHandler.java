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
public class ChangeEventTypeStateHandler
extends AbstractStateHandler {
    @Autowired
    public ChangeEventTypeStateHandler(CalendarManager calendarManager) {
        super(calendarManager);
    }

    @Override
    public void register(EventUpdateStateMachine stateMachine) {
        stateMachine.onState(EventUpdateStateMachine.States.CHANGE_EVENT_TYPE, this);
    }

    @Override
    public void onState(UpdateEventContext context) throws Exception {
        UpdateEventParam param = context.getUpdateEventParam();
        String originalSubCalendarId = param.getOriginalSubCalendarId();
        String subCalendarId = param.getSubCalendarId();
        String originalEventType = param.getOriginalEventType();
        String eventType = param.getEventType();
        SubCalendarEvent subCalendarEvent = context.getSubCalendarEvent();
        if (StringUtils.isNotBlank(originalSubCalendarId) && !StringUtils.equals(originalSubCalendarId, subCalendarId)) {
            subCalendarEvent.setSubCalendar(this.calendarManager.getSubCalendar(originalSubCalendarId));
        } else {
            subCalendarEvent.setSubCalendar(this.calendarManager.getSubCalendar(subCalendarId));
        }
        context.setUpdatedEvent(this.calendarManager.changeEvent(subCalendarEvent, originalEventType, eventType, subCalendarId));
    }
}

