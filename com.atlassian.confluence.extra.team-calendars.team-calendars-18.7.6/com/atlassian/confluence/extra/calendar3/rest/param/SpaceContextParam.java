/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;

public class SpaceContextParam {
    @FormParam(value="calendarContext")
    @DefaultValue(value="myCalendars")
    String calendarContext;
    @FormParam(value="viewingSpaceKey")
    @DefaultValue(value="viewingSpaceKey")
    String viewingSpaceKey;

    public String getCalendarContext() {
        return this.calendarContext;
    }

    public void setCalendarContext(String calendarContext) {
        this.calendarContext = calendarContext;
    }

    public String getViewingSpaceKey() {
        return this.viewingSpaceKey;
    }

    public void setViewingSpaceKey(String viewingSpaceKey) {
        this.viewingSpaceKey = viewingSpaceKey;
    }
}

