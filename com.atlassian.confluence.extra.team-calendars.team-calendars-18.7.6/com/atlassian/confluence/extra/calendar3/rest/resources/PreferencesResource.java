/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.plugins.rest.common.security.LicensedOnly
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Optional
 *  com.sun.jersey.api.core.InjectParam
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.events.RemindingOffByAction;
import com.atlassian.confluence.extra.calendar3.events.RemindingOnByAction;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.extra.calendar3.rest.UserPreferenceListResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.param.HideEventOfSubCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.SetCalendarViewParam;
import com.atlassian.confluence.extra.calendar3.rest.param.SetReminderForUserOnParam;
import com.atlassian.confluence.extra.calendar3.rest.param.SetShowOnboardingSpacesParam;
import com.atlassian.confluence.extra.calendar3.rest.param.SetTimeZoneParam;
import com.atlassian.confluence.extra.calendar3.rest.param.ShowEventOfSubCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.SubCalendarIdParam;
import com.atlassian.confluence.extra.calendar3.rest.param.SuppressMessageParam;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.security.LicensedOnly;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Optional;
import com.sun.jersey.api.core.InjectParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

@Path(value="calendar/preferences")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
@LicensedOnly
public class PreferencesResource
extends AbstractResource {
    private final EventPublisher eventPublisher;

    protected PreferencesResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, EventPublisher eventPublisher, UserAccessor userAccessor) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor);
        this.eventPublisher = eventPublisher;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getCalendarPreferences(@QueryParam(value="user") String user) {
        if (user != null && !this.userAccessor.isSuperUser((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        ConfluenceUser theUser = user != null ? this.userAccessor.getUserByKey(new UserKey(user)) : AuthenticatedUserThreadLocal.get();
        return Response.ok((Object)this.calendarManager.getUserPreference(theUser).toJson().toString()).build();
    }

    @Path(value="bulk")
    @GET
    @Produces(value={"application/json"})
    public Response getCalendarUsers(@QueryParam(value="start") @DefaultValue(value="0") long start, @QueryParam(value="limit") @DefaultValue(value="100") long limit) {
        if (!this.userAccessor.isSuperUser((User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        try {
            return Response.ok((Object)new UserPreferenceListResponseEntity(this.calendarManager.getAllCalendarUsers(start, limit), start, limit).toJson().toString()).build();
        }
        catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @Path(value="subcalendars/setreminder")
    @Produces(value={"application/json"})
    @PUT
    public Response setReminderForLoginUserOn(@InjectParam SetReminderForUserOnParam param) throws Exception {
        boolean isReminder;
        boolean status;
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        ConfluenceUser loginUser = AuthenticatedUserThreadLocal.get();
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (persistedSubCalendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar) {
            persistedSubCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)persistedSubCalendar).getSourceSubCalendar();
        }
        if ((status = this.calendarManager.setReminderFor(persistedSubCalendar, loginUser, isReminder = param.isReminder())) == isReminder) {
            BaseSubCalendarEvent event = isReminder ? new RemindingOnByAction((Object)status, loginUser, persistedSubCalendar) : new RemindingOffByAction((Object)status, loginUser, persistedSubCalendar);
            this.eventPublisher.publish((Object)event);
        }
        JSONObject thisObj = new JSONObject();
        thisObj.put("success", true);
        thisObj.put("for_user", (Object)loginUser.getKey().getStringValue());
        thisObj.put("on_calendar", (Object)subCalendarId);
        thisObj.put("reminder_status", status);
        return Response.ok((Object)thisObj.toString()).build();
    }

    @Path(value="view")
    @PUT
    @Produces(value={"application/json"})
    public Response setCalendarView(@InjectParam SetCalendarViewParam param) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        UserCalendarPreference userCalendarPreference = this.calendarManager.getUserPreference(currentUser);
        userCalendarPreference.setCalendarView(param.getView());
        this.calendarManager.setUserPreference(currentUser, userCalendarPreference);
        return Response.ok().build();
    }

    @Path(value="timezone")
    @PUT
    @Produces(value={"application/json"})
    public Response set(@InjectParam SetTimeZoneParam param) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUserPreferences confluenceUserPrefs = this.userAccessor.getConfluenceUserPreferences((User)currentUser);
        try {
            confluenceUserPrefs.setTimeZone(param.getConfluenceTimeZoneId());
            return Response.ok().build();
        }
        catch (AtlassianCoreException unsupportedTimeZoneId) {
            LOG.error(String.format("Unable to change user's preference of time zone ID to %s", param.getConfluenceTimeZoneId()), (Throwable)unsupportedTimeZoneId);
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(StringUtils.isBlank(unsupportedTimeZoneId.getMessage()) ? ExceptionUtils.getStackTrace(unsupportedTimeZoneId) : unsupportedTimeZoneId.getMessage()).toString()).build());
        }
    }

    @Path(value="messagekey")
    @DELETE
    @Produces(value={"application/json"})
    public Response suppressMessage(@InjectParam SuppressMessageParam param) {
        this.suppressMessageInternal(param.getMessageKey());
        return Response.ok().build();
    }

    private void suppressMessageInternal(String messageKey) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        UserCalendarPreference calendarUserPref = this.calendarManager.getUserPreference(currentUser);
        HashSet<String> disabledMessageKeys = new HashSet<String>(calendarUserPref.getDisabledMessageKeys());
        disabledMessageKeys.add(messageKey);
        calendarUserPref.setDisabledMessageKeys(disabledMessageKeys);
        this.calendarManager.setUserPreference(currentUser, calendarUserPref);
    }

    @Path(value="subcalendars/watch")
    @PUT
    @Produces(value={"application/json"})
    public Response watchSubCalendar(@InjectParam SubCalendarIdParam param) {
        ConfluenceUser currentUser;
        HashSet<String> subCalendarsInView;
        String subCalendarId = param.getSubCalendarId();
        Optional<PersistedSubCalendar> persistedSubCalendar = this.calendarManager.getPersistedSubCalendar(subCalendarId);
        PersistedSubCalendar calendar = (PersistedSubCalendar)persistedSubCalendar.get();
        if (calendar instanceof SubscribingSubCalendar) {
            calendar = (PersistedSubCalendar)this.calendarManager.getPersistedSubCalendar(((SubscribingSubCalendar)calendar).getSubscriptionId()).get();
        }
        if (!(subCalendarsInView = new HashSet<String>(this.calendarManager.getSubCalendarsInView(currentUser = AuthenticatedUserThreadLocal.get()))).contains(subCalendarId)) {
            if (calendar.getCreator().equals(currentUser.getKey().toString())) {
                subCalendarsInView.add(calendar.getId());
                UserCalendarPreference userCalendarPreference = this.calendarManager.getUserPreference(currentUser);
                userCalendarPreference.setSubCalendarsInView(subCalendarsInView);
                this.calendarManager.setUserPreference(currentUser, userCalendarPreference);
            } else {
                ArrayList<String> subCalendarColors = new ArrayList<String>(this.calendarManager.getAvailableSubCalendarColorCssClasses());
                SubCalendar newCalendar = new SubCalendar();
                newCalendar.setType("internal-subscription");
                newCalendar.setName(calendar.getName());
                newCalendar.setDescription(calendar.getDescription());
                newCalendar.setColor(this.getNextSubCalendarColor(calendar.getColor(), subCalendarColors));
                newCalendar.setTimeZoneId(calendar.getTimeZoneId());
                newCalendar.setSourceLocation("subscription://" + calendar.getId());
                try {
                    this.calendarManager.save(newCalendar);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        this.calendarManager.watchSubCalendar(calendar, currentUser);
        return Response.ok().build();
    }

    @Path(value="subcalendars/watch")
    @DELETE
    @Produces(value={"application/json"})
    public Response unwatchSubCalendar(@InjectParam SubCalendarIdParam param) {
        this.calendarManager.unwatchSubCalendar(this.calendarManager.getSubCalendar(param.getSubCalendarId()), AuthenticatedUserThreadLocal.get());
        return Response.ok().build();
    }

    @AnonymousSiteAccess
    @Path(value="events/hidden")
    @PUT
    @Produces(value={"application/json"})
    public Response hideEventsOfSubCalendar(@InjectParam HideEventOfSubCalendarParam param) {
        List<String> subCalendarIds = param.getSubCalendarIds();
        if (null != AuthenticatedUserThreadLocal.get() && null != subCalendarIds && !subCalendarIds.isEmpty()) {
            for (String subCalendarId : subCalendarIds) {
                this.calendarManager.hideEventsOfSubCalendar(this.calendarManager.getSubCalendar(subCalendarId), AuthenticatedUserThreadLocal.get());
            }
        }
        return Response.noContent().build();
    }

    @AnonymousSiteAccess
    @Path(value="events/hidden")
    @DELETE
    @Produces(value={"application/json"})
    public Response showEventsOfSubCalendar(@InjectParam ShowEventOfSubCalendarParam param) {
        List<String> subCalendarIds = param.getSubCalendarIds();
        if (null != AuthenticatedUserThreadLocal.get() && null != subCalendarIds && !subCalendarIds.isEmpty()) {
            for (String subCalendarId : subCalendarIds) {
                this.calendarManager.unhideEventsOfSubCalendar(this.calendarManager.getSubCalendar(subCalendarId), AuthenticatedUserThreadLocal.get());
            }
        }
        return Response.noContent().build();
    }

    @Path(value="onboardingspace")
    @GET
    @Produces(value={"application/json"})
    public Response getShowOnboardingSpaces(@QueryParam(value="spaceKey") String spaceKey) throws JSONException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        UserCalendarPreference userCalendarPreference = this.calendarManager.getUserPreference(currentUser);
        boolean showOnboarding = true;
        if (userCalendarPreference.getShowOnboardingSpaces().contains(spaceKey)) {
            showOnboarding = false;
        }
        JSONObject showOnboardingJson = new JSONObject();
        showOnboardingJson.put("showOnboarding", showOnboarding);
        return Response.ok((Object)showOnboardingJson.toString()).build();
    }

    @Path(value="onboardingspace")
    @PUT
    @Produces(value={"application/json"})
    public Response setShowOnboardingSpaces(@InjectParam SetShowOnboardingSpacesParam param) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        UserCalendarPreference userCalendarPreference = this.calendarManager.getUserPreference(currentUser);
        userCalendarPreference.getShowOnboardingSpaces().add(param.getSpaceKey());
        this.calendarManager.setUserPreference(currentUser, userCalendarPreference);
        return Response.ok().build();
    }
}

