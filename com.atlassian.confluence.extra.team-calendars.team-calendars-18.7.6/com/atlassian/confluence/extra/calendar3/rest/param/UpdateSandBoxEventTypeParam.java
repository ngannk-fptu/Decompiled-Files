/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class UpdateSandBoxEventTypeParam {
    @FormParam(value="eventTypeId")
    String eventTypeId;
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="title")
    String title;
    @FormParam(value="icon")
    String icon;
    @FormParam(value="periodInMins")
    int periodInMins;

    public String getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getPeriodInMins() {
        return this.periodInMins;
    }

    public void setPeriodInMins(int periodInMins) {
        this.periodInMins = periodInMins;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

