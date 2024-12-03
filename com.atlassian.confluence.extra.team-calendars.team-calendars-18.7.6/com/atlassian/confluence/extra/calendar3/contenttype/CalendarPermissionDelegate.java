/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionDelegate
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 *  com.google.common.base.Optional
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.contenttype;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentEntityAdapter;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.wrapper.SpacePermissionsManagerWrapper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarPermissionDelegate
implements PermissionDelegate {
    private final CalendarPermissionManager calendarPermissionManager;
    private final CalendarContentEntityAdapter calendarContentEntityAdapter;
    private final CalendarManager calendarManager;
    private final UserAccessor userAccessor;
    private final SpacePermissionsManagerWrapper spacePermissionsManagerWrapper;
    private final PermissionManager permissionManager;

    @Autowired
    public CalendarPermissionDelegate(CalendarPermissionManager calendarPermissionManager, CalendarContentEntityAdapter calendarContentEntityAdapter, CalendarManager calendarManager, UserAccessor userAccessor, SpacePermissionsManagerWrapper spacePermissionsManagerWrapper, PermissionManager permissionManager) {
        this.calendarPermissionManager = calendarPermissionManager;
        this.calendarContentEntityAdapter = calendarContentEntityAdapter;
        this.calendarManager = calendarManager;
        this.userAccessor = userAccessor;
        this.spacePermissionsManagerWrapper = spacePermissionsManagerWrapper;
        this.permissionManager = permissionManager;
    }

    private Optional<PersistedSubCalendar> getPersistedSubcalendarFromCCEO(Object target) {
        if (!(target instanceof CustomContentEntityObject)) {
            return Optional.absent();
        }
        CustomContentEntityObject customContentEntityObject = (CustomContentEntityObject)target;
        String subCalendarId = this.calendarContentEntityAdapter.getSubCalendarId(customContentEntityObject);
        return this.calendarManager.getPersistedSubCalendar(subCalendarId);
    }

    private ConfluenceUser getConfluenceUserFromUser(User user) {
        ConfluenceUser confluenceUser = null;
        if (user != null) {
            confluenceUser = user instanceof ConfluenceUser ? (ConfluenceUser)user : this.userAccessor.getUserByName(user.getName());
        }
        return confluenceUser;
    }

    public boolean canView(User user, Object target) {
        Optional<PersistedSubCalendar> persistedSubCalendarOptional = this.getPersistedSubcalendarFromCCEO(target);
        return (Boolean)persistedSubCalendarOptional.transform(persistedSubCalendar -> this.calendarPermissionManager.hasViewEventPrivilege((PersistedSubCalendar)persistedSubCalendar, this.getConfluenceUserFromUser(user))).or((Object)false);
    }

    public boolean canView(User user) {
        return this.spacePermissionsManagerWrapper.getUseConfluencePermission(this.getConfluenceUserFromUser(user));
    }

    public boolean canEdit(User user, Object target) {
        Optional<PersistedSubCalendar> persistedSubCalendarOptional = this.getPersistedSubcalendarFromCCEO(target);
        return (Boolean)persistedSubCalendarOptional.transform(persistedSubCalendar -> this.calendarPermissionManager.hasEditEventPrivilege((PersistedSubCalendar)persistedSubCalendar, this.getConfluenceUserFromUser(user))).or((Object)false);
    }

    public boolean canSetPermissions(User user, Object target) {
        return this.canAdminister(user, target);
    }

    public boolean canRemove(User user, Object target) {
        Optional<PersistedSubCalendar> persistedSubCalendarOptional = this.getPersistedSubcalendarFromCCEO(target);
        return (Boolean)persistedSubCalendarOptional.transform(persistedSubCalendar -> this.calendarPermissionManager.hasDeleteSubCalendarPrivilege((PersistedSubCalendar)persistedSubCalendar, this.getConfluenceUserFromUser(user))).or((Object)false);
    }

    public boolean canExport(User user, Object target) {
        return this.canView(user, target);
    }

    public boolean canAdminister(User user, Object target) {
        Optional<PersistedSubCalendar> persistedSubCalendarOptional = this.getPersistedSubcalendarFromCCEO(target);
        return (Boolean)persistedSubCalendarOptional.transform(persistedSubCalendar -> this.calendarPermissionManager.hasAdminSubCalendarPrivilege((PersistedSubCalendar)persistedSubCalendar, this.getConfluenceUserFromUser(user))).or((Object)false);
    }

    public boolean canCreate(User user, Object container) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, container);
    }

    public boolean canCreateInTarget(User user, Class aClass) {
        throw new UnsupportedOperationException();
    }
}

