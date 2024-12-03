/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event.impl;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.AbstractEventValidator;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.JSONUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarEventValidator
extends AbstractEventValidator {
    private final CalendarManager calendarManager;
    private final CalendarPermissionManager calendarPermissionManager;

    @Autowired
    public CalendarEventValidator(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager) {
        super(localeManager, i18NBeanFactory);
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
    }

    @Override
    public boolean isValid(UpdateEventParam param, Map<String, List<String>> fieldErrors) throws WebApplicationException {
        Set<String> childSubCalendarIds;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        String subCalendarId = param.getSubCalendarId();
        String originalSubCalendarId = param.getOriginalSubCalendarId();
        String eventType = param.getEventType();
        if (StringUtils.isBlank(subCalendarId)) {
            this.addFieldError(fieldErrors, "calendar", this.getText("calendar3.error.blank"));
            return false;
        }
        if (!this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)JSONUtil.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (!this.calendarPermissionManager.hasEditEventPrivilege(persistedSubCalendar, currentUser)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.FORBIDDEN).header("Content-Type", (Object)"application/json").entity((Object)JSONUtil.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        Set<String> disableEventTypes = persistedSubCalendar.getDisableEventTypes();
        if (disableEventTypes != null && (StringUtils.isEmpty(originalSubCalendarId) || originalSubCalendarId.equals(subCalendarId)) && (disableEventTypes.contains(param.getCustomEventTypeId()) || disableEventTypes.contains(param.getOriginalCustomEventTypeId()) || disableEventTypes.contains(CalendarUtil.convertSubCalendarTypeToJiraEventType(eventType)) || disableEventTypes.contains(param.getOriginalEventType()))) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)JSONUtil.toJsonArray(this.getText(StringUtils.isEmpty(param.getUid()) ? "calendar3.error.disableEvent.permission.addevent" : "calendar3.error.disableEvent.permission.updateevent", Arrays.asList(persistedSubCalendar.getName()))).toString()).build());
        }
        String childSubCalendarId = param.getChildSubCalendarId();
        if (CalendarUtil.isJiraSubCalendarType(eventType) && StringUtils.isNotEmpty(childSubCalendarId) && !(childSubCalendarIds = persistedSubCalendar.getChildSubCalendarIds()).contains(childSubCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)JSONUtil.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(childSubCalendarId))).toString()).build());
        }
        if (StringUtils.isNotBlank(originalSubCalendarId) && !StringUtils.equals(originalSubCalendarId, subCalendarId)) {
            if (!this.calendarManager.hasSubCalendar(originalSubCalendarId)) {
                throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)JSONUtil.toJsonArray(this.getText("calendar3.error.srcsubcalendarnotfound", Arrays.asList(originalSubCalendarId))).toString()).build());
            }
            if (!this.calendarPermissionManager.hasEditEventPrivilege(this.calendarManager.getSubCalendar(originalSubCalendarId), currentUser)) {
                throw new WebApplicationException(Response.status((Response.Status)Response.Status.FORBIDDEN).header("Content-Type", (Object)"application/json").entity((Object)JSONUtil.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
            }
        }
        return true;
    }
}

