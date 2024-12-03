/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.SpaceAware
 *  com.google.common.base.Optional
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.xwork.CalendarAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CalendarPageAction
extends CalendarAction
implements SpaceAware {
    private Space space;
    private String calendarId;
    Set<String> subCalendarIds;
    private String description;
    private String calendarName;
    private String creator;
    private String contentId;

    @Override
    public String execute() throws Exception {
        Optional<PersistedSubCalendar> calendarOption = this.calendarManager.getPersistedSubCalendar(this.calendarId);
        if (calendarOption.isPresent()) {
            this.subCalendarIds = ImmutableSet.of((Object)this.calendarId);
            PersistedSubCalendar subCalendar = (PersistedSubCalendar)calendarOption.get();
            CustomContentEntityObject content = this.calendarContentTypeManager.loadCalendarContent(subCalendar.getId());
            this.contentId = content.getIdAsString();
            this.description = subCalendar.getDescription();
            this.calendarName = subCalendar.getName();
            return "success";
        }
        return "notfound";
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

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    @Override
    protected CalendarRenderer.CalendarContext getCalendarContext() {
        return CalendarRenderer.CalendarContext.singleCalendar;
    }

    @Override
    protected Set<String> getSubCalendarIds() {
        return this.subCalendarIds;
    }

    public long getNoOfCalendars() {
        return 1L;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public String getSpaceCalendarUrl() {
        return this.space != null ? String.format("%s/display/%s/calendars", this.settingsManager.getGlobalSettings().getBaseUrl(), this.space.getKey()) : "";
    }

    @Override
    public String getCalendarId() {
        return this.contentId;
    }

    @Override
    protected Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> updateRenderParams(Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> commonParams) {
        super.updateRenderParams(commonParams);
        commonParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.contentId, this.contentId);
        commonParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.subCalendarId, this.calendarId);
        return commonParams;
    }
}

