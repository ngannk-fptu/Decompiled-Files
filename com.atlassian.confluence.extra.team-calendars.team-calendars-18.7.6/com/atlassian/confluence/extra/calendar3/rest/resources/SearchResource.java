/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.transaction.TransactionInterceptor
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.calendar3.rest.resources;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.SubCalendarSubscriptionStatisticsAccessor;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.Interceptors.TeamCalResourceLoggingInterceptor;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarListResponseEntity;
import com.atlassian.confluence.extra.calendar3.rest.SubCalendarsResponseEntity;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.transaction.TransactionInterceptor;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="calendar/search")
@InterceptorChain(value={TransactionInterceptor.class, TeamCalResourceLoggingInterceptor.class})
public class SearchResource
extends AbstractResource {
    private final SettingsManager settingsManager;
    private final CalendarManager calendarManager;
    private final CalendarPermissionManager calendarPermissionManager;
    private final SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor;
    private final CacheFactory cacheFactory;

    public SearchResource(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarManager calendarManager, CalendarPermissionManager calendarPermissionManager, UserAccessor userAccessor, SettingsManager settingsManager, SubCalendarSubscriptionStatisticsAccessor subCalendarSubscriptionStatisticsAccessor, @Qualifier(value="cacheFactory") CacheFactory cacheFactory) {
        super(i18NBeanFactory, localeManager, calendarManager, calendarPermissionManager, userAccessor);
        this.settingsManager = settingsManager;
        this.calendarManager = calendarManager;
        this.calendarPermissionManager = calendarPermissionManager;
        this.subCalendarSubscriptionStatisticsAccessor = subCalendarSubscriptionStatisticsAccessor;
        this.cacheFactory = cacheFactory;
    }

    @Path(value="subcalendars/all")
    @GET
    @Produces(value={"application/json"})
    public Response getAllSubCalendars() {
        LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar> subCalendars = new LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar>();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        for (SubCalendarSummary subCalendarSummary : this.calendarManager.getAllSubCalendars(currentUser)) {
            PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarSummary.getId());
            if (!this.shouldSubCalendarBeVisibleInSearchResult(subCalendar, currentUser, false)) continue;
            subCalendars.add(new SubCalendarsResponseEntity.ExtendedSubCalendar(subCalendar, this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasReloadEventsPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasEditSubCalendarPrivilege(currentUser), this.calendarPermissionManager.hasEditEventPrivilege(subCalendar, currentUser), false, false, false, 0, false, this.calendarManager.isEventsOfSubCalendarHidden(subCalendar, currentUser), this.calendarPermissionManager.hasDeleteSubCalendarPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasAdminSubCalendarPrivilege(subCalendar, currentUser), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventViewUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventViewGroupRestrictions(subCalendar), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventEditUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventEditGroupRestrictions(subCalendar), Collections.emptySet(), false));
        }
        return Response.ok((Object)new SubCalendarsResponseEntity(new ArrayList<SubCalendarsResponseEntity.ExtendedSubCalendar>(subCalendars)).toJson().toString()).build();
    }

    @Path(value="subcalendars/bulk")
    @GET
    @Produces(value={"application/json"})
    public Response getBulkSubCalendars(@QueryParam(value="startIndex") @DefaultValue(value="0") int startIndex, @QueryParam(value="pageSize") @DefaultValue(value="100") int pageSize, @QueryParam(value="space") @DefaultValue(value="") String spaceKey) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.userAccessor.isSuperUser((User)currentUser)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return Response.ok((Object)new SubCalendarListResponseEntity(this.calendarManager.getAllSubCalendarIds(spaceKey, startIndex, pageSize)).toJson().toString()).build();
    }

    @Path(value="subcalendars")
    @GET
    @Produces(value={"application/json"})
    public Response getSubCalendarsMatchingQuery(@QueryParam(value="term") String term, @QueryParam(value="startIndex") @DefaultValue(value="0") int startIndex, @QueryParam(value="pageSize") @DefaultValue(value="10") int pageSize, @QueryParam(value="showSubscriberCount") @DefaultValue(value="false") boolean showSubscriberCount, @QueryParam(value="showSubCalendarsInView") @DefaultValue(value="false") boolean showSubCalendarsInView) throws InvalidSearchException {
        LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar> subCalendars = new LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar>();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        for (SubCalendarSummary subCalendarSummary : this.calendarManager.findSubCalendars(term, startIndex, pageSize, currentUser)) {
            PersistedSubCalendar subCalendar = this.calendarManager.getSubCalendar(subCalendarSummary.getId());
            if (this.shouldSubCalendarBeVisibleInSearchResult(subCalendar, currentUser, showSubCalendarsInView)) {
                subCalendars.add(new SubCalendarsResponseEntity.ExtendedSubCalendar(subCalendar, this.calendarPermissionManager.hasViewEventPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasReloadEventsPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasEditSubCalendarPrivilege(currentUser), this.calendarPermissionManager.hasEditEventPrivilege(subCalendar, currentUser), false, false, false, showSubscriberCount ? this.subCalendarSubscriptionStatisticsAccessor.getSubscriberCount(subCalendar) : 0, false, this.calendarManager.isEventsOfSubCalendarHidden(subCalendar, currentUser), this.calendarPermissionManager.hasDeleteSubCalendarPrivilege(subCalendar, currentUser), this.calendarPermissionManager.hasAdminSubCalendarPrivilege(subCalendar, currentUser), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventViewUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventViewGroupRestrictions(subCalendar), new HashSet<SubCalendarsResponseEntity.ExtendedSubCalendar.PermittedUser>(Collections2.transform(this.calendarPermissionManager.getEventEditUserRestrictions(subCalendar), (Function)new AbstractResource.UserToPermittedUserTransformer(this.userAccessor, this.settingsManager, this.cacheFactory))), this.calendarPermissionManager.getEventEditGroupRestrictions(subCalendar), Collections.emptySet(), false));
            }
            if (subCalendars.size() < pageSize) continue;
            break;
        }
        return Response.ok((Object)new SubCalendarsResponseEntity(new ArrayList<SubCalendarsResponseEntity.ExtendedSubCalendar>(subCalendars)).toJson().toString()).build();
    }

    @Path(value="subcalendars/popular")
    @GET
    @Produces(value={"application/json"})
    public Response getPopularSubCalendarSubscriptions(@QueryParam(value="startIndex") @DefaultValue(value="0") int startIndex, @QueryParam(value="pageSize") @DefaultValue(value="10") int pageSize) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar> subCalendars = new LinkedHashSet<SubCalendarsResponseEntity.ExtendedSubCalendar>();
        for (SubCalendarSubscriptionStatisticsAccessor.PopularSubCalendarSubscription popularSubCalendarSubscription : this.subCalendarSubscriptionStatisticsAccessor.getPopularSubscriptions(currentUser, startIndex, pageSize)) {
            PersistedSubCalendar subCalendar = popularSubCalendarSubscription.getSubCalendar();
            subCalendars.add(new SubCalendarsResponseEntity.ExtendedSubCalendar(subCalendar, true, true, true, true, false, false, false, popularSubCalendarSubscription.getSubscribeCount(), true, false, true, true, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), false));
        }
        return Response.ok((Object)new SubCalendarsResponseEntity(new ArrayList<SubCalendarsResponseEntity.ExtendedSubCalendar>(subCalendars)).toJson().toString()).build();
    }

    private boolean shouldSubCalendarBeVisibleInSearchResult(PersistedSubCalendar subCalendar, ConfluenceUser currentUser, boolean showSubCalendarsInView) {
        return showSubCalendarsInView || !this.calendarManager.getSubCalendarsInView(AuthenticatedUserThreadLocal.get()).contains(subCalendar.getId()) && !this.subCalendarSubscriptionStatisticsAccessor.getUsersSubscribingToSubCalendar(subCalendar, false).contains(currentUser);
    }
}

