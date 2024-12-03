/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.google.common.base.Optional
 *  com.sun.jersey.api.core.InjectParam
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.json.JSONObject
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.AbstractSubCalendarResource;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.param.DeleteCustomEventTypeParam;
import com.atlassian.confluence.extra.calendar3.rest.param.DisableEventTypeParam;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateCustomEventTypeParam;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateSandBoxEventTypeParam;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.google.common.base.Optional;
import com.sun.jersey.api.core.InjectParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="calendar/eventtype")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
public class EventTypeResource
extends AbstractSubCalendarResource {
    protected EventTypeResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, UserAccessor userAccessor, SettingsManager settingsManager, @Qualifier(value="cacheFactory") CacheFactory cacheFactory) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor, settingsManager, cacheFactory);
    }

    @Path(value="sandbox")
    @PUT
    @Produces(value={"application/json"})
    public Response updateSanboxEventType(@InjectParam UpdateSandBoxEventTypeParam param) throws Exception {
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        String title = param.getTitle();
        if (StringUtils.isBlank(title) || title.length() > 256) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar.customeventtype.error.title")).toString()).build());
        }
        if (StringUtils.isBlank(param.getIcon())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("com.atlassian.confluence.extra.team-calendars.eventtypes.custom.missing.icon")).toString()).build());
        }
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (persistedSubCalendar == null) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        if (!this.calendarPermissionManager.hasEditEventPrivilege(persistedSubCalendar, AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        this.calendarManager.updateReminderForSanboxEventType(persistedSubCalendar, param.getEventTypeId(), param.getPeriodInMins());
        return Response.ok((Object)new GeneralResponseEntity().toJson().toString()).build();
    }

    @Path(value="custom")
    @PUT
    @Produces(value={"application/json"})
    public Response updateCustomEventType(@InjectParam UpdateCustomEventTypeParam param) throws Exception {
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        String title = param.getTitle();
        if (StringUtils.isBlank(title) || title.length() > 256) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar.customeventtype.error.title")).toString()).build());
        }
        if (StringUtils.isBlank(param.getIcon())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("com.atlassian.confluence.extra.team-calendars.eventtypes.custom.missing.icon")).toString()).build());
        }
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (persistedSubCalendar == null) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        if (!this.calendarPermissionManager.hasEditEventPrivilege(persistedSubCalendar, AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        return this.updateCustomEventType(persistedSubCalendar, param.getCustomEventTypeId(), title, param.getIcon(), param.getPeriodInMins());
    }

    @Path(value="custom")
    @DELETE
    @Produces(value={"application/json"})
    public Response deleteCustomEventType(@InjectParam DeleteCustomEventTypeParam param) throws Exception {
        Optional<PersistedSubCalendar> persistedSubCalendar;
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            this.throwNotFoundWebApplicationException(subCalendarId);
        }
        if (!(persistedSubCalendar = this.calendarManager.getPersistedSubCalendar(subCalendarId)).isPresent()) {
            this.throwNotFoundWebApplicationException(subCalendarId);
        }
        if (!this.calendarPermissionManager.hasEditEventPrivilege((PersistedSubCalendar)persistedSubCalendar.get(), AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        String customEventTypeId = param.getCustomEventTypeId();
        if (StringUtils.isBlank(customEventTypeId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.customeventypenotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        this.calendarManager.deleteCustomEventType(subCalendarId, customEventTypeId);
        return Response.ok((Object)new GeneralResponseEntity().toJson().toString()).build();
    }

    @Path(value="disable")
    @PUT
    @Produces(value={"application/json"})
    public Response disableEventTypeCalendar(@InjectParam DisableEventTypeParam param) {
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (persistedSubCalendar == null) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        if (!this.calendarPermissionManager.hasEditEventPrivilege(persistedSubCalendar, AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        return this.disableEventTypes(persistedSubCalendar, param.getDisableEventTypes());
    }

    private Response updateCustomEventType(PersistedSubCalendar persistedSubCalendar, String customEventTypeId, String title, String icon, int periodInMins) throws Exception {
        LOG.info("Updating custom event type for Calendar " + persistedSubCalendar.getName());
        CustomEventType customEventType = this.calendarManager.updateCustomEventType(persistedSubCalendar, customEventTypeId, title, icon, periodInMins);
        return Response.status((Response.Status)Response.Status.OK).header("Content-Type", (Object)"application/json").entity((Object)customEventType.toJson().toString()).build();
    }

    private Response disableEventTypes(PersistedSubCalendar persistedSubCalendar, List<String> disableEventTypes) {
        try {
            this.calendarManager.disableEventTypes(persistedSubCalendar, disableEventTypes);
            String persistedSubCalendarId = persistedSubCalendar.getId();
            JSONObject responseJson = new SubCalendarsResponseEntity(new ArrayList<SubCalendarsResponseEntity.ExtendedSubCalendar>(this.getSubcalendarsInternal(null, null, new ArrayList<String>(Arrays.asList(persistedSubCalendarId)))), persistedSubCalendarId).toJson();
            return Response.ok((Object)responseJson.toString()).build();
        }
        catch (Exception e) {
            return this.getResponseError(e, "Unable to disable event for Calendar " + persistedSubCalendar.getName(), StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage());
        }
    }

    private void throwNotFoundWebApplicationException(String subCalendarId) {
        throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
    }
}

