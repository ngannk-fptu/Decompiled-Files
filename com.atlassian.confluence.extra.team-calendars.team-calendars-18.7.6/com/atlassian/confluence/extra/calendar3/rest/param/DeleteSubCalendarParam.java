/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import com.atlassian.confluence.extra.calendar3.rest.param.SpaceContextParam;
import java.util.List;
import javax.ws.rs.FormParam;

public class DeleteSubCalendarParam
extends SpaceContextParam {
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="include")
    List<String> subCalendarIdIncludes;

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public List<String> getSubCalendarIdIncludes() {
        return this.subCalendarIdIncludes;
    }

    public void setSubCalendarIdIncludes(List<String> subCalendarIdIncludes) {
        this.subCalendarIdIncludes = subCalendarIdIncludes;
    }
}

