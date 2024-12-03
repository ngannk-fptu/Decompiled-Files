/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  javax.annotation.Nonnull
 *  javax.validation.constraints.NotNull
 */
package com.atlassian.mywork.host.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.mywork.host.service.TaskOrder;
import com.atlassian.sal.usercompatibility.UserKey;
import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

@Transactional
public interface UserDao {
    public long getLastReadNotificationId(String var1);

    public void setLastReadNotificationId(String var1, Long var2);

    @NotNull
    public TaskOrder getTaskOrdering(String var1);

    public void setTaskOrdering(String var1, TaskOrder var2);

    public void delete(@Nonnull UserKey var1);

    public int deleteRemovedUsers();
}

