/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine;

import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.Context;
import com.google.common.annotations.VisibleForTesting;

public class UpdateEventContext
extends Context {
    private SubCalendarEvent updatedEvent;
    private SubCalendarEvent subCalendarEvent;
    private String originalSubCalendarId;
    private String subCalendarId;
    private UpdateEventParam updateEventParam;

    public UpdateEventContext(String originalSubCalendarId, String subCalendarId, SubCalendarEvent subCalendarEvent, UpdateEventParam param) {
        this.originalSubCalendarId = originalSubCalendarId;
        this.subCalendarId = subCalendarId;
        this.subCalendarEvent = subCalendarEvent;
        this.updateEventParam = param;
    }

    @VisibleForTesting
    UpdateEventContext() {
    }

    public SubCalendarEvent getUpdatedEvent() {
        return this.updatedEvent;
    }

    public void setUpdatedEvent(SubCalendarEvent updatedEvent) {
        this.updatedEvent = updatedEvent;
    }

    public SubCalendarEvent getSubCalendarEvent() {
        return this.subCalendarEvent;
    }

    public void setSubCalendarEvent(SubCalendarEvent subCalendarEvent) {
        this.subCalendarEvent = subCalendarEvent;
    }

    public String getOriginalSubCalendarId() {
        return this.originalSubCalendarId;
    }

    public void setOriginalSubCalendarId(String originalSubCalendarId) {
        this.originalSubCalendarId = originalSubCalendarId;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public UpdateEventParam getUpdateEventParam() {
        return this.updateEventParam;
    }

    public void setUpdateEventParam(UpdateEventParam updateEventParam) {
        this.updateEventParam = updateEventParam;
    }
}

