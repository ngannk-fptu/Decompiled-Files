/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.calendarstore.CalendarDataStore;
import com.atlassian.confluence.extra.calendar3.calendarstore.RefreshableCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.events.SubCalendarRestrictionsUpdated;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.wrapper.SpacePermissionsManagerWrapper;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DefaultCalendarPermissionManager
implements CalendarPermissionManager {
    private final CalendarDataStore<PersistedSubCalendar> calendarDataStore;
    private final EventPublisher eventPublisher;
    private SpacePermissionsManagerWrapper spacePermissionsManagerWrapper;

    @Autowired
    public DefaultCalendarPermissionManager(@Qualifier(value="calendarDataStore") CalendarDataStore<PersistedSubCalendar> calendarDataStore, @ComponentImport EventPublisher eventPublisher, @Qualifier(value="cachingSpacePermissionManagerWrapper") SpacePermissionsManagerWrapper spacePermissionsManagerWrapper) {
        this.calendarDataStore = calendarDataStore;
        this.eventPublisher = eventPublisher;
        this.spacePermissionsManagerWrapper = spacePermissionsManagerWrapper;
    }

    @Override
    public boolean hasEditSubCalendarPrivilege(ConfluenceUser user) {
        return null != user && this.spacePermissionsManagerWrapper.getUseConfluencePermission(user);
    }

    @Override
    public boolean hasReloadEventsPrivilege(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        return this.calendarDataStore instanceof RefreshableCalendarDataStore && ((RefreshableCalendarDataStore)this.calendarDataStore).hasReloadEventsPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasDeleteSubCalendarPrivilege(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        return this.spacePermissionsManagerWrapper.getUseConfluencePermission(user) && this.calendarDataStore.hasDeletePrivilege(subCalendar, user);
    }

    @Override
    public boolean hasAdminSubCalendarPrivilege(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        return this.calendarDataStore.hasAdminPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasViewEventPrivilege(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        return this.spacePermissionsManagerWrapper.getUseConfluencePermission(user) && this.calendarDataStore.hasViewEventPrivilege(subCalendar, user);
    }

    @Override
    public boolean hasEditEventPrivilege(PersistedSubCalendar subCalendar, ConfluenceUser user) {
        return this.spacePermissionsManagerWrapper.getUseConfluencePermission(user) && this.calendarDataStore.hasEditEventPrivilege(subCalendar, user);
    }

    @Override
    public Set<ConfluenceUser> getEventEditUserRestrictions(PersistedSubCalendar persistedSubCalendar) {
        return this.calendarDataStore.getEventEditUserRestrictions(persistedSubCalendar);
    }

    @Override
    public Set<String> getEventEditGroupRestrictions(PersistedSubCalendar persistedSubCalendar) {
        return this.calendarDataStore.getEventEditGroupRestrictions(persistedSubCalendar);
    }

    @Override
    public Set<ConfluenceUser> getEventViewUserRestrictions(PersistedSubCalendar persistedSubCalendar) {
        return this.calendarDataStore.getEventViewUserRestrictions(persistedSubCalendar);
    }

    @Override
    public Set<String> getEventViewGroupRestrictions(PersistedSubCalendar persistedSubCalendar) {
        return this.calendarDataStore.getEventViewGroupRestrictions(persistedSubCalendar);
    }

    @Override
    public void restrictEventEditToUsers(PersistedSubCalendar persistedSubCalendar, Set<ConfluenceUser> users) {
        SubCalendarRestrictionsUpdated.UserEditRestrictionsUpdated subCalendarRestrictionsUpdatedEvent = new SubCalendarRestrictionsUpdated.UserEditRestrictionsUpdated(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, users, this.getEventEditUserRestrictions(persistedSubCalendar));
        this.calendarDataStore.restrictEventEditToUsers(persistedSubCalendar.getId(), users);
        this.eventPublisher.publish((Object)subCalendarRestrictionsUpdatedEvent);
    }

    @Override
    public void restrictEventEditToGroups(PersistedSubCalendar persistedSubCalendar, Set<String> groupNames) {
        SubCalendarRestrictionsUpdated.GroupEditRestrictionsUpdated subCalendarRestrictionsUpdatedEvent = new SubCalendarRestrictionsUpdated.GroupEditRestrictionsUpdated(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, groupNames, this.getEventEditGroupRestrictions(persistedSubCalendar));
        this.calendarDataStore.restrictEventEditToGroups(persistedSubCalendar.getId(), groupNames);
        this.eventPublisher.publish((Object)subCalendarRestrictionsUpdatedEvent);
    }

    @Override
    public void restrictEventViewToUsers(PersistedSubCalendar persistedSubCalendar, Set<ConfluenceUser> users) {
        SubCalendarRestrictionsUpdated.UserViewRestrictionsUpdated subCalendarRestrictionsUpdatedEvent = new SubCalendarRestrictionsUpdated.UserViewRestrictionsUpdated(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, users, this.getEventViewUserRestrictions(persistedSubCalendar));
        this.calendarDataStore.restrictEventViewToUsers(persistedSubCalendar.getId(), users);
        this.eventPublisher.publish((Object)subCalendarRestrictionsUpdatedEvent);
    }

    @Override
    public void restrictEventViewToGroups(PersistedSubCalendar persistedSubCalendar, Set<String> groupNames) {
        SubCalendarRestrictionsUpdated.GroupViewRestrictionsUpdated subCalendarRestrictionsUpdatedEvent = new SubCalendarRestrictionsUpdated.GroupViewRestrictionsUpdated(this, AuthenticatedUserThreadLocal.get(), persistedSubCalendar, groupNames, this.getEventViewGroupRestrictions(persistedSubCalendar));
        this.calendarDataStore.restrictEventViewToGroups(persistedSubCalendar.getId(), groupNames);
        this.eventPublisher.publish((Object)subCalendarRestrictionsUpdatedEvent);
    }
}

