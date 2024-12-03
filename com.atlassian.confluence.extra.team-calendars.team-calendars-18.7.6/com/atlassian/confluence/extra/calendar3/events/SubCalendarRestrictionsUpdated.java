/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.events.BaseSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Set;

public class SubCalendarRestrictionsUpdated
extends BaseSubCalendarEvent<PersistedSubCalendar> {
    public SubCalendarRestrictionsUpdated(Object eventSource, ConfluenceUser trigger, PersistedSubCalendar subCalendar) {
        super(eventSource, trigger, subCalendar);
    }

    public static class GroupEditRestrictionsUpdated
    extends SubCalendarRestrictionsUpdated {
        private final Set<String> newGroupsPermittedToEdit;
        private final Set<String> oldGroupsPermittedToEdit;

        public GroupEditRestrictionsUpdated(CalendarPermissionManager eventSource, ConfluenceUser trigger, PersistedSubCalendar persistedSubCalendar, Set<String> newGroupsPermittedToEdit, Set<String> oldGroupsPermittedToEdit) {
            super((Object)eventSource, trigger, persistedSubCalendar);
            this.newGroupsPermittedToEdit = newGroupsPermittedToEdit;
            this.oldGroupsPermittedToEdit = oldGroupsPermittedToEdit;
        }

        public Set<String> getNewGroupsPermittedToEdit() {
            return this.newGroupsPermittedToEdit;
        }

        public Set<String> getOldGroupsPermittedToEdit() {
            return this.oldGroupsPermittedToEdit;
        }
    }

    public static class UserEditRestrictionsUpdated
    extends SubCalendarRestrictionsUpdated {
        private final Set<ConfluenceUser> newUsersPermittedToEdit;
        private final Set<ConfluenceUser> oldUsersPermittedToEdit;

        public UserEditRestrictionsUpdated(CalendarPermissionManager eventSource, ConfluenceUser trigger, PersistedSubCalendar persistedSubCalendar, Set<ConfluenceUser> newUsersPermittedToEdit, Set<ConfluenceUser> oldUsersPermittedToEdit) {
            super((Object)eventSource, trigger, persistedSubCalendar);
            this.newUsersPermittedToEdit = newUsersPermittedToEdit;
            this.oldUsersPermittedToEdit = oldUsersPermittedToEdit;
        }

        public Set<ConfluenceUser> getNewUsersPermittedToEdit() {
            return this.newUsersPermittedToEdit;
        }

        public Set<ConfluenceUser> getOldUsersPermittedToEdit() {
            return this.oldUsersPermittedToEdit;
        }
    }

    public static class GroupViewRestrictionsUpdated
    extends SubCalendarRestrictionsUpdated {
        private final Set<String> newGroupsPermittedToView;
        private final Set<String> oldGroupsPermittedToView;

        public GroupViewRestrictionsUpdated(CalendarPermissionManager eventSource, ConfluenceUser trigger, PersistedSubCalendar persistedSubCalendar, Set<String> newGroupsPermittedToView, Set<String> oldGroupsPermittedToView) {
            super((Object)eventSource, trigger, persistedSubCalendar);
            this.newGroupsPermittedToView = newGroupsPermittedToView;
            this.oldGroupsPermittedToView = oldGroupsPermittedToView;
        }

        public Set<String> getNewGroupsPermittedToView() {
            return this.newGroupsPermittedToView;
        }

        public Set<String> getOldGroupsPermittedToView() {
            return this.oldGroupsPermittedToView;
        }
    }

    public static class UserViewRestrictionsUpdated
    extends SubCalendarRestrictionsUpdated {
        private final Set<ConfluenceUser> newUsersPermittedToView;
        private final Set<ConfluenceUser> oldUsersPermittedToView;

        public UserViewRestrictionsUpdated(CalendarPermissionManager eventSource, ConfluenceUser trigger, PersistedSubCalendar persistedSubCalendar, Set<ConfluenceUser> newUsersPermittedToView, Set<ConfluenceUser> oldUsersPermittedToView) {
            super((Object)eventSource, trigger, persistedSubCalendar);
            this.newUsersPermittedToView = newUsersPermittedToView;
            this.oldUsersPermittedToView = oldUsersPermittedToView;
        }

        public Set<ConfluenceUser> getNewUsersPermittedToView() {
            return this.newUsersPermittedToView;
        }

        public Set<ConfluenceUser> getOldUsersPermittedToView() {
            return this.oldUsersPermittedToView;
        }
    }
}

