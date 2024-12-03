/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.model.group.Group;

public interface UserGroupAdministrationMapping {
    public User getUser();

    public Group getTargetGroup();
}

