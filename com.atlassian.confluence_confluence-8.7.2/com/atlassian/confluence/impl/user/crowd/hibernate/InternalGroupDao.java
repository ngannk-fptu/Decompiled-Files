/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalGroup
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalGroup;

public interface InternalGroupDao<T extends InternalGroup>
extends GroupDao {
    public InternalGroup internalFindByName(long var1, String var3) throws GroupNotFoundException;

    public InternalGroup internalFindByGroup(Group var1) throws GroupNotFoundException;

    public void removeAllGroups(long var1);

    public T add(Group var1) throws DirectoryNotFoundException, InvalidGroupException;

    public T addLocal(Group var1) throws DirectoryNotFoundException, InvalidGroupException;
}

