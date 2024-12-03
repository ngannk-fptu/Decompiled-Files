/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.user.User
 *  com.google.common.base.Optional
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  com.sun.jersey.api.core.InjectParam
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.PrivateCalendarUrlManager;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentTypeManager;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarAddedNew;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarAddedSubscription;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarCreatedOnJiraEventCreation;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarInternalSubscribed;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.UserCalendarPreference;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.AbstractSubCalendarResource;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarWatchingStatusEntity;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.SubscriptionCountResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.param.AddSubCalendarSubscriptionParam;
import com.atlassian.confluence.extra.calendar3.rest.param.DeleteSubCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.HideSubCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.RefreshSubCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.RestrictSubCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateCalendarParam;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateSubCalendarColorParam;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.user.User;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.sun.jersey.api.core.InjectParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@UnlicensedSiteAccess
@Path(value="calendar")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
public class CalendarResource
extends AbstractSubCalendarResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarResource.class);
    private final CalendarManager calendarManager;
    private final CalendarPermissionManager calendarPermissionManager;
    private final SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final PrivateCalendarUrlManager privateCalendarUrlManager;
    private final EventPublisher eventPublisher;
    private final SpaceManager spaceManager;
    private final CalendarRenderer calendarRenderer;
    private final CalendarContentTypeManager calendarContentTypeManager;
    private final PluginAccessor pluginAccessor;

    public CalendarResource(EventPublisher eventPublisher, SettingsManager settingsManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor, UserAccessor userAccessor, PrivateCalendarUrlManager privateCalendarUrlManager, PermissionManager permissionManager, SpacePermissionManager spacePermissionManager, SpaceManager spaceManager, CalendarRenderer calendarRenderer, CalendarContentTypeManager calendarContentTypeManager, PluginAccessor pluginAccessor, @Qualifier(value="cacheFactory") CacheFactory cacheFactory) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor, settingsManager, cacheFactory);
        this.eventPublisher = eventPublisher;
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.subCalendarSubscriptionStatisticsAccessor = subCalendarSubscriptionStatisticsAccessor;
        this.privateCalendarUrlManager = privateCalendarUrlManager;
        this.spacePermissionManager = spacePermissionManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.calendarRenderer = calendarRenderer;
        this.calendarContentTypeManager = calendarContentTypeManager;
        this.pluginAccessor = pluginAccessor;
    }

    @Path(value="subcalendars/user/view")
    @GET
    @Produces(value={"application/json"})
    public Response getSubCalendarsInUserView() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return Response.ok(this.calendarManager.getUserPreference(currentUser).getSubCalendarsInView()).build();
    }

    @Path(value="subcalendars/user/view")
    @PUT
    @Produces(value={"application/json"})
    public Response updateSubCalendarsInUserView(String[] subCalendarIds) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Set<String> finalSet = this.calendarManager.updateSubCalendarsInView(currentUser, subCalendarIds);
        return Response.ok(finalSet).build();
    }

    @AnonymousSiteAccess
    @Path(value="subcalendars/watching/status")
    @GET
    @Produces(value={"application/json"})
    public Response getWatchingStatus(@QueryParam(value="include") List<String> subCalendarIdIncludes) throws JSONException {
        try {
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            HashSet<String> subCalendarIds = new HashSet<String>(null == subCalendarIdIncludes || subCalendarIdIncludes.isEmpty() ? this.calendarManager.getSubCalendarsInView(currentUser) : subCalendarIdIncludes);
            List<PersistedSubCalendar> persistedSubCalendars = this.calendarManager.getSubCalendarsWithRestriction(subCalendarIds.toArray(new String[0]));
            Map<String, Boolean> watchingCalendarStatuses = this.calendarManager.isWatching(currentUser, persistedSubCalendars.toArray(new PersistedSubCalendar[0]));
            ArrayList<SubCalendarWatchingStatusEntity> watchingStatuses = new ArrayList<SubCalendarWatchingStatusEntity>();
            for (PersistedSubCalendar subCalendar : persistedSubCalendars) {
                boolean isWatch = watchingCalendarStatuses.get(subCalendar.getId());
                boolean isWatchViaContent = this.calendarManager.isWatchingViaContent(subCalendar instanceof SubscribingSubCalendar ? this.calendarManager.getSubCalendar(((SubscribingSubCalendar)subCalendar).getSubscriptionId()) : subCalendar, currentUser);
                watchingStatuses.add(new SubCalendarWatchingStatusEntity(subCalendar, isWatch, isWatchViaContent));
            }
            JSONObject thisObj = new JSONObject();
            JSONArray subCalendarArray = new JSONArray();
            for (SubCalendarWatchingStatusEntity subCalendarWatchingStatusEntity : watchingStatuses) {
                subCalendarArray.put((Object)subCalendarWatchingStatusEntity.toJson());
            }
            thisObj.put("success", true);
            thisObj.put("payload", (Object)subCalendarArray);
            return Response.ok((Object)thisObj.toString()).build();
        }
        catch (Exception e) {
            return this.getResponseError(e, "Unable to get watching statuses", StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage());
        }
    }

    @AnonymousSiteAccess
    @Path(value="subcalendars")
    @GET
    @Produces(value={"application/json"})
    public Response getSubCalendars(@QueryParam(value="include") List<String> subCalendarIdIncludes, @QueryParam(value="viewingSpaceKey") String viewingSpaceKey, @QueryParam(value="calendarContext") String calendarContext) {
        return this.getSubCalendarsResponse(calendarContext, viewingSpaceKey, subCalendarIdIncludes);
    }

    private Response getSubCalendarsResponse(String calendarContext, String spaceKey, List<String> subCalendarIdIncludes) {
        Collection<SubCalendarsResponseEntity.ExtendedSubCalendar> subCalendars = this.getSubcalendarsInternal(calendarContext, spaceKey, subCalendarIdIncludes);
        LOG.info("getSubCalendars successfully with total sub calendar is {}", (Object)(subCalendars == null ? 0 : subCalendars.size()));
        return Response.ok((Object)new SubCalendarsResponseEntity(new ArrayList<SubCalendarsResponseEntity.ExtendedSubCalendar>(subCalendars)).toJson().toString()).build();
    }

    @Path(value="subcalendars/preferences")
    @DELETE
    @Produces(value={"application/json"})
    public Response hideSubCalendar(@InjectParam HideSubCalendarParam param) {
        String subCalendarId = param.getSubCalendarId();
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        PersistedSubCalendar subCalendar = (PersistedSubCalendar)this.calendarManager.getPersistedSubCalendar(subCalendarId).orNull();
        if (subCalendar == null || !this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, authenticatedUser)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.calendarnotfound")).toString()).build());
        }
        if (CalendarRenderer.CalendarContext.spaceCalendars.getValue().equals(param.getCalendarContext())) {
            if (!this.spacePermissionManager.hasPermission("VIEWSPACE", this.spaceManager.getSpace(param.getViewingSpaceKey()), (User)authenticatedUser)) {
                throw new WebApplicationException(Response.status((Response.Status)Response.Status.FORBIDDEN).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
            }
            this.calendarManager.deleteSubCalendarOnSpace(subCalendar, param.getViewingSpaceKey());
        } else {
            this.calendarManager.hideSubCalendar(subCalendar, AuthenticatedUserThreadLocal.get());
        }
        return this.getSubCalendarsResponse(param.getCalendarContext(), param.getViewingSpaceKey(), null == param.getSubCalendarIds() ? Collections.emptyList() : new ArrayList(Collections2.filter(param.getSubCalendarIds(), (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)subCalendarId)))));
    }

    @Path(value="subcalendars/restrictions")
    @PUT
    @Produces(value={"application/json"})
    public Response restrictSubCalendar(@InjectParam RestrictSubCalendarParam param) {
        Set<String> invalidGroupNames;
        List<String> groupsPermittedToEdit;
        Set<String> invalidUserIds;
        List<String> userIdsPermittedToEdit;
        Set<String> invalidGroupNames2;
        List<String> groupsPermittedToView;
        Set<String> invalidUsersIds;
        String subCalendarId = param.getSubCalendarId();
        if (!this.calendarManager.hasSubCalendar(StringUtils.defaultString(subCalendarId))) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (StringUtils.isNotBlank(subCalendarId) && (param.isUpdateUsersPermittedToView() || param.isUpdateGroupsPermittedToView() || param.isUpdateUsersPermittedToEdit() || param.isUpdateGroupsPermittedToEdit()) && !this.calendarPermissionManager.hasAdminSubCalendarPrivilege(persistedSubCalendar, AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        LinkedHashMap<String, List<String>> fieldErrors = new LinkedHashMap<String, List<String>>();
        List<String> userIdsPermittedToView = param.getUserIdsPermittedToView();
        if (null != userIdsPermittedToView && !(invalidUsersIds = this.getInvalidUserIds(userIdsPermittedToView)).isEmpty()) {
            this.addFieldError(fieldErrors, "permissions", this.getText("calendar3.error.invalidusernames", Arrays.asList(StringUtils.join(invalidUsersIds, ", "))));
        }
        if (null != (groupsPermittedToView = param.getGroupsPermittedToView()) && !(invalidGroupNames2 = this.getInvalidGroupNames(groupsPermittedToView)).isEmpty()) {
            this.addFieldError(fieldErrors, "permissions", this.getText("calendar3.error.invalidgroupnames", Arrays.asList(StringUtils.join(invalidGroupNames2, ", "))));
        }
        if (null != (userIdsPermittedToEdit = param.getUserIdsPermittedToEdit()) && !(invalidUserIds = this.getInvalidUserIds(userIdsPermittedToEdit)).isEmpty()) {
            this.addFieldError(fieldErrors, "permissions", this.getText("calendar3.error.invalidusernames", Arrays.asList(StringUtils.join(invalidUserIds, ", "))));
        }
        if (null != (groupsPermittedToEdit = param.getGroupsPermittedToEdit()) && !(invalidGroupNames = this.getInvalidGroupNames(groupsPermittedToEdit)).isEmpty()) {
            this.addFieldError(fieldErrors, "permissions", this.getText("calendar3.error.invalidgroupnames", Arrays.asList(StringUtils.join(invalidGroupNames, ", "))));
        }
        if (fieldErrors.isEmpty()) {
            if (param.isUpdateUsersPermittedToView()) {
                this.calendarPermissionManager.restrictEventViewToUsers(persistedSubCalendar, null == userIdsPermittedToView ? null : this.getIdsAsUsers(Sets.newHashSet(userIdsPermittedToView)));
            }
            if (param.isUpdateGroupsPermittedToView()) {
                this.calendarPermissionManager.restrictEventViewToGroups(persistedSubCalendar, (Set<String>)(null == groupsPermittedToView ? null : new HashSet<String>(groupsPermittedToView)));
            }
            if (param.isUpdateUsersPermittedToEdit()) {
                this.calendarPermissionManager.restrictEventEditToUsers(persistedSubCalendar, null == userIdsPermittedToEdit ? null : this.getIdsAsUsers(Sets.newHashSet(userIdsPermittedToEdit)));
            }
            if (param.isUpdateGroupsPermittedToEdit()) {
                this.calendarPermissionManager.restrictEventEditToGroups(persistedSubCalendar, (Set<String>)(null == groupsPermittedToEdit ? null : new HashSet<String>(groupsPermittedToEdit)));
            }
            return this.getSubCalendarsResponse(param.getCalendarContext(), param.getViewingSpaceKey(), param.getSubCalendarIncludes());
        }
        return this.createErrorResponse(fieldErrors);
    }

    @Path(value="subcalendars")
    @PUT
    @Produces(value={"application/json"})
    public Response updateSubCalendar(@InjectParam UpdateCalendarParam param) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        String subCalendarId = param.getSubCalendarId();
        Response response = this.updateSubCalendarInternal(StringUtils.isNotEmpty(param.getParentSubCalendarId()) ? this.calendarManager.getSubCalendar(param.getParentSubCalendarId()) : null, param.getType(), subCalendarId, param.getName(), param.getDescription(), param.getColor(), param.getSpaceKey(), param.getTimeZoneId(), param.getLocation(), param.getUserName(), param.getPassword(), StringUtils.isBlank(subCalendarId) && this.userAccessor.getConfluenceUserPreferences((User)currentUser).isWatchingOwnContent(), param.getSubCalendarIncludes(), false, param.getCalendarContext(), param.getViewingSpaceKey());
        if (param.getType().equalsIgnoreCase("subscription")) {
            this.eventPublisher.publish((Object)new SubCalendarAddedSubscription(this, currentUser, StringUtils.isNotBlank(param.getSpaceKey())));
        } else {
            this.eventPublisher.publish((Object)new SubCalendarAddedNew(this, currentUser, StringUtils.isNotBlank(param.getSpaceKey())));
        }
        LOGGER.info("Calendar with name [{}[ for user [{}] has been create successfully", (Object)param.getName(), (Object)param.getUserName());
        return response;
    }

    private Response updateSubCalendarInternal(PersistedSubCalendar parentSubCalendar, String type, String subCalendarId, String name, String description, String color, String spaceKey, String timeZoneId, String location, String userName, String password, boolean addWatch, List<String> subCalendarIncludes, boolean isChangeColor, String calendarContext, String viewingSpaceKey) {
        LinkedHashMap<String, List<String>> fieldErrors = new LinkedHashMap<String, List<String>>();
        if (StringUtils.isNotBlank(subCalendarId) && !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        if (StringUtils.isBlank(color)) {
            color = this.calendarManager.getRandomCalendarColor(new String[0]);
        }
        if (null == this.calendarManager.getSubCalendarColorAsHexValue(StringUtils.defaultString(color))) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.invalidfield", Arrays.asList("color"))).toString()).build());
        }
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        boolean isBadRequest = false;
        if (parentSubCalendar != null) {
            isBadRequest = !this.calendarPermissionManager.hasEditEventPrivilege(parentSubCalendar, confluenceUser);
        } else if (!StringUtils.isBlank(subCalendarId)) {
            boolean bl = isBadRequest = !this.calendarPermissionManager.hasAdminSubCalendarPrivilege(this.calendarManager.getSubCalendar(subCalendarId), confluenceUser);
        }
        if (!this.calendarPermissionManager.hasEditSubCalendarPrivilege(confluenceUser) || isBadRequest) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        if (StringUtils.isBlank(name)) {
            this.addFieldError(fieldErrors, "name", this.getText("calendar3.error.blank"));
        }
        try {
            String eventType;
            PersistedSubCalendar sourceSubCalendar;
            SubCalendar subCalendar;
            SubCalendar subCalendar2 = subCalendar = StringUtils.isBlank(subCalendarId) ? new SubCalendar() : (PersistedSubCalendar)this.calendarManager.getSubCalendar(subCalendarId).clone();
            if (!isChangeColor && subCalendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar && CalendarUtil.isJiraSubCalendarType((sourceSubCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)subCalendar).getSourceSubCalendar()).getType())) {
                subCalendar = sourceSubCalendar;
                parentSubCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)parentSubCalendar).getSourceSubCalendar();
            }
            subCalendar.setType(type);
            subCalendar.setParent(parentSubCalendar);
            subCalendar.setSpaceKey(spaceKey);
            subCalendar.setName(name);
            subCalendar.setDescription(description);
            subCalendar.setColor(color);
            subCalendar.setTimeZoneId(timeZoneId);
            subCalendar.setSourceLocation(StringUtils.trim(location));
            subCalendar.setUserName(userName);
            subCalendar.setPassword(password);
            this.calendarManager.validateSubCalendar(subCalendar, fieldErrors);
            if (!fieldErrors.isEmpty()) {
                return this.createErrorResponse(fieldErrors);
            }
            PersistedSubCalendar updatedSubCalendar = this.calendarManager.save(subCalendar);
            this.calendarManager.unhideEventsOfSubCalendar(updatedSubCalendar, confluenceUser);
            if (addWatch && updatedSubCalendar.isWatchable()) {
                this.calendarManager.watchSubCalendar(updatedSubCalendar, confluenceUser);
            }
            if (StringUtils.isNotBlank(eventType = CalendarUtil.getEventTypeFromStoreKey(updatedSubCalendar.getStoreKey())) && CalendarUtil.isJiraEventType(eventType) && this.hasPeriodReminderForEvent(parentSubCalendar, eventType)) {
                ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
                this.eventPublisher.publish((Object)new SubCalendarCreatedOnJiraEventCreation((Object)updatedSubCalendar, currentUser, parentSubCalendar));
            }
            JSONObject responseJson = new SubCalendarsResponseEntity(new ArrayList<SubCalendarsResponseEntity.ExtendedSubCalendar>(this.getSubcalendarsInternal(calendarContext, viewingSpaceKey, subCalendarIncludes)), updatedSubCalendar.getId()).toJson();
            return Response.ok((Object)responseJson.toString()).build();
        }
        catch (UnsupportedOperationException e) {
            this.addFieldError(fieldErrors, "location", this.getText("calendar3.error.unsupportedsubcalendarlocation"));
            return this.createErrorResponse(fieldErrors);
        }
        catch (CalendarException e) {
            return this.getResponseError(e, String.format("Unable to save/update sub-calendar %s", name), this.getText(e.getErrorMessageKey(), e.getErrorMessageSubstitutions()));
        }
        catch (Exception e) {
            return this.getResponseError(e, String.format("Unable to save/update sub-calendar %s", name), StringUtils.isBlank(e.getMessage()) ? ExceptionUtils.getStackTrace(e) : e.getMessage());
        }
    }

    private boolean hasPeriodReminderForEvent(PersistedSubCalendar parentSubCalendar, String eventType) {
        Set<EventTypeReminder> eventTypeReminders = parentSubCalendar.getEventTypeReminders();
        for (EventTypeReminder eventTypeReminder : eventTypeReminders) {
            if (!eventTypeReminder.getEventTypeId().equals(eventType)) continue;
            return true;
        }
        return false;
    }

    private Set<String> getInvalidGroupNames(Collection<String> groupNames) {
        return new HashSet<String>(Collections2.filter(groupNames, groupName -> null == this.userAccessor.getGroup(groupName)));
    }

    private Set<String> getInvalidUserIds(Collection<String> userIds) {
        return Sets.newHashSet((Iterable)Collections2.filter(userIds, userId -> this.getUserById((String)userId) == null));
    }

    private void addFieldError(Map<String, List<String>> fieldErrors, String field, String msg) {
        List<String> errorMessages;
        if (fieldErrors.containsKey(field)) {
            errorMessages = fieldErrors.get(field);
        } else {
            errorMessages = new ArrayList<String>();
            fieldErrors.put(field, errorMessages);
        }
        msg = GeneralUtil.htmlEncode((String)msg);
        if (!errorMessages.contains(msg)) {
            errorMessages.add(msg);
        }
    }

    @Path(value="subcalendars")
    @DELETE
    @Produces(value={"application/json"})
    public Response deleteSubCalendar(@InjectParam DeleteSubCalendarParam param) {
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar persistedSubCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (persistedSubCalendar instanceof SubscribingSubCalendar ? !this.calendarPermissionManager.hasEditSubCalendarPrivilege(AuthenticatedUserThreadLocal.get()) : !this.calendarPermissionManager.hasDeleteSubCalendarPrivilege(persistedSubCalendar, AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        this.calendarManager.removeSubCalendar(persistedSubCalendar);
        return this.getSubCalendarsResponse(param.getCalendarContext(), param.getViewingSpaceKey(), param.getSubCalendarIdIncludes());
    }

    @Path(value="subcalendars/color")
    @PUT
    @Produces(value={"application/json"})
    public Response updateSubCalendarColor(@InjectParam UpdateSubCalendarColorParam param) {
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        return this.updateSubCalendarInternal(subCalendar.getParent(), subCalendar.getType(), subCalendarId, subCalendar.getName(), subCalendar.getDescription(), param.getColor(), subCalendar.getSpaceKey(), subCalendar.getTimeZoneId(), subCalendar.getSourceLocation(), subCalendar.getUserName(), subCalendar.getPassword(), false, param.getSubCalendarIncludes(), true, param.getCalendarContext(), param.getViewingSpaceKey());
    }

    @Path(value="subcalendar/space")
    @POST
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public Response updateCalendarSpace(UpdateCalendarSpaceParam param) {
        String subCalendarId = param.getCalendarId();
        String spaceKey = param.getSpaceKey();
        Optional<PersistedSubCalendar> persistedSubCalendar = this.calendarManager.getPersistedSubCalendar(subCalendarId);
        if (StringUtils.isBlank(subCalendarId) || !persistedSubCalendar.isPresent()) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.calendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar subCalendar = (PersistedSubCalendar)persistedSubCalendar.get();
        return this.updateSubCalendarInternal(subCalendar.getParent(), subCalendar.getType(), subCalendarId, subCalendar.getName(), subCalendar.getDescription(), subCalendar.getColor(), spaceKey, subCalendar.getTimeZoneId(), subCalendar.getSourceLocation(), subCalendar.getUserName(), subCalendar.getPassword(), false, Arrays.asList(subCalendarId), true, null, null);
    }

    @AnonymousSiteAccess
    @PUT
    @Path(value="subcalendars/admin/refresh")
    @Produces(value={"application/json"})
    public Response refreshSubCalendar(@InjectParam RefreshSubCalendarParam param) {
        String subCalendarId = param.getSubCalendarId();
        if (StringUtils.isBlank(subCalendarId) || !this.calendarManager.hasSubCalendar(subCalendarId)) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.subcalendarnotfound", Arrays.asList(subCalendarId))).toString()).build());
        }
        PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (!this.calendarPermissionManager.hasReloadEventsPrivilege(subCalendar, AuthenticatedUserThreadLocal.get())) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).header("Content-Type", (Object)"application/json").entity((Object)this.toJsonArray(this.getText("calendar3.error.notpermitted")).toString()).build());
        }
        this.calendarManager.refresh(subCalendar);
        return Response.ok((Object)new GeneralResponseEntity().toJson().toString()).build();
    }

    @Path(value="subcalendar/privateurl/{subCalendarId}")
    @GET
    @Produces(value={"text/text"})
    public Response privateUrlForCalendar(@PathParam(value="subCalendarId") String subCalendarId) {
        PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarId);
        if (subCalendar == null || !this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        String token = this.privateCalendarUrlManager.getTokenFor(AuthenticatedUserThreadLocal.get(), subCalendarId);
        if (token == null) {
            LOG.error(String.format("Unable to generate private token for sub-calendar %s", subCalendarId));
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok((Object)(this.settingsManager.getGlobalSettings().getBaseUrl() + "/rest/calendar-services/1.0/calendar/export/subcalendar/private/" + token + ".ics")).build();
    }

    @Path(value="/subcalendar/privateurl/reset/{subCalendarId}")
    @PUT
    @Produces(value={"text/plain"})
    public Response resetPrivateUrls(@PathParam(value="subCalendarId") String subCalendarId) {
        this.privateCalendarUrlManager.resetPrivateUrlsFor(AuthenticatedUserThreadLocal.get(), subCalendarId);
        return Response.ok().build();
    }

    @Path(value="/subcalendar/privateurl/resetall")
    @PUT
    @Produces(value={"text/plain"})
    public Response resetAllPrivateUrls() {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        this.privateCalendarUrlManager.resetAllPrivateUrls();
        return Response.ok().build();
    }

    @Path(value="haspopular")
    @GET
    @Produces(value={"application/json"})
    public Response hasPopularSubCalendarSubscriptions() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        boolean hasPopular = this.subCalendarSubscriptionStatisticsAccessor.hasPopularSubscriptions(currentUser);
        return Response.ok((Object)hasPopular).build();
    }

    @Path(value="subcalendars/subscribe")
    @PUT
    @Produces(value={"application/json"})
    public Response addSubCalendarSubscription(@InjectParam AddSubCalendarSubscriptionParam param) {
        List<String> subCalendarIds = param.getSubCalendarIds();
        if (null != subCalendarIds) {
            ArrayList<String> subCalendarColors = new ArrayList<String>(this.calendarManager.getAvailableSubCalendarColorCssClasses());
            String currentColor = param.getColor();
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            boolean onSpace = CalendarRenderer.CalendarContext.spaceCalendars.getValue().equals(param.getCalendarContext());
            Collection subCalendarIdsInView = Collections2.filter((Collection)Collections2.transform(onSpace ? this.calendarManager.getSubCalendarsOnSpace(param.getViewingSpaceKey()) : this.calendarManager.getSubCalendarsInView(AuthenticatedUserThreadLocal.get()), subCalendarId -> {
                PersistedSubCalendar subCalendarInView = this.calendarManager.getSubCalendar((String)subCalendarId);
                if (null == subCalendarInView) {
                    return null;
                }
                return subCalendarInView instanceof SubscribingSubCalendar ? ((SubscribingSubCalendar)subCalendarInView).getSubscriptionId() : subCalendarInView.getId();
            }), (Predicate)Predicates.notNull());
            for (PersistedSubCalendar toSubscribe : Collections2.filter((Collection)Collections2.transform(subCalendarIds, this.calendarManager::getSubCalendar), (Predicate)Predicates.and((Predicate)Predicates.notNull(), persistedSubCalendar -> !subCalendarIdsInView.contains(persistedSubCalendar.getId()) && this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)persistedSubCalendar, AuthenticatedUserThreadLocal.get())))) {
                if (StringUtils.equals(toSubscribe.getCreator(), currentUser.getKey().toString()) && !onSpace) {
                    HashSet<String> subCalendarsInView = new HashSet<String>(this.calendarManager.getSubCalendarsInView(currentUser));
                    subCalendarsInView.add(toSubscribe.getId());
                    UserCalendarPreference userCalendarPreference = this.calendarManager.getUserPreference(currentUser);
                    userCalendarPreference.setSubCalendarsInView(subCalendarsInView);
                    this.calendarManager.setUserPreference(currentUser, userCalendarPreference);
                    if (param.isAddWatch()) {
                        this.calendarManager.watchSubCalendar(toSubscribe, currentUser);
                    }
                } else {
                    String spaceKeySubscribe = null;
                    if (StringUtils.isNotEmpty(toSubscribe.getSpaceKey()) && this.spaceManager.getSpace(toSubscribe.getSpaceKey()) != null) {
                        spaceKeySubscribe = toSubscribe.getSpaceKey();
                    }
                    this.updateSubCalendarInternal(null, "internal-subscription", null, toSubscribe.getName(), toSubscribe.getDescription(), currentColor, onSpace ? param.getViewingSpaceKey() : spaceKeySubscribe, null, "subscription://" + toSubscribe.getId(), null, null, param.isAddWatch(), Collections.emptyList(), false, param.getCalendarContext(), param.getViewingSpaceKey());
                }
                currentColor = this.getNextSubCalendarColor(currentColor, subCalendarColors);
                this.eventPublisher.publish((Object)new SubCalendarInternalSubscribed(toSubscribe, currentUser, onSpace));
            }
        }
        return this.getSubCalendarsResponse(param.getCalendarContext(), param.getViewingSpaceKey(), param.getSubCalendarIncludes());
    }

    @Path(value="subcalendars/links")
    @GET
    @Produces(value={"application/json"})
    public Response getContentLinkingToSpaceSubCalendar(@QueryParam(value="subCalendarId") String subCalendarId) {
        return Response.ok((Object)new SubscriptionCountResponseEntity(!this.calendarManager.hasSubCalendar(subCalendarId) ? 0 : this.subCalendarSubscriptionStatisticsAccessor.getSubscriberCount(this.calendarManager.getSubCalendar(subCalendarId))).toJson().toString()).build();
    }

    @Path(value="render")
    @GET
    public Response getCalendarRenderHTML(@QueryParam(value="calendarContext") CalendarRenderer.CalendarContext calendarContext) {
        Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> calendarParams = this.calendarRenderer.newRenderParamsBuilder().calendarContext(calendarContext).build();
        return Response.ok((Object)this.calendarRenderer.render(calendarParams)).build();
    }

    @Path(value="render/space")
    @GET
    @Produces(value={"application/json"})
    public Response getSpaceCalendarRenderHTML(@QueryParam(value="spaceKey") String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        LinkedHashMap<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("spaceKey", spaceKey);
        Map<CalendarRenderer.RenderParamsBuilder.ParamName, Object> calendarParams = this.calendarRenderer.newRenderParamsBuilder().calendarContext(CalendarRenderer.CalendarContext.spaceCalendars).build();
        long numberOfCalendars = this.calendarManager.countSubCalendarsOnSpace(spaceKey);
        response.put("numberOfCalendars", numberOfCalendars);
        response.put("licenseMessages", calendarParams.get((Object)CalendarRenderer.RenderParamsBuilder.ParamName.licenseMessages));
        CustomContentEntityObject cceo = this.calendarContentTypeManager.loadCalendarContentBySpaceKey(spaceKey);
        if (cceo == null) {
            this.calendarContentTypeManager.createCalendarContentTypeFor(space);
            cceo = this.calendarContentTypeManager.loadCalendarContentBySpaceKey(space.getKey());
        }
        calendarParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.contentId, cceo.getIdAsString());
        calendarParams.put(CalendarRenderer.RenderParamsBuilder.ParamName.enableShareCalendar, false);
        response.put("html", this.calendarRenderer.render(calendarParams));
        return Response.ok(response).build();
    }

    public static class UpdateCalendarSpaceParam {
        @XmlElement
        String calendarId;
        @XmlElement
        String spaceKey;

        public String getCalendarId() {
            return this.calendarId;
        }

        public void setCalendarId(String calendarId) {
            this.calendarId = calendarId;
        }

        public String getSpaceKey() {
            return this.spaceKey;
        }

        public void setSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
        }
    }
}

