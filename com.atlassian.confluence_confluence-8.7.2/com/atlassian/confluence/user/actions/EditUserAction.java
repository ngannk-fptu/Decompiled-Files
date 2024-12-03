/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.exception.runtime.CrowdRuntimeException
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.apache.struts2.interceptor.ParameterAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.user.UserFormValidator;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.user.actions.UserDetailsMap;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class EditUserAction
extends AbstractUsersAction
implements FormAware,
ParameterAware {
    private static final Logger log = LoggerFactory.getLogger(EditUserAction.class);
    private static final Set<DirectoryType> USER_RENAME_DIRECTORY_TYPES = ImmutableSet.of((Object)DirectoryType.INTERNAL, (Object)DirectoryType.DELEGATING);
    private UserFormValidator validator;
    private String fullName;
    private String email;
    private String personalInformation;
    private UserDetailsMap userDetailsMap = null;
    private UserDetailsManager userDetailsManager;
    private PersonalInformationManager personalInformationManager;
    private CrowdService crowdService;
    private CrowdDirectoryService crowdDirectoryService;

    public String getFullName() {
        if (this.fullName == null && this.getUser() != null) {
            this.fullName = this.getUser().getFullName();
        }
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        if (this.email == null && this.getUser() != null) {
            this.email = this.getUser().getEmail();
        }
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void validate() {
        if (this.settingsManager.getGlobalSettings().isExternalUserManagement() || this.userAccessor.isReadOnly(this.getUser())) {
            return;
        }
        UserForm form = new UserForm(this.getUser().getKey(), this.username, this.fullName, this.email);
        this.validator.validateEditUserAllowRename(form, this.messageHolder);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public String doEdit() throws Exception {
        try {
            if (this.canRename() && !IdentifierUtils.equalsInLowerCase((String)this.getUser().getName(), (String)this.username)) {
                this.user = this.userAccessor.renameUser(this.getUser(), this.username);
            }
            boolean shouldUpdatePersonalInfo = this.shouldUpdatePersonalInfo(this.getUser(), this.fullName, this.getPersonalInformation());
            if (this.canUpdate()) {
                DefaultUser userTemplate = new DefaultUser((com.atlassian.user.User)this.getUser());
                userTemplate.setFullName(this.fullName);
                userTemplate.setEmail(this.email);
                this.userAccessor.saveUser((com.atlassian.user.User)userTemplate);
            }
            this.getUserDetailsMap().copyPropertiesToManager();
            if (shouldUpdatePersonalInfo) {
                this.personalInformationManager.savePersonalInformation(this.getUser(), this.getPersonalInformation(), this.getFullName());
            }
        }
        catch (CrowdRuntimeException e) {
            String rootCause = ExceptionUtils.getRootCauseMessage((Throwable)e);
            this.addActionError(this.getText("edit.user.failed"));
            log.warn("Failed to update user profile. Cause: {}", (Object)rootCause);
            return "error";
        }
        return "success";
    }

    public boolean canRename() {
        User crowdUser = this.crowdService.getUser(this.getUser().getName());
        DirectoryType directoryType = this.crowdDirectoryService.findDirectoryById(crowdUser.getDirectoryId()).getType();
        return this.canUpdate() && USER_RENAME_DIRECTORY_TYPES.contains(directoryType);
    }

    public boolean canUpdate() {
        return !this.settingsManager.getGlobalSettings().isExternalUserManagement() && !this.userAccessor.isReadOnly(this.getUser());
    }

    private boolean shouldUpdatePersonalInfo(com.atlassian.user.User user, String fullName, String newInfo) {
        PersonalInformation oldInfo = this.getPersonalInformationEntity();
        return oldInfo == null || !newInfo.equals(oldInfo.getBodyContent().getBody()) || this.hasFullNameChanged(user, fullName);
    }

    private boolean hasFullNameChanged(com.atlassian.user.User user, String fullName) {
        return fullName != null && !fullName.trim().equals(user.getFullName());
    }

    public String getUserProperty(String key) {
        return this.getUserDetailsMap().getProperty(key);
    }

    public List<String> getUserDetailsKeys(String groupKey) {
        return this.userDetailsManager.getProfileKeys(groupKey);
    }

    public List<String> getUserDetailsGroups() {
        return this.userDetailsManager.getProfileGroups();
    }

    private PersonalInformation getPersonalInformationEntity() {
        return this.personalInformationManager.getOrCreatePersonalInformation(this.getUser());
    }

    public void setUserDetailsManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((com.atlassian.user.User)this.getAuthenticatedUser(), Permission.SET_PERMISSIONS, this.getUser());
    }

    @Override
    public boolean isEditMode() {
        return true;
    }

    public String getPersonalInformation() {
        if (this.personalInformation == null) {
            PersonalInformation infoObj = this.getPersonalInformationEntity();
            this.personalInformation = infoObj.getBodyContent().getBody();
        }
        return this.personalInformation;
    }

    public void setPersonalInformation(String personalInformation) {
        this.personalInformation = personalInformation;
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public void setCrowdService(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public void setCrowdDirectoryService(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public void setParameters(Map map) {
        this.getUserDetailsMap().setParameters(map);
    }

    public UserDetailsMap getUserDetailsMap() {
        if (this.userDetailsMap == null) {
            this.userDetailsMap = new UserDetailsMap(this.getUser(), this.userDetailsManager);
        }
        return this.userDetailsMap;
    }

    public void setUserFormValidator(UserFormValidator validator) {
        this.validator = validator;
    }
}

