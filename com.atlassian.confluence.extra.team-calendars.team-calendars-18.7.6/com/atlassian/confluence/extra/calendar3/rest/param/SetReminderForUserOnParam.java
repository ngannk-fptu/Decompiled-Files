/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class SetReminderForUserOnParam {
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="isReminder")
    boolean isReminder;

    public boolean isReminder() {
        return this.isReminder;
    }

    public void setReminder(boolean isReminder) {
        this.isReminder = isReminder;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }
}

