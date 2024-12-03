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

public class AddSubCalendarSubscriptionParam
extends SpaceContextParam {
    @FormParam(value="color")
    String color;
    @FormParam(value="watchSubCalendars")
    boolean addWatch;
    @FormParam(value="subCalendarIds")
    List<String> subCalendarIds;
    @FormParam(value="include")
    List<String> subCalendarIncludes;

    public boolean isAddWatch() {
        return this.addWatch;
    }

    public void setAddWatch(boolean addWatch) {
        this.addWatch = addWatch;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getSubCalendarIds() {
        return this.subCalendarIds;
    }

    public void setSubCalendarIds(List<String> subCalendarIds) {
        this.subCalendarIds = subCalendarIds;
    }

    public List<String> getSubCalendarIncludes() {
        return this.subCalendarIncludes;
    }

    public void setSubCalendarIncludes(List<String> subCalendarIncludes) {
        this.subCalendarIncludes = subCalendarIncludes;
    }
}

