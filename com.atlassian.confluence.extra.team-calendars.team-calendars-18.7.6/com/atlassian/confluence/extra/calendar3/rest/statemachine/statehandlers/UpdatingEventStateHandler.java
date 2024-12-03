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
public class UpdatingEventStateHandler
extends AbstractStateHandler {
    @Autowired
    public UpdatingEventStateHandler(CalendarManager calendarManager) {
        super(calendarManager);
    }

    @Override
    public void register(EventUpdateStateMachine stateMachine) {
        stateMachine.onState(EventUpdateStateMachine.States.UPDATING_EVENT, this);
    }

    @Override
    public void onState(UpdateEventContext context) throws Exception {
        UpdateEventParam param = context.getUpdateEventParam();
        SubCalendarEvent subCalendarEvent = context.getSubCalendarEvent();
        if (null == subCalendarEvent.getOriginalStartTime() || param.isEditAllInRecurrenceSeries()) {
            context.trigger(EventUpdateStateMachine.Events.update_original);
            subCalendarEvent.setRecurrenceId(null);
            this.processUpdateEvent(context, param);
        } else {
            context.trigger(EventUpdateStateMachine.Events.update_reschedule);
            this.processUpdateEvent(context, param);
        }
    }

    private void processUpdateEvent(UpdateEventContext context, UpdateEventParam param) throws Exception {
        String subCalendarId = context.getSubCalendarId();
        String originalSubCalendarId = context.getOriginalSubCalendarId();
        String eventType = param.getEventType();
        String originalEventType = param.getOriginalEventType();
        if (StringUtils.isNotBlank(originalSubCalendarId) && !StringUtils.equals(originalSubCalendarId, subCalendarId) && StringUtils.equals(originalEventType, eventType)) {
            context.trigger(EventUpdateStateMachine.Events.move);
        } else if (StringUtils.isNotBlank(originalEventType) && (!StringUtils.equals(originalEventType, eventType) || StringUtils.equals(eventType, "custom") && !StringUtils.equals(param.getOriginalCustomEventTypeId(), param.getCustomEventTypeId()))) {
            context.trigger(EventUpdateStateMachine.Events.change_event_type);
        } else {
            context.trigger(EventUpdateStateMachine.Events.update);
        }
    }
}

