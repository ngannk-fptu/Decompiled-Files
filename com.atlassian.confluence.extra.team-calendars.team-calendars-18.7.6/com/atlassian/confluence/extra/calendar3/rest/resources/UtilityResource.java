/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.joda.time.DateTime
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path(value="calendar/util")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
public class UtilityResource
extends AbstractResource {
    private final CalendarManager calendarManager;
    private final LocaleManager localeManager;
    private final FormatSettingsManager formatSettingsManager;
    private final CalendarSettingsManager calendarSettingsManager;

    public UtilityResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, UserAccessor userAccessor, FormatSettingsManager formatSettingsManager, CalendarSettingsManager calendarSettingsManager) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor);
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
        this.calendarManager = calendarManager;
        this.calendarSettingsManager = calendarSettingsManager;
    }

    @Path(value="format/subcalendar/ids")
    @GET
    @Produces(value={"application/json"})
    public Response getSubCalendarsMapFromCommaSeparatedIds(@QueryParam(value="subCalendarIds") String subCalendarIds) {
        JSONArray subCalendarsJsonArray = new JSONArray();
        try {
            for (String subCalendarId : new HashSet<String>(Arrays.asList(StringUtils.split(StringUtils.defaultString(subCalendarIds), ", ")))) {
                PersistedSubCalendar subCalendar = (PersistedSubCalendar)this.calendarManager.getPersistedSubCalendar(subCalendarId).orNull();
                if (null == subCalendar) continue;
                JSONObject subCalendarJson = new JSONObject();
                subCalendarJson.put("id", (Object)subCalendar.getId());
                subCalendarJson.put("name", (Object)subCalendar.getName());
                subCalendarsJsonArray.put((Object)subCalendarJson);
            }
            return Response.ok((Object)subCalendarsJsonArray.toString()).build();
        }
        catch (JSONException e) {
            LOG.error(String.format("Unable to translate sub calendar IDs %s", subCalendarIds), (Throwable)e);
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage()).toString()).build());
        }
    }

    @AnonymousSiteAccess
    @Path(value="format/date")
    @GET
    @Produces(value={"application/json"})
    public Response getFormattedDates(@QueryParam(value="date") List<String> dates, @QueryParam(value="pattern") String pattern) {
        if (null == dates || dates.isEmpty() || StringUtils.isBlank(pattern)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        List<String> formattedDates = this.getFormattedDatesInternal(dates, pattern);
        return Response.ok((Object)this.toJsonArray(formattedDates.toArray(new String[formattedDates.size()])).toString()).build();
    }

    @Path(value="validate/group")
    @GET
    @Produces(value={"application/json"})
    public Response filterInvalidGroupNames(@QueryParam(value="groupNames") List<String> groupNames) {
        Collection validGroupNames = Collections2.filter(groupNames, (Predicate)Predicates.and((Predicate)Predicates.notNull(), groupName -> null != this.userAccessor.getGroup(groupName)));
        return Response.ok().entity((Object)this.toJsonArray(validGroupNames.toArray(new String[validGroupNames.size()])).toString()).build();
    }

    private List<String> getFormattedDatesInternal(List<String> dates, String pattern) {
        DateTimeFormatter formatter;
        Locale userLocale = this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
        if (StringUtils.equals("date", pattern)) {
            formatter = this.getDateFormatter();
        } else if (StringUtils.equals("time", pattern)) {
            formatter = this.getTimeFormatter();
        } else if (StringUtils.equals("confluence", pattern)) {
            formatter = DateTimeFormat.forPattern((String)this.formatSettingsManager.getDateFormat()).withLocale(userLocale);
        } else if (StringUtils.isNotBlank(pattern)) {
            formatter = DateTimeFormat.forPattern((String)pattern).withLocale(userLocale);
        } else {
            throw new IllegalArgumentException("Date format pattern not specified");
        }
        return new ArrayList<String>(Collections2.transform(this.getDateStringAsDateTime(dates), arg_0 -> ((DateTimeFormatter)formatter).print(arg_0)));
    }

    private List<DateTime> getDateStringAsDateTime(List<String> dateString) {
        return new ArrayList<DateTime>(Collections2.transform(dateString, dateString1 -> {
            boolean isDateTime = dateString1.length() > 8;
            return new DateTime(Integer.parseInt(dateString1.substring(4, 8)), Integer.parseInt(dateString1.substring(2, 4)), Integer.parseInt(dateString1.substring(0, 2)), isDateTime ? Integer.parseInt(dateString1.substring(8, 10)) : 0, isDateTime ? Integer.parseInt(dateString1.substring(10)) : 0, 0, 0);
        }));
    }

    private DateTimeFormatter getDateFormatter() {
        return DateTimeFormat.mediumDate().withLocale(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    private DateTimeFormatter getTimeFormatter() {
        return DateTimeFormat.forPattern((String)(this.calendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a")).withLocale(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }
}

