/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.manager.permission.GroupGroupAdministrationMapping;
import com.atlassian.crowd.manager.permission.UserGroupAdministrationMapping;
import java.util.Set;

public interface SingleGroupAdministrationMappings {
    public Set<GroupGroupAdministrationMapping> getGroupMappings();

    public Set<UserGroupAdministrationMapping> getUserMappings();
}

