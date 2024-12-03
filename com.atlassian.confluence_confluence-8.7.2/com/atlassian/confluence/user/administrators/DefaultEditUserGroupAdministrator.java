/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.page.Pager
 *  javax.persistence.PersistenceException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.administrators;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.administrators.EditUserGroupAdministrator;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEditUserGroupAdministrator
implements EditUserGroupAdministrator {
    private static final Logger log = LoggerFactory.getLogger(DefaultEditUserGroupAdministrator.class);
    private final User user;
    private final User remoteUser;
    private List<String> initialMemberGroups;
    private List<String> readOnlyGroups;
    private List<SpacePermission> globalPermissions;
    private List<Message> errors = new ArrayList<Message>();
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;

    public DefaultEditUserGroupAdministrator(User user, User remoteUser, UserAccessor userAccessor, PermissionManager permissionManager, SpacePermissionManager spacePermissionManager) {
        this.user = user;
        this.remoteUser = remoteUser;
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.loadGroups();
    }

    @Override
    public List<String> getInitialMemberGroups() {
        return this.initialMemberGroups;
    }

    private void loadGroups() {
        this.initialMemberGroups = new ArrayList<String>();
        this.readOnlyGroups = new ArrayList<String>();
        Pager groupsForUser = this.userAccessor.getGroups(this.user);
        for (Group group : groupsForUser) {
            if (this.userAccessor.isReadOnly(group)) {
                this.readOnlyGroups.add(group.getName());
                continue;
            }
            this.initialMemberGroups.add(group.getName());
        }
    }

    @Override
    public List<String> getReadOnlyGroups() {
        return this.readOnlyGroups;
    }

    @Override
    public boolean checkPermissions(List<String> requestedGroups) {
        boolean adminEditingSelf = this.remoteUser.getName().equals(this.user.getName());
        boolean hasAdminUserPermission = this.hasUserPermissions("SYSTEMADMINISTRATOR");
        boolean hasConfluenceUserPermission = this.hasUserPermissions("USECONFLUENCE");
        if (!(!adminEditingSelf || hasAdminUserPermission && hasConfluenceUserPermission)) {
            boolean hasAdminPermission = hasAdminUserPermission;
            boolean hasUsePermission = hasConfluenceUserPermission;
            for (String remainingGroup : requestedGroups) {
                if (hasAdminPermission || this.hasGroupPermission(remainingGroup, "SYSTEMADMINISTRATOR")) {
                    hasAdminPermission = true;
                }
                if (!hasUsePermission && !this.hasGroupPermission(remainingGroup, "USECONFLUENCE")) continue;
                hasUsePermission = true;
            }
            return hasAdminPermission && hasUsePermission;
        }
        return true;
    }

    private boolean hasUserPermissions(String permission) {
        for (SpacePermission spacePermission : this.getGlobalPermissions()) {
            if (!spacePermission.getType().equals(permission) || !spacePermission.isUserPermission() || !spacePermission.getUserName().equals(this.remoteUser.getName())) continue;
            return true;
        }
        return false;
    }

    private boolean hasGroupPermission(String group, String permission) {
        for (SpacePermission spacePermission : this.getGlobalPermissions()) {
            String groupName = spacePermission.getGroup();
            if (!spacePermission.getType().equals(permission) || groupName == null || !groupName.equals(group)) continue;
            return true;
        }
        return false;
    }

    private List<SpacePermission> getGlobalPermissions() {
        if (this.globalPermissions == null) {
            this.globalPermissions = this.spacePermissionManager.getGlobalPermissions();
        }
        return this.globalPermissions;
    }

    @Override
    public boolean updateGroups(List<String> requestedGroups) {
        Group group;
        ArrayList<String> groupsToJoin = new ArrayList<String>();
        ArrayList<String> groupsToLeave = new ArrayList<String>();
        groupsToJoin.addAll(requestedGroups);
        groupsToJoin.removeAll(this.initialMemberGroups);
        for (String initialMemberGroup : this.initialMemberGroups) {
            if (requestedGroups.contains(initialMemberGroup)) continue;
            groupsToLeave.add(initialMemberGroup);
        }
        for (String groupToJoin : groupsToJoin) {
            group = this.userAccessor.getGroup(groupToJoin);
            if (group == null || this.userAccessor.hasMembership(groupToJoin, this.user.getName())) continue;
            if (!this.permissionManager.hasPermission(this.remoteUser, Permission.EDIT, group)) {
                this.errors.add(Message.getInstance("cannot.add.group.membership.permissions", group.getName()));
                continue;
            }
            this.userAccessor.addMembership(group, this.user);
            if (this.userAccessor.hasMembership(group, this.user)) continue;
            this.errors.add(Message.getInstance("cannot.add.group.membership.failed", this.user.getName(), group.getName()));
        }
        for (String groupToLeave : groupsToLeave) {
            group = this.userAccessor.getGroup(groupToLeave);
            if (group == null || !this.userAccessor.hasMembership(group.getName(), this.user.getName())) continue;
            if (!this.permissionManager.hasPermission(this.remoteUser, Permission.EDIT, group)) {
                this.errors.add(Message.getInstance("cannot.remove.group.membership.permissions", group.getName()));
                continue;
            }
            this.userAccessor.removeMembership(group, this.user);
            if (!this.userAccessor.hasMembership(group, this.user)) continue;
            this.errors.add(Message.getInstance("cannot.remove.group.membership.failed", this.user.getName(), group.getName()));
        }
        this.flush();
        return this.errors.isEmpty();
    }

    private void flush() {
        if (ContainerManager.getInstance().getContainerContext() != null) {
            try {
                SessionFactory factory = (SessionFactory)ContainerManager.getInstance().getContainerContext().getComponent((Object)"sessionFactory");
                Session session = factory.getCurrentSession();
                session.flush();
            }
            catch (ComponentNotFoundException | PersistenceException e) {
                log.error("Unable to flush session", e);
            }
        }
    }

    @Override
    public List<Message> getErrors() {
        return this.errors;
    }
}

