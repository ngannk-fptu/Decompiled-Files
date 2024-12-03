/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.opensymphony.xwork2.Preparable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UsernameToUserTranslatingPaginationSupport;
import com.atlassian.confluence.user.actions.AbstractEntityPaginationAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class ViewMembersOfGroupAction
extends AbstractEntityPaginationAction<User>
implements Preparable {
    private static final Logger log = LoggerFactory.getLogger(ViewMembersOfGroupAction.class);
    private static final int USERS_PER_PAGE = 50;
    private String membersOfGroupTerm;
    private String usersToAdd;
    private String username;

    public ViewMembersOfGroupAction() {
        this.paginationSupport = new UsernameToUserTranslatingPaginationSupport(50, 0);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String removeFromGroup() {
        Group group = this.getGroup();
        if (group == null) {
            this.addActionError("group.could.not.be.found", this.membersOfGroupTerm);
            return "error";
        }
        boolean adminEditingSelf = this.getAuthenticatedUser().getName().equals(this.getUsername());
        if (adminEditingSelf && (this.isLastGroup(this.membersOfGroupTerm, "SYSTEMADMINISTRATOR") || this.isLastGroup(this.membersOfGroupTerm, "USECONFLUENCE"))) {
            this.addActionError(this.getText("error.leave.admin.group"));
            return "error";
        }
        if (!this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, group)) {
            this.addActionError("cannot.remove.group.membership.permissions", this.getUserByName(this.username).getName(), group.getName());
            return "error";
        }
        ConfluenceUser user = this.getUserByName(this.username);
        if (user == null && (user = this.getUserByName(this.getDecodedUsername())) == null) {
            this.addActionError("user.could.not.be.found", this.username);
            return "error";
        }
        log.debug("Removing user '{}' from '{}'", (Object)user.getName(), (Object)group.getName());
        try {
            this.userAccessor.removeMembership(group, user);
        }
        catch (InfrastructureException e) {
            log.error("Failed to remove user " + user.getName() + " from group " + group.getName() + " membership", (Throwable)e);
        }
        if (this.userAccessor.hasMembership(group, user)) {
            this.addActionError("cannot.remove.group.membership.failed", this.getUserByName(this.username).getName(), group.getName());
            return "error";
        }
        return "success";
    }

    public String addToGroup() {
        String[] userNames = GeneralUtil.splitCommaDelimitedString(this.usersToAdd);
        Group group = this.getGroup();
        ArrayList<String> invalidUsers = new ArrayList<String>();
        if (group == null) {
            this.addActionError("group.could.not.be.found", this.membersOfGroupTerm);
            return "error";
        }
        if (!this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, group)) {
            this.addActionError("cannot.add.group.membership.permissions", group.getName());
            return "error";
        }
        for (String userName : userNames) {
            ConfluenceUser user = this.userAccessor.getUserByName(userName = userName.trim());
            if (user != null) {
                try {
                    this.userAccessor.addMembership(group, user);
                }
                catch (InfrastructureException e) {
                    log.error("Failed to add user " + user.getName() + " to group " + group.getName() + " membership", (Throwable)e);
                }
                if (this.userAccessor.hasMembership(group, user)) continue;
                this.addActionError("cannot.add.group.membership.failed", user.getName(), group.getName());
                return "error";
            }
            invalidUsers.add(userName);
        }
        if (invalidUsers.size() > 0) {
            this.handleErrorsInAddition(invalidUsers, this.getText("user.could.not.be.found"));
            return "error";
        }
        return "success";
    }

    private Group getGroup() {
        Group group = this.userAccessor.getGroup(this.membersOfGroupTerm);
        if (group == null) {
            group = this.userAccessor.getGroup(this.getDecodedMembersOfGroupTerm());
        }
        return group;
    }

    private boolean isLastGroup(String group, String permission) {
        ArrayList<String> permissionGroups = new ArrayList<String>();
        for (SpacePermission spacePermission : this.spacePermissionManager.getGlobalPermissions()) {
            if (!spacePermission.getType().equals(permission) || !spacePermission.isGroupPermission() || !this.userAccessor.hasMembership(spacePermission.getGroup(), this.getAuthenticatedUser().getName())) continue;
            permissionGroups.add(spacePermission.getGroup());
        }
        return permissionGroups.contains(group) && permissionGroups.size() == 1;
    }

    private void handleErrorsInAddition(List<String> errorList, String key) {
        if (!errorList.isEmpty()) {
            for (String entry : errorList) {
                this.addActionError(this.getText(key, new Object[]{HtmlUtil.htmlEncode(entry)}));
            }
        }
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String getMembersOfGroupTerm() {
        return this.membersOfGroupTerm;
    }

    public void setMembersOfGroupTerm(String membersOfGroupTerm) {
        this.membersOfGroupTerm = membersOfGroupTerm;
    }

    public String getDecodedMembersOfGroupTerm() {
        return HtmlUtil.urlDecode(this.membersOfGroupTerm);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDecodedUsername() {
        return HtmlUtil.urlDecode(this.username);
    }

    public void setUsersToAdd(String usersToAdd) {
        this.usersToAdd = usersToAdd;
    }

    public boolean isCanEditGroup() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.getGroup());
    }

    public void prepare() throws Exception {
        Group group = this.getGroup();
        if (group != null) {
            List<String> memberNames = this.userAccessor.getMemberNamesAsList(group);
            UsernameToUserTranslatingPaginationSupport usernameToUserPaginationSupport = (UsernameToUserTranslatingPaginationSupport)this.paginationSupport;
            usernameToUserPaginationSupport.setUserAccessor(this.userAccessor);
            this.paginationSupport.setItems(memberNames);
        } else {
            this.paginationSupport.setItems(Collections.emptyList());
        }
    }

    public String getUrlEncodeMembersOfGroupTerm() {
        return HtmlUtil.urlEncode(HtmlUtil.urlEncode(this.getDecodedMembersOfGroupTerm()));
    }
}

