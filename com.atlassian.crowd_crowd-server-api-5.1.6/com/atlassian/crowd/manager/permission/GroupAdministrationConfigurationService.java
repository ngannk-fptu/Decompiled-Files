/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.FeatureInaccessibleException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.exception.FeatureInaccessibleException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.permission.GroupGroupAdministrationMapping;
import com.atlassian.crowd.manager.permission.SingleGroupAdministrationMappings;
import com.atlassian.crowd.manager.permission.UserGroupAdministrationMapping;
import com.atlassian.crowd.model.group.Group;

public interface GroupAdministrationConfigurationService {
    public void grantGroupPermissions(GroupGroupAdministrationMapping var1) throws GroupNotFoundException, FeatureInaccessibleException;

    public void grantGroupPermissions(UserGroupAdministrationMapping var1) throws UserNotFoundException, GroupNotFoundException, FeatureInaccessibleException;

    public void revokeGroupPermissions(GroupGroupAdministrationMapping var1) throws GroupNotFoundException, FeatureInaccessibleException;

    public void revokeGroupPermissions(UserGroupAdministrationMapping var1) throws UserNotFoundException, GroupNotFoundException, FeatureInaccessibleException;

    public SingleGroupAdministrationMappings getGroupAdministrators(Group var1) throws GroupNotFoundException, FeatureInaccessibleException;
}

