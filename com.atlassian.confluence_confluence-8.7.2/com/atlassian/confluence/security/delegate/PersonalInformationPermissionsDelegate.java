/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.delegate.UserPermissionsDelegate;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.user.User;

public class PersonalInformationPermissionsDelegate
implements PermissionDelegate<PersonalInformation> {
    private UserPermissionsDelegate userPermissionsDelegate;

    @Override
    public boolean canView(User user, PersonalInformation target) {
        return this.userPermissionsDelegate.canView(user, this.getUser(target));
    }

    @Override
    public boolean canView(User user) {
        return this.userPermissionsDelegate.canView(user);
    }

    @Override
    public boolean canEdit(User user, PersonalInformation target) {
        return this.userPermissionsDelegate.canEdit(user, this.getUser(target));
    }

    @Override
    public boolean canSetPermissions(User user, PersonalInformation target) {
        return this.userPermissionsDelegate.canSetPermissions(user, this.getUser(target));
    }

    @Override
    public boolean canRemove(User user, PersonalInformation target) {
        return this.userPermissionsDelegate.canRemove(user, this.getUser(target));
    }

    @Override
    public boolean canExport(User user, PersonalInformation target) {
        return this.userPermissionsDelegate.canExport(user, this.getUser(target));
    }

    @Override
    public boolean canAdminister(User user, PersonalInformation target) {
        return this.userPermissionsDelegate.canAdminister(user, this.getUser(target));
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.userPermissionsDelegate.canCreate(user, PermissionManager.TARGET_APPLICATION);
    }

    @Override
    public boolean canCreateInTarget(User user, Class typeToCreate) {
        return this.userPermissionsDelegate.canCreateInTarget(user, typeToCreate);
    }

    private ConfluenceUser getUser(PersonalInformation personalInfo) {
        return personalInfo.getUser();
    }

    public void setUserPermissionsDelegate(UserPermissionsDelegate userPermissionsDelegate) {
        this.userPermissionsDelegate = userPermissionsDelegate;
    }
}

