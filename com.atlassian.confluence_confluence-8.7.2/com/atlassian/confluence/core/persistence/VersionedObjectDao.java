/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.core.bean.EntityObject
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core.persistence;

import bucket.core.persistence.ObjectDao;
import com.atlassian.core.bean.EntityObject;
import java.util.Iterator;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface VersionedObjectDao<T extends EntityObject>
extends ObjectDao {
    @Deprecated
    @Transactional
    public void save(EntityObject var1, EntityObject var2);

    @Deprecated(forRemoval=true)
    public Iterator<T> findLatestVersionsIterator();

    @Deprecated(forRemoval=true)
    public long findLatestVersionsCount();
}

