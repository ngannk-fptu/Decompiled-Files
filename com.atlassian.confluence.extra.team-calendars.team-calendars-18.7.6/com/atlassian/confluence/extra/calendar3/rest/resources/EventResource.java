/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.Collections2
 *  com.sun.jersey.api.core.InjectParam
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.ISODateTimeFormat
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.GenericMessage;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.exception.RuntimeCredentialsRequiredException;
import com.atlassian.confluence.extra.calendar3.model.ConfluenceUserInvitee;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.LocalizedSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.AbstractSubCalendarResource;
import com.atlassian.confluence.extra.calendar3.rest.EventResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.EventResponseWarningEntity;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.extra.calendar3.rest.LoadEventsOverOauthRequiredEntity;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.param.DeleteEventParam;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.resources.MessageToStringTransformerFunction;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.StateHandler;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.UpdateEventContext;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.EventChainValidator;
import com.atlassian.confluence.extra.calendar3.util.TimeZoneUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import com.sun.jersey.api.core.InjectParam;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="calendar/events")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
public class EventResource
extends AbstractSubCalendarResource {
    private static final int MAX_USERS = 3;
    private final CalendarSettingsManager teamCalendarSettingsManager;
    private final FormatSettingsManager formatSettingsManager;
    private final EventChainValidator eventChainValidator;
    private final List<StateHandler> stateHandlers;

    protected EventResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, CalendarSettingsManager teamCalendarSettingsManager, UserAccessor userAccessor, SettingsManager settingsManager, FormatSettingsManager formatSettingsManager, EventChainValidator eventValidator, List<StateHandler> stateHandlers, @Qualifier(value="cacheFactory") CacheFactory cacheFactory) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor, settingsManager, cacheFactory);
        this.teamCalendarSettingsManager = teamCalendarSettingsManager;
        this.formatSettingsManager = formatSettingsManager;
        this.eventChainValidator = eventValidator;
        this.stateHandlers = stateHandlers;
    }

    @PUT
    @Produces(value={"application/json"})
    public Response updateEvent(@InjectParam UpdateEventParam param) throws Exception {
        boolean isTurnOnReminder;
        PersistedSubCalendar subCalendar;
        SubCalendarEvent updatedEvent;
        DateTimeZone userTimeZone;
        ConfluenceUser currentUser;
        block14: {
            PersistedSubCalendar parentSubCalendar;
            String customEventTypeId;
            String eventType;
            block15: {
                DateTime start;
                LinkedHashMap<String, List<String>> fieldErrors = new LinkedHashMap<String, List<String>>();
                this.eventChainValidator.isValid(param, fieldErrors);
                if (!fieldErrors.isEmpty()) {
                    return this.createErrorResponse(fieldErrors);
                }
                currentUser = AuthenticatedUserThreadLocal.get();
                String subCalendarId = param.getSubCalendarId();
                String originalSubCalendarId = param.getOriginalSubCalendarId();
                eventType = param.getEventType();
                String uid = param.getUid();
                String what = param.getWhat();
                String originalEventType = param.getOriginalEventType();
                customEventTypeId = param.getCustomEventTypeId();
                List<String> person = param.getPerson();
                String url = param.getUrl();
                boolean allDayEvent = param.isAllDayEvent();
                String freq = param.getFreq();
                String interval = param.getInterval();
                String until = param.getUntil();
                String userTimeZoneId = param.getUserTimeZoneId();
                userTimeZone = DateTimeZone.forID((String)userTimeZoneId);
                boolean dragAndDropUpdate = param.isDragAndDropUpdate();
                DateTime dateTime = dragAndDropUpdate ? DateTimeFormat.forPattern((String)"ddMMyyyyHHmm").withZone(userTimeZone).parseDateTime(param.getStartDateDnd()) : (start = TimeZoneUtil.tryParseDateTimeStringForEventEdit(this.localeManager, param.getStartDate(), allDayEvent ? null : param.getStartTime(), userTimeZone, this.teamCalendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a"));
                DateTime end = dragAndDropUpdate ? DateTimeFormat.forPattern((String)"ddMMyyyyHHmm").withZone(userTimeZone).parseDateTime(param.getEndDateDnd()) : TimeZoneUtil.tryParseDateTimeStringForEventEdit(this.localeManager, param.getEndDate(), allDayEvent ? null : param.getEndTime(), userTimeZone, this.teamCalendarSettingsManager.isTimeFormat24Hour() ? "H:mm" : "h:mm a");
                end = allDayEvent ? end.plusDays(1) : end;
                String repeatUntilString = null;
                if (StringUtils.isNotBlank(until)) {
                    LocalDate localDate = TimeZoneUtil.tryParseBasicDateStringForEventEdit(until);
                    Objects.requireNonNull(localDate);
                    repeatUntilString = allDayEvent ? localDate.toString(ISODateTimeFormat.basicDate()) : new DateTime(start.withDate(localDate).getMillis(), DateTimeZone.UTC).toString(ISODateTimeFormat.basicDateTimeNoMillis());
                }
                SubCalendarEvent subCalendarEvent = new SubCalendarEvent();
                subCalendarEvent.setEditAllInRecurrenceSeries(param.isEditAllInRecurrenceSeries());
                if (StringUtils.isNotBlank(uid)) {
                    subCalendarEvent.setUid(this.getUnwrappedEventUid(uid));
                }
                if (StringUtils.isNotBlank(param.getOriginalStartDate())) {
                    subCalendarEvent.setOriginalStart(param.getOriginalStartDate());
                }
                subCalendarEvent.setName(what);
                subCalendarEvent.setInvitees(Optional.ofNullable(person).orElse(Collections.emptyList()).stream().map(confluenceUserId -> new ConfluenceUserInvitee(this.getUserById((String)confluenceUserId))).collect(Collectors.toSet()));
                subCalendarEvent.setUrl(StringUtils.trim(url));
                subCalendarEvent.setDescription(StringUtils.defaultString(param.getDescription()));
                subCalendarEvent.setLocation(StringUtils.defaultString(param.getWhere()));
                subCalendarEvent.setStartTime(start);
                subCalendarEvent.setEndTime(end);
                subCalendarEvent.setRecurrenceId(StringUtils.defaultIfEmpty(StringUtils.trim(param.getRecurrenceId()), null));
                subCalendarEvent.setAllDay(allDayEvent);
                subCalendarEvent.setEventType(eventType);
                subCalendarEvent.setCustomEventTypeId(param.getCustomEventTypeId());
                subCalendarEvent.setOriginalCustomEventTypeId(param.getOriginalCustomEventTypeId());
                if (param.isEditAllInRecurrenceSeries() || StringUtils.isBlank(subCalendarEvent.getRecurrenceId())) {
                    if (StringUtils.isNotBlank(param.getRruleStr())) {
                        if (StringUtils.isNotBlank(repeatUntilString)) {
                            if (param.getRruleStr().contains("UNTIL")) {
                                throw new RuntimeException(String.format("RRule string %s sent is incorrect", param.getRruleStr()));
                            }
                            String newRruleString = param.getRruleStr() + ";UNTIL=" + repeatUntilString;
                            subCalendarEvent.setRruleStr(newRruleString);
                        } else {
                            subCalendarEvent.setRruleStr(param.getRruleStr());
                        }
                    }
                    if (StringUtils.isNotBlank(freq)) {
                        subCalendarEvent.setRepeat(new SubCalendarEvent.Repeat(StringUtils.defaultIfEmpty(StringUtils.trim(freq), null), StringUtils.defaultIfEmpty(StringUtils.trim(param.getByDay()), null), StringUtils.defaultIfEmpty(StringUtils.trim(interval), null), repeatUntilString));
                    }
                }
                UpdateEventContext context = new UpdateEventContext(originalSubCalendarId, subCalendarId, subCalendarEvent, param);
                EventUpdateStateMachine<UpdateEventContext> stateMachine = new EventUpdateStateMachine<UpdateEventContext>(context);
                stateMachine.registerHandler(this.stateHandlers);
                stateMachine.start(EventUpdateStateMachine.States.START);
                if (StringUtils.isBlank(subCalendarEvent.getUid())) {
                    stateMachine.trigger(EventUpdateStateMachine.Events.create);
                } else {
                    stateMachine.trigger(EventUpdateStateMachine.Events.update);
                }
                updatedEvent = context.getUpdatedEvent();
                this.calendarManager.unhideEventsOfSubCalendar(updatedEvent.getSubCalendar(), currentUser);
                subCalendar = updatedEvent.getSubCalendar();
                parentSubCalendar = subCalendar.getParent();
                isTurnOnReminder = false;
                if (parentSubCalendar == null) break block14;
                if (!StringUtils.isNotEmpty(customEventTypeId) || parentSubCalendar.getCustomEventTypes().isEmpty()) break block15;
                Set<CustomEventType> customEventTypes = parentSubCalendar.getCustomEventTypes();
                for (CustomEventType customEventType : customEventTypes) {
                    if (!customEventType.getCustomEventTypeId().equals(customEventTypeId) || customEventType.getPeriodInMins() <= 0) continue;
                    isTurnOnReminder = true;
                    break block14;
                }
                break block14;
            }
            if (!StringUtils.isEmpty(customEventTypeId) || parentSubCalendar.getEventTypeReminders().isEmpty()) break block14;
            Set<EventTypeReminder> eventTypeReminders = parentSubCalendar.getEventTypeReminders();
            for (EventTypeReminder eventTypeReminder : eventTypeReminders) {
                if (!eventTypeReminder.getEventTypeId().equals(eventType)) continue;
                isTurnOnReminder = true;
                break;
            }
        }
        return Response.ok((Object)new EventResponseEntity(new LocalizedSubCalendarEvent(updatedEvent, userTimeZone, this.getUserLocale(), this.formatSettingsManager, this.teamCalendarSettingsManager), new SubCalendarsResponseEntity.ExtendedSubCalendar(subCalendar, this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasReloadEventsPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasEditSubCalendarPrivilege(currentUser), this.calendarPermissionManager.hasEditEventPrivilege(subCalendar, currentUser), false, false, false, 0, false, this.calendarManager.isEventsOfSubCalendarHidden(subCalendar, currentUser), this.calendarPermissionManager.hasDeleteSubCalendarPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasAdminSubCalendarPrivilege(subCalendar, currentUser), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventViewUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventViewGroupRestrictions(subCalendar), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventEditUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventEditGroupRestrictions(subCalendar), Collections.emptySet(), isTurnOnReminder)).toJson().toString()).build();
    }

    @DELETE
    @Produces(value={"application/json"})
    public Response deleteEvent(@InjectParam DeleteEventParam param) throws Exception {
        Set<String> disableEventTypes;
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        boolean hasEditEventPrivilege = this.calendarPermissionManager.hasEditEventPrivilege(this.calendarManager.getSubCalendar(subCalendarId), AuthenticatedUserThreadLocal.get());
        if (!hasEditEventPrivilege) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (subCalendar.getParent() != null && (disableEventTypes = subCalendar.getParent().getDisableEventTypes()) != null) {
            boolean isDisableEventType = false;
            if (this.isSubscribingSubCalendar(subCalendar)) {
                String subscriptionType = ((SubscribingSubCalendar)subCalendar).getSubscriptionType();
                if (subscriptionType.equals("custom")) {
                    Set<CustomEventType> customEventTypeSet = subCalendar.getCustomEventTypes();
                    if (customEventTypeSet != null && !customEventTypeSet.isEmpty() && disableEventTypes.contains(customEventTypeSet.iterator().next().getCustomEventTypeId())) {
                        isDisableEventType = true;
                    }
                } else if (disableEventTypes.contains(subscriptionType)) {
                    isDisableEventType = true;
                }
            } else if (disableEventTypes.contains(subCalendar.getType())) {
                isDisableEventType = true;
            }
            if (isDisableEventType) {
                throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.disableEvent.permission.deleteevent", Arrays.asList(subCalendar.getName()))).toString()).build());
            }
        }
        String uid = param.getUid();
        String recurUntil = param.getRecurUntil();
        if (StringUtils.isNotBlank(recurUntil)) {
            this.calendarManager.stopEventRecurrence(this.calendarManager.getSubCalendar(subCalendarId), this.getUnwrappedEventUid(uid), recurUntil);
        } else {
            String originalStart = param.getOriginalStart();
            String recurrenceId = param.getRecurrenceId();
            if (StringUtils.isBlank(originalStart) || StringUtils.isNotBlank(recurrenceId)) {
                this.calendarManager.removeEvent(this.calendarManager.getSubCalendar(subCalendarId), this.getUnwrappedEventUid(uid), StringUtils.defaultIfEmpty(StringUtils.trim(recurrenceId), null));
            } else {
                PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
                this.calendarManager.excludeEvent(persistedSubCalendar, this.getUnwrappedEventUid(uid), ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(originalStart));
            }
        }
        return Response.ok((Object)new GeneralResponseEntity().toJson().toString()).build();
    }

    @AnonymousSiteAccess
    @GET
    @Produces(value={"application/json; charset=UTF-8"})
    public Response getEvents(@QueryParam(value="subCalendarId") String subCalendarId, @QueryParam(value="userTimeZoneId") String userTimeZoneId, @QueryParam(value="start") String start, @QueryParam(value="end") String end) throws Exception {
        ConfluenceUser currentUser;
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.loadevents.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (!this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, currentUser = AuthenticatedUserThreadLocal.get())) {
            String warningMessage = this.getPermissionsWarning(subCalendar);
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.FORBIDDEN).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray("warning-type", warningMessage).toString()).build());
        }
        try {
            DateTimeZone userTimeZone = DateTimeZone.forID((String)userTimeZoneId);
            Collection subCalendarEvents = Collections2.transform(this.calendarManager.getEvents(subCalendar, ISODateTimeFormat.dateTimeNoMillis().parseDateTime(start), ISODateTimeFormat.dateTimeNoMillis().parseDateTime(end).plusDays(30)), (Function)Functions.compose((Function)new LocalizedSubCalendarEventTransformer(userTimeZone, this.getUserLocale(), this.formatSettingsManager, this.teamCalendarSettingsManager), subCalendarEvent -> {
                if (null != subCalendarEvent.getOriginalStartTime()) {
                    subCalendarEvent.setUid(this.getWrappedEventUid((SubCalendarEvent)subCalendarEvent));
                }
                return subCalendarEvent;
            }));
            String warning = null;
            Message messageWarning = this.calendarManager.getSubCalendarEventWarning(subCalendar, ISODateTimeFormat.dateTimeNoMillis().parseDateTime(start).getMillis(), ISODateTimeFormat.dateTimeNoMillis().parseDateTime(end).plusDays(30).getMillis());
            if (messageWarning != null) {
                warning = new MessageToStringTransformerFunction(this.getI18nBean()).apply(messageWarning);
            }
            return Response.ok((Object)new EventResponseWarningEntity(warning, subCalendarEvents).toJson().toString()).build();
        }
        catch (CredentialsRequiredException e) {
            return this.handleApplinkCredential(subCalendarId, subCalendar, e);
        }
        catch (Exception e) {
            CacheException cacheException;
            Throwable innerException;
            if (e instanceof CacheException && (innerException = (cacheException = (CacheException)e).getCause()) instanceof RuntimeCredentialsRequiredException) {
                CredentialsRequiredException credentialsRequiredException = ((RuntimeCredentialsRequiredException)innerException).getCredentialsRequiredException();
                this.handleApplinkCredential(subCalendarId, subCalendar, credentialsRequiredException);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Unable to load events of sub-calendar %s", subCalendarId), (Throwable)e);
            } else {
                LOG.error(String.format("Unable to load events of sub-calendar %s", subCalendarId));
            }
            Response errorResponse = Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.loadevents.unexpected", Arrays.asList(subCalendar.getName(), subCalendarId, StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage()))).toString()).build();
            throw new WebApplicationException(errorResponse);
        }
    }

    private Response handleApplinkCredential(@QueryParam(value="subCalendarId") String subCalendarId, PersistedSubCalendar subCalendar, CredentialsRequiredException e) {
        LOG.info(String.format("Unable to retrieve JIRA sub-calendar %s events. User is probably not authenticated. Log at DEBUG level for more details.", subCalendarId));
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Unable to retrieve JIRA sub-calendar %s events.", subCalendarId), (Throwable)e);
        } else {
            LOG.error(String.format("Unable to retrieve JIRA sub-calendar %s events.", subCalendarId));
        }
        String oAuthUriString = e.getAuthorisationURI().toString();
        Response response = Response.status((Response.Status)Response.Status.UNAUTHORIZED).header("WWW-Authenticate", (Object)String.format("OAuth realm=\"%s\"", oAuthUriString)).entity((Object)new LoadEventsOverOauthRequiredEntity(oAuthUriString, subCalendar.getId(), subCalendar.getName())).build();
        throw new WebApplicationException(response);
    }

    @Path(value="upcoming/{status}")
    @GET
    public Response setUpcommingEventEnable(@PathParam(value="status") String status) {
        boolean isShowUpcommingBadge = Boolean.parseBoolean(StringUtils.defaultIfEmpty(status, "false"));
        this.teamCalendarSettingsManager.setShowUpcommingEventBadge(isShowUpcommingBadge);
        return Response.ok().build();
    }

    @AnonymousSiteAccess
    @Path(value="{viewingSpaceKey}/upcoming")
    @GET
    @Produces(value={"application/json"})
    public Response getSubCalendars(@PathParam(value="viewingSpaceKey") String viewingSpaceKey, @QueryParam(value="calendarContext") String calendarContext, @QueryParam(value="userTimeZoneId") String userTimeZoneId, @QueryParam(value="start") String start, @QueryParam(value="end") String end) throws JSONException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        DateTimeZone userTimeZone = DateTimeZone.forID((String)userTimeZoneId);
        DateTime startDate = new DateTime((Object)start).withZone(DateTimeZone.forOffsetHours((int)0)).withZoneRetainFields(userTimeZone);
        DateTime endDate = new DateTime((Object)end).withZone(DateTimeZone.forOffsetHours((int)0)).withZoneRetainFields(userTimeZone);
        if (!this.teamCalendarSettingsManager.isShowUpcommingEventBadge()) {
            return Response.ok((Object)this.getUpcommingJsonObject(0, 0).toString()).build();
        }
        Collection<SubCalendarsResponseEntity.ExtendedSubCalendar> subCalendars = this.getSubcalendarsInternal(calendarContext, viewingSpaceKey, null);
        if (subCalendars.size() > 5) {
            return Response.ok((Object)this.getUpcommingJsonObject(subCalendars.size(), 0).toString()).build();
        }
        int sumEvents = 0;
        for (SubCalendarsResponseEntity.ExtendedSubCalendar subCalendar : subCalendars) {
            Set<String> childSubCalendarIds = subCalendar.getSubCalendar().getChildSubCalendarIds();
            for (String subCalendarId : childSubCalendarIds) {
                PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
                if (!this.calendarPermissionManager.hasViewEventPrivilege(persistedSubCalendar, currentUser)) continue;
                try {
                    Set<SubCalendarEvent> upcomingEvents = this.calendarManager.getUpcomingEvents(persistedSubCalendar, startDate, endDate);
                    sumEvents += upcomingEvents.size();
                }
                catch (Exception e) {
                    LOG.debug(String.format("Unable to get events for subCalendarId: %s", subCalendarId), (Throwable)e);
                }
            }
        }
        JSONObject upComingEventsJson = this.getUpcommingJsonObject(subCalendars.size(), sumEvents);
        return Response.ok((Object)upComingEventsJson.toString()).build();
    }

    @AnonymousSiteAccess
    @Path(value="multiplexed")
    @GET
    @Produces(value={"application/json; charset=UTF-8"})
    public Response getEvents(@QueryParam(value="subCalendarIds") List<String> subCalendarIds, @QueryParam(value="userTimeZoneId") String userTimeZoneId, @QueryParam(value="start") String start, @QueryParam(value="daysMore") @DefaultValue(value="30") int daysMore) throws Exception {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Collection subCalendars = Optional.ofNullable(subCalendarIds).orElse(Collections.emptyList()).stream().map(this.calendarManager::getSubCalendar).filter(Objects::nonNull).filter(persistedSubCalendar -> this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)persistedSubCalendar, user)).collect(Collectors.toList());
        DateTime startRange = ISODateTimeFormat.dateTimeNoMillis().parseDateTime(start);
        DateTimeZone userTimeZone = DateTimeZone.forID((String)userTimeZoneId);
        ArrayList multiplexedSubCalendarEvents = new ArrayList();
        for (PersistedSubCalendar subCalendar : subCalendars) {
            Collection subCalendarEvents = Collections2.transform(this.calendarManager.getEvents(subCalendar, startRange, startRange.plusDays(daysMore)), (Function)Functions.compose((Function)new LocalizedSubCalendarEventTransformer(userTimeZone, this.getUserLocale(), this.formatSettingsManager, this.teamCalendarSettingsManager), subCalendarEvent -> {
                if (null != subCalendarEvent.getOriginalStartTime()) {
                    subCalendarEvent.setUid(this.getWrappedEventUid((SubCalendarEvent)subCalendarEvent));
                }
                return subCalendarEvent;
            }));
            multiplexedSubCalendarEvents.addAll(subCalendarEvents);
        }
        return Response.ok((Object)new AbstractResource.JsonSerializablesStreamingOutput(multiplexedSubCalendarEvents, "UTF-8")).build();
    }

    private String getPermissionsWarning(PersistedSubCalendar subCalendar) {
        String warningMessage;
        Set<Object> editors = new HashSet();
        String contactAdminUrl = this.settingsManager.getGlobalSettings().getBaseUrl() + "/contactadministrators.action";
        if (subCalendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar) {
            PersistedSubCalendar parentSubCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)subCalendar).getSourceSubCalendar().getParent();
            editors = this.calendarPermissionManager.getEventEditUserRestrictions(parentSubCalendar);
            ConfluenceUser creatorUser = this.userAccessor.getUserByKey(new UserKey(parentSubCalendar.getCreator()));
            if (creatorUser != null) {
                editors.add(creatorUser);
            }
        }
        if (!editors.isEmpty()) {
            ArrayList<String> userNames = new ArrayList<String>();
            for (ConfluenceUser confluenceUser : editors) {
                userNames.add(StringEscapeUtils.escapeHtml(confluenceUser.getFullName()));
                if (userNames.size() <= 3) continue;
                break;
            }
            GenericMessage message = new GenericMessage("calendar3.error.loadevents.notpermission.contactUserEdit", new Serializable[]{StringEscapeUtils.escapeHtml(subCalendar.getName()), StringUtils.join(userNames, ", "), contactAdminUrl});
            warningMessage = this.getWarningMessages(subCalendar, message);
        } else {
            GenericMessage message = new GenericMessage("calendar3.error.loadevents.notpermission", new Serializable[]{StringEscapeUtils.escapeHtml(subCalendar.getName()), contactAdminUrl});
            warningMessage = this.getWarningMessages(subCalendar, message);
        }
        return warningMessage;
    }

    private String getWarningMessages(PersistedSubCalendar subCalendar, Message message) {
        return new MessageToStringTransformerFunction(this.getI18nBean()).apply(this.calendarManager.getTextForSubCalendar(subCalendar, message));
    }

    private JSONObject getUpcommingJsonObject(int numnberOfCalendar, int sumEvents) throws JSONException {
        JSONObject upComingEventsJson = new JSONObject();
        upComingEventsJson.put("upcomingEvents", sumEvents);
        upComingEventsJson.put("subCalendars", numnberOfCalendar);
        return upComingEventsJson;
    }

    private String getUnwrappedEventUid(String wrappedEventUid) {
        int indexOfSlash = StringUtils.indexOf(wrappedEventUid, '/');
        if (0 <= indexOfSlash && indexOfSlash < wrappedEventUid.length() - 1) {
            return wrappedEventUid.substring(indexOfSlash + 1);
        }
        return wrappedEventUid;
    }

    private String getWrappedEventUid(SubCalendarEvent sce) {
        return sce.getOriginalStart() + '/' + sce.getUid();
    }

    private boolean isSubscribingSubCalendar(PersistedSubCalendar persistedSubCalendar) {
        return persistedSubCalendar instanceof SubscribingSubCalendar;
    }

    private static class LocalizedSubCalendarEventTransformer
    implements Function<SubCalendarEvent, LocalizedSubCalendarEvent> {
        private final DateTimeZone userTimeZone;
        private final Locale userLocale;
        private final FormatSettingsManager formatSettingsManager;
        private final CalendarSettingsManager calendarSettingsManager;

        private LocalizedSubCalendarEventTransformer(DateTimeZone userTimeZone, Locale userLocale, FormatSettingsManager formatSettingsManager, CalendarSettingsManager calendarSettingsManager) {
            this.userTimeZone = userTimeZone;
            this.userLocale = userLocale;
            this.formatSettingsManager = formatSettingsManager;
            this.calendarSettingsManager = calendarSettingsManager;
        }

        public LocalizedSubCalendarEvent apply(SubCalendarEvent subCalendarEvent) {
            return new LocalizedSubCalendarEvent(subCalendarEvent, this.userTimeZone, this.userLocale, this.formatSettingsManager, this.calendarSettingsManager);
        }
    }
}

