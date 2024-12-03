/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import java.util.List;
import javax.ws.rs.FormParam;

public class DisableEventTypeParam {
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="disableEventTypes")
    List<String> disableEventTypes;

    public List<String> getDisableEventTypes() {
        return this.disableEventTypes;
    }

    public void setDisableEventTypes(List<String> disableEventTypes) {
        this.disableEventTypes = disableEventTypes;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }
}

