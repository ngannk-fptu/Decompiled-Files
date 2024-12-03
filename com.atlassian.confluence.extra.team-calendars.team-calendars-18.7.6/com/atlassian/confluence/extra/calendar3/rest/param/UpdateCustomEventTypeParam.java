/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HtmlUtil
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import com.atlassian.confluence.util.HtmlUtil;
import javax.ws.rs.FormParam;

public class UpdateCustomEventTypeParam {
    @FormParam(value="customEventTypeId")
    String customEventTypeId;
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="title")
    String title;
    @FormParam(value="icon")
    String icon;
    @FormParam(value="periodInMins")
    int periodInMins;

    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    public void setCustomEventTypeId(String customEventTypeId) {
        this.customEventTypeId = customEventTypeId;
    }

    public String getIcon() {
        return HtmlUtil.htmlEncode((String)this.icon);
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

