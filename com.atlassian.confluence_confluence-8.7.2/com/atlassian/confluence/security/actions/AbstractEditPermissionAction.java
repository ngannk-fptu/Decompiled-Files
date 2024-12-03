/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.security.PermissionUtils;
import com.atlassian.confluence.security.actions.AbstractPermissionsAction;
import com.atlassian.confluence.security.actions.EditPermissionsAware;
import com.atlassian.confluence.security.administrators.EditPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.util.HtmlUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEditPermissionAction
extends AbstractPermissionsAction
implements EditPermissionsAware {
    protected String selectedUsername;
    protected String selectedGroup;
    private String usersToAdd;
    private List<String> groupListToAdd;
    private String groupsToAdd;
    protected EditPermissionsAdministrator permissionsAdministrator;

    @Override
    public PermissionsAdministrator getPermissionsAdministrator() {
        return this.permissionsAdministrator;
    }

    @Override
    public void validate() {
        super.validate();
        if (this.permissionsAdministrator.isGroupsToAddEmpty(this.getRequestParams())) {
            this.addFieldError("groupsToAdd", this.getText("groups.to.add.empty"));
        }
        if (this.permissionsAdministrator.isGroupsToAddTooLarge(this.getRequestParams())) {
            this.addFieldError("groupsToAdd", "groups.to.add.too.many.entries", new Object[]{this.permissionsAdministrator.getNumOfGroupEntries(), EditPermissionsAdministrator.MAX_ENTRIES});
        }
        if (this.permissionsAdministrator.isUsersToAddEmpty(this.getRequestParams())) {
            this.addFieldError("usersToAdd", this.getText("users.to.add.empty"));
        }
        if (this.permissionsAdministrator.isUsersToAddTooLarge(this.getRequestParams())) {
            this.addFieldError("usersToAdd", "users.to.add.too.many.entries", new Object[]{this.permissionsAdministrator.getNumOfUserEntries(), EditPermissionsAdministrator.MAX_ENTRIES});
        }
    }

    protected String executeAction(String errorMessageKey) {
        try {
            Map requestParams = this.getRequestParams();
            this.permissionsAdministrator.applyPermissionChanges(this.permissionsAdministrator.getInitialPermissionsFromForm(requestParams), this.permissionsAdministrator.getRequestedPermissionsFromForm(requestParams));
        }
        catch (IllegalArgumentException e) {
            this.addActionError(this.getText(errorMessageKey));
            return "error";
        }
        List<String> groupNamesToBeAdded = this.getGroupsToAddAsList();
        if (!groupNamesToBeAdded.isEmpty()) {
            List<String> groupsThatCouldNotBeAdded = this.permissionsAdministrator.addGuardPermissionToGroups(groupNamesToBeAdded, this.getGuardPermission());
            return this.handleErrorsInAddition(groupsThatCouldNotBeAdded, "groupsToAdd", "group.could.not.be.found");
        }
        List<String> userNamesToBeAdded = this.getUsersToAddAsList();
        if (!userNamesToBeAdded.isEmpty()) {
            List<String> usersThatCouldNotBeAdded = this.permissionsAdministrator.addGuardPermissionToUsers(userNamesToBeAdded, this.getGuardPermission());
            return this.handleErrorsInAddition(usersThatCouldNotBeAdded, "usersToAdd", "user.could.not.be.found");
        }
        return "success";
    }

    private String handleErrorsInAddition(List<String> errorList, String fieldName, String key) {
        if (!errorList.isEmpty()) {
            for (String entry : errorList) {
                this.addFieldError(fieldName, this.getText(key, new Object[]{HtmlUtil.htmlEncode(entry)}));
            }
        }
        return this.getAdjustReturn();
    }

    @Override
    public List<String> getUsersToAddAsList() {
        return PermissionUtils.convertCommaDelimitedStringToList(this.usersToAdd);
    }

    private List<String> getNonEmptyElements(List<String> array) {
        return array == null ? Collections.emptyList() : array.stream().filter(g -> !StringUtils.isEmpty((CharSequence)g)).collect(Collectors.toList());
    }

    @Override
    public void setUsersToAdd(String usersToAdd) {
        this.usersToAdd = usersToAdd;
    }

    @Override
    public List<String> getGroupsToAddAsList() {
        return this.groupsToAdd != null ? PermissionUtils.convertCommaDelimitedStringToList(this.groupsToAdd) : this.getNonEmptyElements(this.groupListToAdd);
    }

    @Override
    public void setGroupsToAdd(String groupsToAdd) {
        this.groupsToAdd = groupsToAdd;
    }

    @Override
    public void setGroupListToAdd(List<String> groupListToAdd) {
        this.groupListToAdd = groupListToAdd;
    }

    @Override
    public String getSelectedUsername() {
        return this.selectedUsername;
    }

    @Override
    public void setSelectedUsername(String selectedUsername) {
        this.selectedUsername = selectedUsername;
    }

    @Override
    public String getSelectedGroup() {
        return this.selectedGroup;
    }

    @Override
    public void setSelectedGroup(String selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    private String getAdjustReturn() {
        if (this.getFieldErrors().isEmpty()) {
            return "adjust";
        }
        return "input";
    }
}

