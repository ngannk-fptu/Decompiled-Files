/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.CalendarTimeZonesProvider;
import com.atlassian.confluence.extra.calendar3.TimeZonesProvider;
import com.atlassian.confluence.extra.calendar3.UserTimeZonesProvider;
import com.atlassian.confluence.extra.calendar3.rest.TimeZonesResponseEntity;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.user.User;
import java.util.Locale;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="timezones")
@InterceptorChain(value={TransactionInterceptor.class})
public class TimezoneResource {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private final UserTimeZonesProvider userTimeZonesProvider;
    private final CalendarTimeZonesProvider calendarTimeZonesProvider;
    private final LocaleManager localeManager;

    public TimezoneResource(LocaleManager localeManager, UserTimeZonesProvider userTimeZonesProvider, CalendarTimeZonesProvider calendarTimeZonesProvider) {
        this.localeManager = localeManager;
        this.userTimeZonesProvider = userTimeZonesProvider;
        this.calendarTimeZonesProvider = calendarTimeZonesProvider;
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }

    @Path(value="user")
    @GET
    @Produces(value={"application/json"})
    public Response getUserTimeZones() {
        return this.getTimeZones(this.userTimeZonesProvider);
    }

    @Path(value="calendar")
    @GET
    @Produces(value={"application/json"})
    public Response getCalendarTimeZones() {
        return this.getTimeZones(this.calendarTimeZonesProvider);
    }

    private Response getTimeZones(TimeZonesProvider timeZonesProvider) {
        return Response.status((Response.Status)Response.Status.OK).header(CONTENT_TYPE_HEADER, (Object)"application/json").entity((Object)new TimeZonesResponseEntity(timeZonesProvider.getAvailableTimeZones(), this.getUserLocale()).toJson().toString()).build();
    }
}

