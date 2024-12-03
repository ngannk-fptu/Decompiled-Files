/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.ResourceDataSourceAware;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import javax.activation.DataHandler;

public class ReminderSubCalendarEvent
extends SubCalendarEvent
implements ResourceDataSourceAware {
    private ReminderEvent reminderEvent;
    private DataHandler dataHandler;
    private String eventTypeIconUrl;

    public ReminderSubCalendarEvent(ReminderEvent reminderEvent) {
        this.reminderEvent = reminderEvent;
    }

    public ReminderEvent getReminderEvent() {
        return this.reminderEvent;
    }

    public void setReminderEvent(ReminderEvent reminderEvent) {
        this.reminderEvent = reminderEvent;
    }

    @Override
    public void setIconUrl(String iconUrl) {
        super.setIconUrl(iconUrl);
        this.reminderEvent.setIconUrl(iconUrl);
    }

    @Override
    public void setMediumIconUrl(String mediumIconUrl) {
        super.setMediumIconUrl(mediumIconUrl);
        this.reminderEvent.setMediumIconUrl(mediumIconUrl);
    }

    @Override
    public DataHandler getResourceDataHandler() {
        return this.dataHandler;
    }

    @Override
    public void setResourceDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.reminderEvent.setDataHandler(dataHandler);
    }

    public String getEventTypeIconUrl() {
        return this.eventTypeIconUrl;
    }

    public void setEventTypeIconUrl(String eventTypeIconUrl) {
        this.eventTypeIconUrl = eventTypeIconUrl;
        this.reminderEvent.setEventTypeIconUrl(eventTypeIconUrl);
    }
}

