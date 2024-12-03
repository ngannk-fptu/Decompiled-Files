/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.util.BatchResult
 */
package com.atlassian.crowd.embedded.spi;

import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.util.BatchResult;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GroupDao {
    public InternalDirectoryGroup findByName(long var1, String var3) throws GroupNotFoundException;

    public GroupWithAttributes findByNameWithAttributes(long var1, String var3) throws GroupNotFoundException;

    public Group add(Group var1) throws DirectoryNotFoundException, InvalidGroupException;

    public Group addLocal(Group var1) throws DirectoryNotFoundException, InvalidGroupException;

    public Group update(Group var1) throws GroupNotFoundException;

    public Group rename(Group var1, String var2) throws GroupNotFoundException, InvalidGroupException;

    public void storeAttributes(Group var1, Map<String, Set<String>> var2) throws GroupNotFoundException;

    public void removeAttribute(Group var1, String var2) throws GroupNotFoundException;

    public void remove(Group var1) throws GroupNotFoundException;

    public <T> List<T> search(long var1, EntityQuery<T> var3);

    public BatchResult<Group> addAll(Set<? extends Group> var1) throws DirectoryNotFoundException;

    public BatchResult<String> removeAllGroups(long var1, Set<String> var3);

    public Set<String> getAllExternalIds(long var1) throws DirectoryNotFoundException;

    public long getGroupCount(long var1) throws DirectoryNotFoundException;

    public Set<String> getLocalGroupNames(long var1) throws DirectoryNotFoundException;

    public Map<String, String> findByExternalIds(long var1, Set<String> var3);

    public Map<String, String> findExternalIdsByNames(long var1, Set<String> var3);

    public long getExternalGroupCount(long var1) throws DirectoryNotFoundException;
}

