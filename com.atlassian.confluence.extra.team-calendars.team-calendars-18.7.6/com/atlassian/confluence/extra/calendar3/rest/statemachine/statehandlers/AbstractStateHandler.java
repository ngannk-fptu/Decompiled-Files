/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine.statehandlers;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.StateHandler;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.UpdateEventContext;

public abstract class AbstractStateHandler
implements StateHandler<UpdateEventContext> {
    protected CalendarManager calendarManager;

    public AbstractStateHandler(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }
}

