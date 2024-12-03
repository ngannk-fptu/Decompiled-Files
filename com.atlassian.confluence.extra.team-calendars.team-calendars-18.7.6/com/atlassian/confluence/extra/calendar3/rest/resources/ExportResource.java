/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.UnrestrictedAccess
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.google.common.base.Optional
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.ICalendarExporter;
import com.atlassian.confluence.extra.calendar3.PrivateCalendarUrlManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.UnrestrictedAccess;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.google.common.base.Optional;
import java.util.Arrays;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

@Path(value="calendar/export")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
public class ExportResource
extends AbstractResource {
    private final ICalendarExporter iCalendarExporter;
    private final CalendarSettingsManager teamCalendarSettingsManager;
    private final PrivateCalendarUrlManager privateCalendarUrlManager;

    protected ExportResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, UserAccessor userAccessor, ICalendarExporter iCalendarExporter, CalendarSettingsManager teamCalendarSettingsManager, PrivateCalendarUrlManager privateCalendarUrlManager) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor);
        this.iCalendarExporter = iCalendarExporter;
        this.teamCalendarSettingsManager = teamCalendarSettingsManager;
        this.privateCalendarUrlManager = privateCalendarUrlManager;
    }

    @Path(value="subcalendar/{subCalendarId}.ics")
    @GET
    @Produces(value={"text/calendar; charset=UTF-8"})
    public Response exportCalendarAsIcalendarText(@PathParam(value="subCalendarId") String subCalendarId, @QueryParam(value="isSubscribe") @DefaultValue(value="true") boolean isSubscribe) {
        return this.exportCalendarAsIcalendarTextInternal(AuthenticatedUserThreadLocal.get(), subCalendarId, false, isSubscribe);
    }

    @Path(value="subcalendar/private/{subCalendarToken}.ics")
    @GET
    @Produces(value={"text/calendar; charset=UTF-8"})
    @UnrestrictedAccess
    public Response exportPrivateCalendarAsIcalendarText(@PathParam(value="subCalendarToken") String subCalendarToken) {
        if (!this.teamCalendarSettingsManager.arePrivateUrlsEnabled()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        ConfluenceUser user = this.privateCalendarUrlManager.getUserFor(subCalendarToken);
        String subCalendarId = this.privateCalendarUrlManager.getCalendarId(subCalendarToken);
        if (subCalendarId == null) {
            LOG.info("No calendar found for private token: " + subCalendarToken + ". The user may have reset their private URL.");
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (user == null) {
            LOG.warn("No user associated with calendar: " + subCalendarId + " for private token: " + subCalendarToken);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        Optional<PersistedSubCalendar> optionalPersistedSubCalendar = this.calendarManager.getPersistedSubCalendar(subCalendarId);
        if (!optionalPersistedSubCalendar.isPresent()) {
            LOG.warn("Calendar not found for calendar id: " + subCalendarId + " for private token: " + subCalendarToken);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)optionalPersistedSubCalendar.get(), user)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return this.exportCalendarAsIcalendarTextInternal(user, subCalendarId, true, true);
    }

    private Response exportCalendarAsIcalendarTextInternal(ConfluenceUser forUser, String subCalendarId, boolean allowAnonymousAccess, boolean isSubscribe) {
        if (subCalendarId == null || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar subCalendar = (PersistedSubCalendar)this.calendarManager.getPersistedSubCalendar(subCalendarId).orNull();
        if (null == subCalendar || !allowAnonymousAccess && !this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok(outputStream -> {
                try {
                    this.iCalendarExporter.export(forUser, subCalendar, outputStream, isSubscribe);
                }
                catch (Exception exportError) {
                    throw new WebApplicationException((Throwable)exportError);
                }
            }).build();
        }
        catch (Exception e) {
            return this.getResponseError(e, String.format("Unable to export sub-calendar %s", subCalendarId), StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage());
        }
    }
}

