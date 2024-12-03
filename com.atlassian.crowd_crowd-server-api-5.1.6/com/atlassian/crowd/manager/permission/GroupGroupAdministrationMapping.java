/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.model.group.Group;

public interface GroupGroupAdministrationMapping {
    public Group getTargetGroup();

    public Group getAdminGroup();
}

