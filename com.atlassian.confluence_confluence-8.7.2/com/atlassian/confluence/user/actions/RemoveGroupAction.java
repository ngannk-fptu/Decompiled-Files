/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.search.page.Pager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.actions.AbstractGroupAction;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class RemoveGroupAction
extends AbstractGroupAction {
    private String confirm;
    private SpaceManager spaceManager;
    private static final Logger log = LoggerFactory.getLogger(RemoveGroupAction.class);

    @Override
    public void validate() {
        super.validate();
        if (!StringUtils.isNotEmpty((CharSequence)this.name)) {
            this.addActionError(this.getText("name.empty"));
        }
        if (this.getGroup() == null) {
            this.addActionError(this.getText("group.doesnt.exist"));
        }
    }

    @Override
    public String doDefault() throws Exception {
        HashSet<String> adminGroups = new HashSet<String>();
        for (SpacePermission spacePermission : this.spacePermissionManager.getGlobalPermissions()) {
            if (!"SYSTEMADMINISTRATOR".equals(spacePermission.getType()) || !spacePermission.isGroupPermission()) continue;
            adminGroups.add(spacePermission.getGroup());
        }
        if (adminGroups.contains(this.getName()) && adminGroups.size() == 1) {
            this.addActionError(this.getText("cannot.remove.last.group.with.admin.permission"));
        }
        return "input";
    }

    public String execute() throws Exception {
        Group groupToDelete = this.getGroup();
        if (this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.REMOVE, groupToDelete)) {
            try {
                this.userAccessor.removeGroup(groupToDelete);
            }
            catch (InfrastructureException e) {
                this.addActionError("cannot.remove.group", groupToDelete.getName());
                log.error("Error removing group, check for LDAP read-only mode", (Throwable)e);
                return "input";
            }
        }
        return "success";
    }

    public List<SpacePermission> getGlobalPermissionsForThisGroup() {
        ArrayList<SpacePermission> result = new ArrayList<SpacePermission>();
        Group group = this.getGroup();
        if (group == null) {
            return result;
        }
        for (SpacePermission spacePermission : this.spacePermissionManager.getGlobalPermissions()) {
            String groupName = spacePermission.getGroup();
            if (groupName == null || !groupName.equals(group.getName())) continue;
            result.add(spacePermission);
        }
        return result;
    }

    public List<SpacePermission> getGroupSpacePermissionsForSpace(Space space) {
        ArrayList<SpacePermission> result = new ArrayList<SpacePermission>();
        Group group = this.getGroup();
        if (group == null) {
            return result;
        }
        for (SpacePermission spacePermission : space.getPermissions()) {
            String groupName = spacePermission.getGroup();
            if (groupName == null || !groupName.equals(group.getName())) continue;
            result.add(spacePermission);
        }
        return result;
    }

    public List<Space> getSpaces() {
        ListBuilder<Space> listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        ArrayList<Space> spaces = new ArrayList<Space>(listBuilder.getAvailableSize());
        for (List list : listBuilder) {
            spaces.addAll(list);
        }
        return spaces;
    }

    @Deprecated
    public Pager<String> getUsers() {
        return this.userAccessor.getMemberNames(this.getGroup());
    }

    public boolean hasMembers() {
        return !this.userAccessor.getMemberNames(this.getGroup()).isEmpty();
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

