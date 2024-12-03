/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import java.util.Date;
import javax.annotation.Nullable;

public interface GroupActionStrategy {
    public DirectoryCacheChangeOperations.GroupsToAddUpdateReplace decide(@Nullable InternalDirectoryGroup var1, @Nullable InternalDirectoryGroup var2, Group var3, Date var4, long var5) throws OperationFailedException;
}

