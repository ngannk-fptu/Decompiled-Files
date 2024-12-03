/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.permission;

import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.permission.UserAdministrationGrantToGroup;
import com.atlassian.crowd.model.user.InternalUser;
import java.util.List;
import java.util.Optional;

public interface UserAdministrationGrantGroupDao {
    public UserAdministrationGrantToGroup add(UserAdministrationGrantToGroup var1);

    public List<UserAdministrationGrantToGroup> findGrantsToGroup(InternalGroup var1);

    public List<UserAdministrationGrantToGroup> findUserGrants(InternalUser var1);

    public Optional<UserAdministrationGrantToGroup> findUserGrantForGroup(InternalUser var1, InternalGroup var2);

    public List<UserAdministrationGrantToGroup> findAll();

    public void remove(InternalUser var1, InternalGroup var2);
}

