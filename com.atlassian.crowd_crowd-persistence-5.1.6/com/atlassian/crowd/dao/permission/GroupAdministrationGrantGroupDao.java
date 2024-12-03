/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.dao.permission;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.permission.GroupAdministrationGrantToGroup;
import java.util.Collection;
import java.util.List;

public interface GroupAdministrationGrantGroupDao {
    public GroupAdministrationGrantToGroup add(GroupAdministrationGrantToGroup var1);

    public List<GroupAdministrationGrantToGroup> findGrantsToGroup(InternalGroup var1);

    public List<GroupAdministrationGrantToGroup> findGrantsOfGroups(Collection<InternalGroup> var1);

    public List<GroupAdministrationGrantToGroup> findGrantsToGroupFromDirectory(InternalGroup var1, Directory var2);

    public List<GroupAdministrationGrantToGroup> findAll();

    public void remove(InternalGroup var1, InternalGroup var2);
}

