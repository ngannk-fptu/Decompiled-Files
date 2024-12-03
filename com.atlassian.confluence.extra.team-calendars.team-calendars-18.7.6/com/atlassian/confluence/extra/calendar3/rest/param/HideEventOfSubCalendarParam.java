/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import java.util.List;
import javax.ws.rs.FormParam;

public class HideEventOfSubCalendarParam {
    @FormParam(value="subCalendarId")
    List<String> subCalendarIds;

    public List<String> getSubCalendarIds() {
        return this.subCalendarIds;
    }

    public void setSubCalendarIds(List<String> subCalendarIds) {
        this.subCalendarIds = subCalendarIds;
    }
}

