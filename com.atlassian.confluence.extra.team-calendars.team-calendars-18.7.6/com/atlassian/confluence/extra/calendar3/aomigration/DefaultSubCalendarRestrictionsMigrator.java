/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  net.java.ao.DBParam
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.calendar3.aomigration.BandanaSubCalendarsProvider;
import com.atlassian.confluence.extra.calendar3.aomigration.SubCalendarRestrictionsMigrator;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.upgrade.task.aomigration.BandanaContextProvider;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.java.ao.DBParam;

public class DefaultSubCalendarRestrictionsMigrator
implements SubCalendarRestrictionsMigrator {
    public static final String VIEW_EVENTS_USERS = "__view_events_users__";
    public static final String EDIT_EVENTS_USERS = "__edit_events_users__";
    public static final String VIEW_EVENTS_GROUPS = "__view_events_groups__";
    public static final String EDIT_EVENTS_GROUPS = "__edit_events_groups__";
    private final UserAccessor userAccessor;
    private final BandanaContextProvider bandanaContextProvider;
    private final BandanaManager bandanaManager;

    public DefaultSubCalendarRestrictionsMigrator(UserAccessor userAccessor, BandanaContextProvider bandanaContextProvider, BandanaManager bandanaManager) {
        this.userAccessor = userAccessor;
        this.bandanaContextProvider = bandanaContextProvider;
        this.bandanaManager = bandanaManager;
    }

    @Override
    public void migrateRestrictions(ActiveObjects activeObjects, BandanaSubCalendarsProvider provider, String sourceSubCalendarId, String destinationSubCalendarId) {
        this.migrateSubCalendarUserViewRestrictions(activeObjects, provider, sourceSubCalendarId, destinationSubCalendarId);
        this.migrateSubCalendarUserEditRestrictions(activeObjects, provider, sourceSubCalendarId, destinationSubCalendarId);
        this.migrateSubCalendarGroupViewRestrictions(activeObjects, provider, sourceSubCalendarId, destinationSubCalendarId);
        this.migrateSubCalendarGroupEditRestrictions(activeObjects, provider, sourceSubCalendarId, destinationSubCalendarId);
    }

    private void migrateSubCalendarUserViewRestrictions(ActiveObjects activeObjects, BandanaSubCalendarsProvider provider, String sourceSubCalendarId, String destinationSubCalendarId) {
        Set<ConfluenceUser> users = this.getValidUsers(this.getPrivileged(sourceSubCalendarId, VIEW_EVENTS_USERS, provider));
        this.createUserRestrictions(activeObjects, users, destinationSubCalendarId, "VIEW");
    }

    private void migrateSubCalendarUserEditRestrictions(ActiveObjects activeObjects, BandanaSubCalendarsProvider provider, String sourceSubCalendarId, String destinationSubCalendarId) {
        Set<ConfluenceUser> users = this.getValidUsers(this.getPrivileged(sourceSubCalendarId, EDIT_EVENTS_USERS, provider));
        this.createUserRestrictions(activeObjects, users, destinationSubCalendarId, "EDIT");
    }

    private void migrateSubCalendarGroupViewRestrictions(ActiveObjects activeObjects, BandanaSubCalendarsProvider provider, String sourceSubCalendarId, String destinationSubCalendarId) {
        Set<String> groups = this.getValidGroupNames(this.getPrivileged(sourceSubCalendarId, VIEW_EVENTS_GROUPS, provider));
        this.createGroupRestrictions(activeObjects, groups, destinationSubCalendarId, "VIEW");
    }

    private void migrateSubCalendarGroupEditRestrictions(ActiveObjects activeObjects, BandanaSubCalendarsProvider provider, String sourceSubCalendarId, String destinationSubCalendarId) {
        Set<String> groups = this.getValidGroupNames(this.getPrivileged(sourceSubCalendarId, EDIT_EVENTS_GROUPS, provider));
        this.createGroupRestrictions(activeObjects, groups, destinationSubCalendarId, "EDIT");
    }

    private Set<ConfluenceUser> getValidUsers(Set<String> userIds) {
        return Sets.newHashSet((Iterable)Collections2.filter((Collection)Collections2.transform(userIds, userId -> this.userAccessor.getUserByKey(new UserKey(userId))), (Predicate)Predicates.notNull()));
    }

    private Set<String> getValidGroupNames(Set<String> groupNames) {
        return Sets.newHashSet((Iterable)Collections2.filter(groupNames, (Predicate)Predicates.and((Predicate)Predicates.notNull(), groupName -> this.userAccessor.getGroup(groupName) != null)));
    }

    private Set<String> getPrivileged(String subCalendarId, String privilege, BandanaSubCalendarsProvider provider) {
        Set privileged = (Set)this.bandanaManager.getValue(this.bandanaContextProvider.getSubCalendarContext(provider, subCalendarId), privilege.toString());
        return null == privileged ? Collections.emptySet() : privileged;
    }

    private void createGroupRestrictions(ActiveObjects activeObjects, Set<String> groupNames, String subCalendarId, String restrictionType) {
        for (String groupName : groupNames) {
            activeObjects.create(SubCalendarGroupRestrictionEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarId), new DBParam("TYPE", (Object)restrictionType), new DBParam("GROUP_NAME", (Object)groupName)});
        }
    }

    private void createUserRestrictions(ActiveObjects activeObjects, Set<ConfluenceUser> users, String subCalendarId, String restrictionType) {
        for (ConfluenceUser privilegedUser : users) {
            activeObjects.create(SubCalendarUserRestrictionEntity.class, new DBParam[]{new DBParam("SUB_CALENDAR_ID", (Object)subCalendarId), new DBParam("TYPE", (Object)restrictionType), new DBParam("USER_KEY", (Object)privilegedUser.getKey().toString())});
        }
    }
}

