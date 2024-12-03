/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.model.group.Group
 */
package com.atlassian.crowd.dao.group;

import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.group.InternalGroupAttribute;
import com.atlassian.crowd.model.group.InternalGroupWithAttributes;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchResultWithIdReferences;
import java.util.Collection;
import java.util.Set;

public interface InternalGroupDao
extends GroupDao {
    public InternalGroup findByName(long var1, String var3) throws GroupNotFoundException;

    public Set<InternalGroupAttribute> findGroupAttributes(long var1);

    public void removeAll(long var1);

    public BatchResultWithIdReferences<Group> addAll(Collection<InternalGroupWithAttributes> var1);

    public Collection<InternalGroup> findByNames(long var1, Collection<String> var3);

    public Collection<InternalGroup> findByIds(Collection<Long> var1);
}

