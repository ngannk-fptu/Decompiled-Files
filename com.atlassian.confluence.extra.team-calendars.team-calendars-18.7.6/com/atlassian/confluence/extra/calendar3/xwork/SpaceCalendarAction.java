/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.xwork.CalendarAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import java.util.HashMap;
import java.util.Map;

public class SpaceCalendarAction
extends CalendarAction
implements SpaceAware {
    private Space space;
    private long noOfCalendars;
    private String contentId;
    private Object licenseMessages;

    @Override
    public String execute() throws Exception {
        this.triggerDashboardViewEvent(true);
        this.noOfCalendars = this.calendarManager.countSubCalendarsOnSpace(this.space.getKey());
        CustomContentEntityObject cceo = this.calendarContentTypeManager.loadCalendarContentBySpaceKey(this.space.getKey());
        if (cceo == null) {
            this.calendarContentTypeManager.createCalendarContentTypeFor(this.space);
            cceo = this.calendarContentTypeManager.loadCalendarContentBySpaceKey(this.space.getKey());
        }
        this.contentId = cceo.getIdAsString();
        this.licenseMessages = this.getParams().get((Object)CalendarRenderer.RenderParamsBuilder.ParamName.licenseMessages);
        return "success";
    }

    public Space getSpace() {
        return this.space;
    }

    public Map<String, Object> getSpaceContext() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("action", (Object)this);
        result.put("spaceKey", this.space.getKey());
        result.put("spaceName", this.space.getName());
        result.put("collector-key", "space-calendar-sidebar-link");
        return result;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public boolean isSpaceRequired() {
        return false;
    }

    public boolean isViewPermissionRequired() {
        return true;
    }

    public String getActionName(String fullClassName) {
        return this.getText("calendar3.spacecalendars.header");
    }

    @Override
    protected CalendarRenderer.CalendarContext getCalendarContext() {
        return CalendarRenderer.CalendarContext.spaceCalendars;
    }

    public long getNoOfCalendars() {
        return this.noOfCalendars;
    }

    public Object getLicenseMessages() {
        return this.licenseMessages;
    }

    @Override
    protected Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> updateRenderParams(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> commonParams) {
        super.updateRenderParams(commonParams);
        commonParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.contentId, this.contentId);
        commonParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.enableShareCalendar, true);
        return commonParams;
    }
}

