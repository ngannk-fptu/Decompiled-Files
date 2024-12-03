/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.user.administrators.DefaultEditUserGroupAdministrator;
import com.atlassian.confluence.user.administrators.EditUserGroupAdministrator;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class EditUserGroupsAction
extends AbstractUsersAction
implements ExternalUserManagementAware {
    private static final Logger log = LoggerFactory.getLogger(EditUserGroupsAction.class);
    private List<String> memberGroups;
    private List<String> allWritableGroups;
    private List<String> readOnlyGroups;
    private EditUserGroupAdministrator editUserGroupAdministrator;
    private List<String> newGroups;

    @Override
    public String doDefault() throws Exception {
        if (this.getUser() == null) {
            this.addActionError("no.such.user.exists", this.username);
        } else {
            this.loadLists();
        }
        return super.doDefault();
    }

    private void loadLists() {
        String timerName = Timers.getConfiguration().isEnabled() ? "Getting groups for user " + (this.getUser() == null ? "Anonymous" : this.getUser().getName()) : "loadLists";
        try (Ticker ignored = Timers.start((String)timerName);){
            this.loadMemberGroups();
            this.loadAllGroups();
        }
    }

    private void loadMemberGroups() {
        this.memberGroups = this.getEditUserGroupAdministrator().getInitialMemberGroups();
        this.readOnlyGroups = this.getEditUserGroupAdministrator().getReadOnlyGroups();
    }

    private void loadAllGroups() {
        try (Ticker ignored = Timers.start((String)"Getting all writeable groups");){
            List<Group> allGroups = this.userAccessor.getWriteableGroups();
            this.allWritableGroups = new ArrayList<String>();
            for (Group group : allGroups) {
                this.allWritableGroups.add(group.getName());
            }
            Collections.sort(this.allWritableGroups);
        }
    }

    public boolean isMember(String groupName) {
        return this.memberGroups.contains(groupName);
    }

    public String execute() throws Exception {
        if (this.getUser() == null) {
            return "redirect_to_admin";
        }
        try {
            this.loadLists();
            if (this.newGroups == null) {
                this.newGroups = new ArrayList<String>();
            }
            if (this.getEditUserGroupAdministrator().checkPermissions(this.newGroups)) {
                if (!this.getEditUserGroupAdministrator().updateGroups(this.newGroups)) {
                    for (Message error : this.getEditUserGroupAdministrator().getErrors()) {
                        this.addActionError(error.getKey(), error.getArguments());
                    }
                }
            } else {
                this.addActionError(this.getText("error.leave.admin.group"));
            }
        }
        catch (InfrastructureException e) {
            this.addActionError(this.getText("modify.group.memberships.failed"));
            log.warn("Failed to modify group memberships", (Throwable)e);
        }
        if (this.getActionErrors().isEmpty()) {
            return super.execute();
        }
        return "error";
    }

    public List<String> getMemberGroups() {
        return this.memberGroups;
    }

    public List<String> getAllWriteableGroups() {
        return this.allWritableGroups;
    }

    public void setNewGroups(List<String> newGroups) {
        this.newGroups = newGroups;
    }

    public List getReadOnlyGroups() {
        return this.readOnlyGroups;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public EditUserGroupAdministrator getEditUserGroupAdministrator() {
        if (this.editUserGroupAdministrator == null) {
            this.editUserGroupAdministrator = new DefaultEditUserGroupAdministrator(this.getUser(), this.getAuthenticatedUser(), this.userAccessor, this.permissionManager, this.spacePermissionManager);
        }
        return this.editUserGroupAdministrator;
    }

    public void setEditUserGroupAdministrator(EditUserGroupAdministrator editUserGroupAdministrator) {
        this.editUserGroupAdministrator = editUserGroupAdministrator;
    }
}

