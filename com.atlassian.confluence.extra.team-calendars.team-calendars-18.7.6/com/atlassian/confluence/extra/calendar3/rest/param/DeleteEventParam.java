/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import javax.ws.rs.FormParam;

public class DeleteEventParam {
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="uid")
    String uid;
    @FormParam(value="recurrenceId")
    String recurrenceId;
    @FormParam(value="recurUntil")
    String recurUntil;
    @FormParam(value="originalStart")
    String originalStart;

    public String getOriginalStart() {
        return this.originalStart;
    }

    public void setOriginalStart(String originalStart) {
        this.originalStart = originalStart;
    }

    public String getRecurrenceId() {
        return this.recurrenceId;
    }

    public void setRecurrenceId(String recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public String getRecurUntil() {
        return this.recurUntil;
    }

    public void setRecurUntil(String recurUntil) {
        this.recurUntil = recurUntil;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

