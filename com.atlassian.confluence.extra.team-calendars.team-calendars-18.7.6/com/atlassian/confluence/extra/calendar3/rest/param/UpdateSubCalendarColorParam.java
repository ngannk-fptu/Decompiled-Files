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

public class UpdateSubCalendarColorParam
extends SpaceContextParam {
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="color")
    String color;
    @FormParam(value="include")
    List<String> subCalendarIncludes;

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public List<String> getSubCalendarIncludes() {
        return this.subCalendarIncludes;
    }

    public void setSubCalendarIncludes(List<String> subCalendarIncludes) {
        this.subCalendarIncludes = subCalendarIncludes;
    }
}

